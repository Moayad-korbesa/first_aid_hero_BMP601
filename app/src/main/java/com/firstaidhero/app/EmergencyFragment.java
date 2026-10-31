package com.firstaidhero.app;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firstaidhero.app.adapter.EmergencyNumberAdapter;
import com.firstaidhero.app.database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EmergencyFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerGeneral, recyclerCustom;
    private EmergencyNumberAdapter generalAdapter, customAdapter;
    private List<String[]> generalList, customList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        try {
            dbHelper.openDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recyclerGeneral = view.findViewById(R.id.recyclerViewGeneralNumbers);
        recyclerCustom = view.findViewById(R.id.recyclerViewCustomNumbers);
        recyclerGeneral.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerCustom.setLayoutManager(new LinearLayoutManager(requireContext()));

        generalList = new ArrayList<>();
        generalList.add(new String[]{"الإسعاف", "110"});
        generalList.add(new String[]{"الإطفاء والدفاع المدني", "112"});
        generalList.add(new String[]{"الشرطة والنجدة", "112"});
        generalAdapter = new EmergencyNumberAdapter(requireContext(), generalList);
        recyclerGeneral.setAdapter(generalAdapter);

        customList = new ArrayList<>();
        customAdapter = new EmergencyNumberAdapter(requireContext(), customList);
        recyclerCustom.setAdapter(customAdapter);
        loadCustomContacts();

        MaterialButton btnAddNumber = view.findViewById(R.id.btnAddNumber);
        btnAddNumber.setOnClickListener(v -> showAddContactDialog());

        return view;
    }

    private void loadCustomContacts() {
        customList.clear();
        Cursor cursor = dbHelper.getAllCustomContacts();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("contact_name"));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone_number"));
                customList.add(new String[]{name, phone});
            } while (cursor.moveToNext());
            cursor.close();
        }
        customAdapter.notifyDataSetChanged();
    }

    private void showAddContactDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_contact);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    (int)(getResources().getDisplayMetrics().widthPixels * 0.85),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        EditText etName = dialog.findViewById(R.id.etContactName);
        EditText etNumber = dialog.findViewById(R.id.etContactNumber);
        Button btnSave = dialog.findViewById(R.id.btnSave);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String number = etNumber.getText().toString().trim();
            if (name.isEmpty() || number.isEmpty()) {
                Toast.makeText(requireContext(), "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show();
                return;
            }
            long result = dbHelper.addCustomContact(name, number);
            if (result != -1) {
                Toast.makeText(requireContext(), "تم إضافة الرقم بنجاح", Toast.LENGTH_SHORT).show();
                loadCustomContacts();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) dbHelper.closeDatabase();
    }
}