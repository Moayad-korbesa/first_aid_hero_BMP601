package com.firstaidhero.app;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firstaidhero.app.adapter.StepAdapter;
import com.firstaidhero.app.database.DatabaseHelper;
import com.firstaidhero.app.model.Step;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProtocolActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView tvCaseTitle, tvDangerLevel, tvSymptoms;
    private RecyclerView recyclerViewSteps;
    private StepAdapter stepAdapter;
    private List<Step> stepList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);

        // زر الرجوع
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // تهيئة قاعدة البيانات
        dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.openDatabase();
        } catch (IOException e) {
            Log.e("ProtocolActivity", "خطأ في فتح قاعدة البيانات", e);
        }

        // ربط العناصر
        tvCaseTitle = findViewById(R.id.tvCaseTitle);
        tvDangerLevel = findViewById(R.id.tvDangerLevel);
        tvSymptoms = findViewById(R.id.tvSymptoms);
        recyclerViewSteps = findViewById(R.id.recyclerViewSteps);
        recyclerViewSteps.setLayoutManager(new LinearLayoutManager(this));

        int caseId = getIntent().getIntExtra("case_id", -1);
        String title = getIntent().getStringExtra("title");
        String dangerLevel = getIntent().getStringExtra("danger_level");
        String symptoms = getIntent().getStringExtra("symptoms");

        if (title == null) title = "حالة غير معروفة";
        if (dangerLevel == null) dangerLevel = "غير محدد";
        if (symptoms == null) symptoms = "لا توجد أعراض مسجلة";

        tvCaseTitle.setText(title);
        tvDangerLevel.setText(dangerLevel);
        tvSymptoms.setText(symptoms);

        if (dangerLevel.equals("حرجة")) {
            tvDangerLevel.setBackgroundColor(getColor(R.color.red_emergency));
        } else if (dangerLevel.equals("متوسطة")) {
            tvDangerLevel.setBackgroundColor(getColor(R.color.orange_warning));
        } else {
            tvDangerLevel.setBackgroundColor(getColor(R.color.green_success));
        }
        tvCaseTitle.setText(title);
        tvDangerLevel.setText(dangerLevel);
        tvSymptoms.setText(symptoms);

        if (dangerLevel.equals("حرجة")) {
            tvDangerLevel.setBackgroundColor(getColor(R.color.red_emergency));
        } else if (dangerLevel.equals("متوسطة")) {
            tvDangerLevel.setBackgroundColor(getColor(R.color.orange_warning));
        } else {
            tvDangerLevel.setBackgroundColor(getColor(R.color.green_success));
        }

        stepList = new ArrayList<>();
        stepAdapter = new StepAdapter(stepList);
        recyclerViewSteps.setAdapter(stepAdapter);

        loadSteps(caseId);

        com.google.android.material.button.MaterialButton btnEmergencyCall = findViewById(R.id.btnEmergencyCall);
        if (dangerLevel.equals("حرجة") || dangerLevel.equals("متوسطة")) {
            btnEmergencyCall.setVisibility(View.VISIBLE);
            btnEmergencyCall.setOnClickListener(v -> {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:110"));
                try {
                    startActivity(callIntent);
                } catch (Exception e) {
                    Toast.makeText(this, "لا يوجد تطبيق هاتف متاح", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadSteps(int caseId) {
        Cursor cursor = dbHelper.getStepsByCaseId(caseId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int stepId = cursor.getInt(cursor.getColumnIndexOrThrow("step_id"));
                int stepNumber = cursor.getInt(cursor.getColumnIndexOrThrow("step_number"));
                String instruction = cursor.getString(cursor.getColumnIndexOrThrow("instruction_text"));

                stepList.add(new Step(stepId, caseId, stepNumber, instruction));
            } while (cursor.moveToNext());
            cursor.close();
        }
        stepAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.closeDatabase();
        }
    }
}