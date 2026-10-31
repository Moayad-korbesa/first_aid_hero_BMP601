package com.firstaidhero.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firstaidhero.app.adapter.EmergencyAdapter;
import com.firstaidhero.app.database.DatabaseHelper;
import com.firstaidhero.app.model.Emergency;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText etSearch;
    private RecyclerView recyclerView;
    private EmergencyAdapter adapter;
    private List<Emergency> emergencyList;
    private TextView tvNoResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        // زر الرجوع
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // تهيئة قاعدة البيانات
        dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.openDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ربط العناصر
        etSearch = findViewById(R.id.etSearch);
        recyclerView = findViewById(R.id.recyclerViewSearchResults);
        tvNoResults = findViewById(R.id.tvNoResults);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        emergencyList = new ArrayList<>();
        adapter = new EmergencyAdapter(emergencyList, emergency -> {
            Intent intent = new Intent(SearchResultsActivity.this, ProtocolActivity.class);
            intent.putExtra("case_id", emergency.getId());
            intent.putExtra("title", emergency.getTitle());
            intent.putExtra("danger_level", emergency.getDangerLevel());
            intent.putExtra("symptoms", emergency.getSymptoms());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // زر مسح البحث
        ImageView btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(v -> {
            etSearch.setText("");
            etSearch.requestFocus();
        });

        // البحث الفوري أثناء الكتابة
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // جلب الكلمة المفتاحية من Intent (إن وجدت)
        String keyword = getIntent().getStringExtra("keyword");
        if (keyword != null && !keyword.isEmpty()) {
            etSearch.setText(keyword);
        }

        etSearch.requestFocus();
    }

    private void performSearch(String keyword) {
        emergencyList.clear();

        if (keyword.isEmpty()) {
            tvNoResults.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
            return;
        }

        Cursor cursor = dbHelper.searchEmergencies(keyword);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("case_id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String dangerLevel = cursor.getString(cursor.getColumnIndexOrThrow("danger_level"));
                String symptoms = cursor.getString(cursor.getColumnIndexOrThrow("symptoms"));

                emergencyList.add(new Emergency(id, title, dangerLevel, symptoms));
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (emergencyList.isEmpty()) {
            tvNoResults.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoResults.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.closeDatabase();
        }
    }
}