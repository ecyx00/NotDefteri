package com.example.notdefteri;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class NotEkleActivity extends AppCompatActivity {

    private EditText editTextBaslik, editTextIcerik;
    private Button buttonKaydet, buttonSil, buttonGeri;
    private DatabaseHelper databaseHelper;
    private long notId = -1; // Düzenlenen notun ID'si

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_ekle);

        editTextBaslik = findViewById(R.id.editTextBaslik);
        editTextIcerik = findViewById(R.id.editTextIcerik);
        buttonKaydet = findViewById(R.id.buttonKaydet);
        buttonSil = findViewById(R.id.buttonSil);
        buttonGeri = findViewById(R.id.buttonGeri);

        databaseHelper = new DatabaseHelper(this);

        // MainActivity'den gelen NOT_ID değerini al
        notId = getIntent().getLongExtra("NOT_ID", -1);

        if (notId != -1) {
            // Eğer düzenlenen bir not varsa, notu getir ve ekrana yükle
            notuGetirVeGoster();
        } else {
            // Yeni bir not oluşturuluyorsa, sil butonunu devre dışı bırak
            buttonSil.setEnabled(false);
        }

        buttonKaydet.setOnClickListener(v -> notKaydet()); // Kaydet butonu
        buttonSil.setOnClickListener(v -> notSilUyarisiGoster()); // Silme uyarısını göster
        buttonGeri.setOnClickListener(v -> finish()); // Geri tuşuna basıldığında ana ekrana dön
    }

    private void notuGetirVeGoster() {
        Cursor cursor = databaseHelper.notuGetir((int) notId); // İlgili notu veritabanından getir

        if (cursor != null && cursor.moveToFirst()) {
            // Sütun indekslerini al
            int baslikIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_BASLIK);
            int icerikIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ICERIK);

            // Sütunların mevcut olup olmadığını kontrol et
            if (baslikIndex != -1 && icerikIndex != -1) {
                // Sütunlar varsa, verileri yükle
                editTextBaslik.setText(cursor.getString(baslikIndex));
                editTextIcerik.setText(cursor.getString(icerikIndex));
            } else {
                // Sütunlar bulunamazsa hata mesajı göster
                Toast.makeText(this, "Veritabanında gerekli sütunlar bulunamadı!", Toast.LENGTH_SHORT).show();
            }
            cursor.close(); // Cursor'u kapat
        } else {
            // Eğer cursor boşsa veya hata oluşmuşsa
            Toast.makeText(this, "Not bulunamadı!", Toast.LENGTH_SHORT).show();
        }
    }


    private void notKaydet() {
        String baslik = editTextBaslik.getText().toString().trim();
        String icerik = editTextIcerik.getText().toString().trim();
        String tarih = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());

        if (baslik.isEmpty() || icerik.isEmpty()) {
            Toast.makeText(this, "Başlık ve içerik boş olamaz!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean result;
        if (notId == -1) {
            // Yeni not ekleme
            result = databaseHelper.notEkle(baslik, icerik, tarih);
        } else {
            // Mevcut notu güncelleme
            result = databaseHelper.notGuncelle((int) notId, baslik, icerik, tarih);
        }

        if (result) {
            Toast.makeText(this, "Not başarıyla kaydedildi!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Not kaydedilirken bir hata oluştu!", Toast.LENGTH_SHORT).show();
        }
    }

    private void notSil() {
        if (notId != -1) {
            boolean result = databaseHelper.notSil((int) notId);
            if (result) {
                Toast.makeText(this, "Not silindi!", Toast.LENGTH_SHORT).show();
                finish(); // Ana ekrana dön
            } else {
                Toast.makeText(this, "Not silinirken bir hata oluştu!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void notSilUyarisiGoster() {
        // Kullanıcıya bir uyarı göstermek için AlertDialog oluştur
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notu Sil");
        builder.setMessage("Bu not silinsin mi?");
        builder.setPositiveButton("Sil", (DialogInterface dialog, int which) -> notSil()); // Kullanıcı "Sil" seçerse
        builder.setNegativeButton("İptal", (DialogInterface dialog, int which) -> dialog.dismiss()); // Kullanıcı "İptal" seçerse
        builder.create().show();
    }
}












