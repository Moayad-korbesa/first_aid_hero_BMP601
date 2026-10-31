package com.firstaidhero.app.model;

public class TreatedCase {
    private int id;
    private String description;
    private String date;
    private String photo; // مسار الصورة كنص
    private String procedures;

    public TreatedCase(int id, String description, String date, String photo, String procedures) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.photo = photo;
        this.procedures = procedures;
    }

    public int getId() { return id; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getPhoto() { return photo; }
    public String getProcedures() { return procedures; }
}