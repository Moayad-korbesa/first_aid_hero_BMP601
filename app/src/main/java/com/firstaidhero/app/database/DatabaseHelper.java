package com.firstaidhero.app.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "firstaid_hero.db";
    private static final int DB_VERSION = 1;
    private final Context context;
    private SQLiteDatabase database;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // لا شيء هنا لأن القاعدة موجودة مسبقاً
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // يمكن إضافة منطق الترقية لاحقاً
    }

    // فتح قاعدة البيانات
    public void openDatabase() throws IOException {
        String dbPath = context.getDatabasePath(DB_NAME).getPath();

        if (!checkDatabase(dbPath)) {
            copyDatabase(dbPath);
        }

        database = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    // التحقق من وجود القاعدة
    private boolean checkDatabase(String dbPath) {
        File dbFile = new File(dbPath);
        return dbFile.exists();
    }

    // نسخ القاعدة من assets
    private void copyDatabase(String dbPath) throws IOException {
        InputStream inputStream = context.getAssets().open(DB_NAME);
        OutputStream outputStream = new FileOutputStream(dbPath);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();

        Log.d("DatabaseHelper", "تم نسخ قاعدة البيانات بنجاح");
    }

    // إغلاق القاعدة
    public void closeDatabase() {
        if (database != null) {
            database.close();
        }
    }

    // الحصول على قاعدة البيانات
    public SQLiteDatabase getDatabase() {
        return database;
    }

    // ====== دوال الاستعلام ======

    // البحث في حالات الطوارئ
    public Cursor searchEmergencies(String keyword) {
        String query = "SELECT * FROM emergencies WHERE title LIKE '%" + keyword + "%' OR symptoms LIKE '%" + keyword + "%'";
        return database.rawQuery(query, null);
    }

    // الحصول على كل الحالات
    public Cursor getAllEmergencies() {
        String query = "SELECT * FROM emergencies";
        return database.rawQuery(query, null);
    }

    // الحصول على خطوات إسعاف حالة معينة
    public Cursor getStepsByCaseId(int caseId) {
        String query = "SELECT * FROM steps WHERE case_id = " + caseId + " ORDER BY step_number";
        return database.rawQuery(query, null);
    }

    // الحصول على جهات الاتصال المخصصة
    public Cursor getAllCustomContacts() {
        String query = "SELECT * FROM custom_contacts";
        return database.rawQuery(query, null);
    }

//    استخدام LIKE للبحث عن أي حالة تطابق جزءاً من التاريخ (مثال: "2026-10")
    public Cursor searchTreatedCasesByDate(String date) {
        // استخدام LIKE للبحث عن أي حالة تطابق جزءاً من التاريخ (مثال: "2026-10")
        String query = "SELECT * FROM treated_cases WHERE date LIKE '%" + date + "%' ORDER BY date DESC";
        return database.rawQuery(query, null);
    }

    // إضافة جهة اتصال جديدة
    public long addCustomContact(String name, String phone) {
        String query = "INSERT INTO custom_contacts (contact_name, phone_number) VALUES ('" + name + "', '" + phone + "')";
        database.execSQL(query);

        Cursor cursor = database.rawQuery("SELECT last_insert_rowid()", null);
        long id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getLong(0);
        }
        cursor.close();
        return id;
    }

    // إضافة حالة معالجة
    public long addTreatedCase(String description, String date, String photo, String procedures) {
        String query = "INSERT INTO treated_cases (description, date, photo, procedures) VALUES ('"
                + description + "', '" + date + "', '" + photo + "', '" + procedures + "')";
        database.execSQL(query);

        Cursor cursor = database.rawQuery("SELECT last_insert_rowid()", null);
        long id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getLong(0);
        }
        cursor.close();
        return id;
    }

    // الحصول على كل الحالات المعالجة
    public Cursor getAllTreatedCases() {
        String query = "SELECT * FROM treated_cases ORDER BY date DESC";
        return database.rawQuery(query, null);
    }

    // إحصائيات: عدد الحالات حسب مستوى الخطورة
    public Cursor getCasesByDangerLevel() {
        String query = "SELECT danger_level, COUNT(*) as count FROM emergencies GROUP BY danger_level";
        return database.rawQuery(query, null);
    }

    // عدد الحالات المعالجة
    public Cursor getTreatedCasesCount() {
        String query = "SELECT COUNT(*) FROM treated_cases";
        return database.rawQuery(query, null);
    }

    // استعلام تجميعي: تصنيف الحالات حسب الخطورة مع العدد
    public Cursor getEmergencyStats() {
        String query = "SELECT danger_level, COUNT(*) as count FROM emergencies GROUP BY danger_level";
        return database.rawQuery(query, null);
    }


}