package com.example.notdefteri;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Veritabanı bilgileri
    private static final String DATABASE_NAME = "NotDefteri.db";
    private static final int DATABASE_VERSION = 1;

    // Tablo ve sütun isimleri
    private static final String TABLE_NAME = "notlar";
    public static final String COLUMN_ID = "id"; // Tablonun birincil anahtar sütunu
    public static final String COLUMN_BASLIK = "baslik"; // Başlık sütunu
    public static final String COLUMN_ICERIK = "icerik"; // İçerik sütunu
    public static final String COLUMN_TARIH = "tarih"; // Tarih sütunu



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_BASLIK + " TEXT NOT NULL, " +
                COLUMN_ICERIK + " TEXT NOT NULL, " +
                COLUMN_TARIH + " TEXT NOT NULL)";
        db.execSQL(CREATE_TABLE);
    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eski tabloyu sil ve yeni tabloyu oluştur
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // 1. Not ekleme işlemi
    public boolean notEkle(String baslik, String icerik, String tarih) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BASLIK, baslik);
        values.put(COLUMN_ICERIK, icerik);
        values.put(COLUMN_TARIH, tarih);

        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result != -1; // Eklenemezse false döner
    }

    // 2. Tüm notları getir
    public Cursor tumNotlariGetir() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT id AS _id, baslik, icerik, tarih FROM " + TABLE_NAME + " ORDER BY id DESC", null);
    }




    // 3. Belirli bir ID'ye sahip notu getir
    public Cursor notuGetir(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT id AS _id, baslik, icerik, tarih FROM " + TABLE_NAME + " WHERE id = ?", new String[]{String.valueOf(id)});
    }


    public Cursor notlariAra(String keyword) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT id AS _id, baslik, icerik, tarih FROM " + TABLE_NAME +
                " WHERE " + COLUMN_BASLIK + " LIKE ? OR " + COLUMN_ICERIK + " LIKE ?" +
                " ORDER BY id DESC";
        String searchKeyword = "%" + keyword + "%";
        return db.rawQuery(query, new String[]{searchKeyword, searchKeyword});
    }





    // 4. Not güncelleme işlemi
    public boolean notGuncelle(int id, String baslik, String icerik, String tarih) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BASLIK, baslik);
        values.put(COLUMN_ICERIK, icerik);
        values.put(COLUMN_TARIH, tarih);

        int result = db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0; // Güncellenen satır sayısı 0'dan büyükse başarılı
    }

    // 5. Not silme işlemi
    public boolean notSil(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0; // Silinen satır sayısı 0'dan büyükse başarılı
    }



}








