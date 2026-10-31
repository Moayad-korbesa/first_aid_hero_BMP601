package com.firstaidhero.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firstaidhero.app.adapter.TreatedCaseAdapter;
import com.firstaidhero.app.database.DatabaseHelper;
import com.firstaidhero.app.model.TreatedCase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CasesLogFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private TreatedCaseAdapter adapter;
    private List<TreatedCase> caseList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cases_log, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        try {
            dbHelper.openDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recyclerView = view.findViewById(R.id.recyclerViewCases);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        caseList = new ArrayList<>();
        adapter = new TreatedCaseAdapter(caseList);
        recyclerView.setAdapter(adapter);

        loadCases();

        FloatingActionButton fabAddCase = view.findViewById(R.id.fabAddCase);
        fabAddCase.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddCaseActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadCases() {
        caseList.clear();
        Cursor cursor = dbHelper.getAllTreatedCases();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("case_log_id"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String photo = cursor.getString(cursor.getColumnIndexOrThrow("photo"));
                String proc = cursor.getString(cursor.getColumnIndexOrThrow("procedures"));
                caseList.add(new TreatedCase(id, desc, date, photo, proc));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCases();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) dbHelper.closeDatabase();
    }
}