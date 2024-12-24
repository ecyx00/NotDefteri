package com.example.notdefteri;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ListView listViewNotlar;
    private Button buttonYeniNot;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        listViewNotlar = findViewById(R.id.listViewNotlar);
        buttonYeniNot = findViewById(R.id.buttonYeniNot);

        buttonYeniNot.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotEkleActivity.class);
            startActivity(intent);
        });

        listViewNotlar.setOnItemClickListener((AdapterView<?> parent, android.view.View view, int position, long id) -> {
            // Tıklanan öğenin ID'sini alın ve düzenleme ekranına gönderin
            Intent intent = new Intent(MainActivity.this, NotEkleActivity.class);
            intent.putExtra("NOT_ID", id);
            startActivity(intent);
        });

        tumNotlariListele();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tumNotlariListele();
    }

    private void tumNotlariListele() {
        Cursor cursor = databaseHelper.tumNotlariGetir();
        if (cursor != null && cursor.getCount() > 0) {
            adapter = new SimpleCursorAdapter(
                    this,
                    R.layout.list_item,
                    cursor,
                    new String[]{DatabaseHelper.COLUMN_BASLIK, DatabaseHelper.COLUMN_ICERIK, DatabaseHelper.COLUMN_TARIH},
                    new int[]{R.id.textViewBaslik, R.id.textViewIcerik, R.id.textViewTarih},
                    0
            );
            listViewNotlar.setAdapter(adapter);
        } else {
            listViewNotlar.setAdapter(null);
            Toast.makeText(this, "Hiç not bulunamadı.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Arama işlemi için SearchView
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                notlariFiltrele(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                notlariFiltrele(newText);
                return false;
            }
        });

        return true;
    }

    private void notlariFiltrele(String query) {
        Cursor cursor;
        if (TextUtils.isEmpty(query)) {
            cursor = databaseHelper.tumNotlariGetir();
        } else {
            cursor = databaseHelper.notlariAra(query);
        }

        if (cursor != null && cursor.getCount() > 0) {
            adapter.changeCursor(cursor); // Yeni sonuçlarla adapter'ı güncelle
        } else {
            Toast.makeText(this, "Arama sonucunda not bulunamadı.", Toast.LENGTH_SHORT).show();
        }
    }
}














