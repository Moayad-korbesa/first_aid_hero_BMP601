package com.firstaidhero.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firstaidhero.app.adapter.EmergencyAdapter;
import com.firstaidhero.app.database.DatabaseHelper;
import com.firstaidhero.app.model.Emergency;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private EmergencyAdapter adapter;
    private List<Emergency> emergencyList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        try {
            dbHelper.openDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recyclerView = view.findViewById(R.id.recyclerViewEmergencies);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        emergencyList = new ArrayList<>();
        adapter = new EmergencyAdapter(emergencyList, emergency -> {
            Intent intent = new Intent(requireContext(), ProtocolActivity.class);
            intent.putExtra("case_id", emergency.getId());
            intent.putExtra("title", emergency.getTitle());
            intent.putExtra("danger_level", emergency.getDangerLevel());
            intent.putExtra("symptoms", emergency.getSymptoms());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        loadEmergencies();

        // زر الطوارئ
        view.findViewById(R.id.btnEmergencyCall).setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:110"));
            try {
                startActivity(callIntent);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "لا يوجد تطبيق هاتف متاح", Toast.LENGTH_SHORT).show();
            }
        });

        // شريط البحث
        view.findViewById(R.id.searchBar).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SearchResultsActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadEmergencies() {
        Cursor cursor = dbHelper.getAllEmergencies();
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
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) dbHelper.closeDatabase();
    }
}