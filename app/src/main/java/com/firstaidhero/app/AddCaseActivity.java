package com.firstaidhero.app;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.firstaidhero.app.database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddCaseActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String savedPhotoPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_case);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.openDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextInputEditText etDesc = findViewById(R.id.etDescription);
        TextInputEditText etDate = findViewById(R.id.etDate);
        TextInputEditText etProc = findViewById(R.id.etProcedures);
        MaterialButton btnSave = findViewById(R.id.btnSaveCase);
        MaterialButton btnSelectPhoto = findViewById(R.id.btnSelectPhoto);
        TextView tvSelectedPhotoName = findViewById(R.id.tvSelectedPhotoName);

        etDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("اختر تاريخ الحالة")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                etDate.setText(sdf.format(new Date(selection)));
            });
            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });

        ActivityResultLauncher<Void> captureImageLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(),
                bitmap -> {
                    if (bitmap != null) {
                        savedPhotoPath = saveBitmapToFile(bitmap);
                        if (!savedPhotoPath.isEmpty()) {
                            tvSelectedPhotoName.setText("✅ تم التقاط الصورة بالكاميرا بنجاح");
                            tvSelectedPhotoName.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(this, "فشل حفظ الصورة", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        ActivityResultLauncher<String> pickMediaLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        savedPhotoPath = uri.toString();
                        tvSelectedPhotoName.setText("✅ تم اختيار الصورة من المعرض بنجاح");
                        tvSelectedPhotoName.setVisibility(View.VISIBLE);
                    }
                }
        );

        btnSelectPhoto.setOnClickListener(v -> showImageSourceDialog(captureImageLauncher, pickMediaLauncher));

        btnSave.setOnClickListener(v -> {
            String desc = etDesc.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String proc = etProc.getText().toString().trim();

            if (desc.isEmpty() || date.isEmpty() || proc.isEmpty()) {
                Toast.makeText(this, "يرجى ملء الحقول الإلزامية (الوصف، التاريخ، الإجراءات)", Toast.LENGTH_SHORT).show();
                return;
            }

            long result = dbHelper.addTreatedCase(desc, date, savedPhotoPath, proc);
            if (result != -1) {
                Toast.makeText(this, "تم حفظ الحالة بنجاح", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "حدث خطأ أثناء الحفظ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showImageSourceDialog(ActivityResultLauncher<Void> cameraLauncher,
                                       ActivityResultLauncher<String> galleryLauncher) {
        String[] options = {"📷 التصوير بالكاميرا", "🖼️ اختيار من المعرض", "إلغاء"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("اختر مصدر الصورة");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    cameraLauncher.launch(null); // فتح الكاميرا باستخدام TakePicturePreview
                } else if (which == 1) {
                    galleryLauncher.launch("image/*"); // فتح المعرض
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private String saveBitmapToFile(Bitmap bitmap) {
        String filename = "case_img_" + System.currentTimeMillis() + ".jpg";
        File directory = getFilesDir(); // مجلد خاص بالتطبيق (لا يحتاج صلاحيات تخزين خارجية)
        File file = new File(directory, filename);

        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out); // ضغط الصورة بنسبة 85% لتوفير المساحة
            return file.getAbsolutePath(); // إرجاع المسار لحفظه في قاعدة البيانات
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) dbHelper.closeDatabase();
    }
}