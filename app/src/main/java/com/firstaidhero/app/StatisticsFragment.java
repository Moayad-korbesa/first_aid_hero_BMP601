package com.firstaidhero.app;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.firstaidhero.app.database.DatabaseHelper;

import java.io.IOException;

public class StatisticsFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private TextView tvTotal, tvCritical, tvModerate, tvMild, tvTreated;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        try {
            dbHelper.openDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        tvTotal = view.findViewById(R.id.tvTotalEmergencies);
        tvCritical = view.findViewById(R.id.tvCriticalCases);
        tvModerate = view.findViewById(R.id.tvModerateCases);
        tvMild = view.findViewById(R.id.tvMildCases);
        tvTreated = view.findViewById(R.id.tvTreatedCases);

        loadStatistics();

        return view;
    }

    private void loadStatistics() {
        int total = 0, critical = 0, moderate = 0, mild = 0;

        Cursor statsCursor = dbHelper.getEmergencyStats();
        if (statsCursor != null) {
            while (statsCursor.moveToNext()) {
                String level = statsCursor.getString(statsCursor.getColumnIndexOrThrow("danger_level"));
                int count = statsCursor.getInt(statsCursor.getColumnIndexOrThrow("count"));
                total += count;
                if (level.equals("حرجة")) critical = count;
                else if (level.equals("متوسطة")) moderate = count;
                else if (level.equals("خفيفة")) mild = count;
            }
            statsCursor.close();
        }

        Cursor treatedCursor = dbHelper.getTreatedCasesCount();
        int treatedCount = 0;
        if (treatedCursor != null && treatedCursor.moveToFirst()) {
            treatedCount = treatedCursor.getInt(0);
            treatedCursor.close();
        }

        tvTotal.setText(String.valueOf(total));
        tvCritical.setText(String.valueOf(critical));
        tvModerate.setText(String.valueOf(moderate));
        tvMild.setText(String.valueOf(mild));
        tvTreated.setText(String.valueOf(treatedCount));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) dbHelper.closeDatabase();
    }
}