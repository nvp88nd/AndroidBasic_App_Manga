package com.example.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.RvDoubleAdapter;
import com.example.auth.Login;
import com.example.btl_appmanga.R;
import com.example.dbhelper.DatabaseHelper;
import com.example.object.Manga;

import java.util.ArrayList;

public class ShowListManga extends AppCompatActivity {

    TextView tvNameList;
    ImageButton ibBack;
    String type, sql;
    String[] seletionArgs;

    RecyclerView rv;
    ArrayList<Manga> lstManga;
    LinearLayoutManager linearLayoutManager;
    RvDoubleAdapter rvDoubleAdapter;

    private SharedPreferences preferences;

    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_list_manga);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preferences = getSharedPreferences("UserPref", MODE_PRIVATE);
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        getObj();
        setObj();

        callRecycleView(sql, seletionArgs);
    }

    private void getObj() {
        tvNameList = findViewById(R.id.tvNameList);
        ibBack = findViewById(R.id.ibBack);
    }

    private void setObj() {
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if(type.equals("decu")) {
            tvNameList.setText("Truyện đề cử");
            sql = "select * from manga ORDER BY RANDOM()";
            seletionArgs = null;
        }
        else if(type.equals("hot")) {
            tvNameList.setText("Truyện hot");
            sql = "select * from manga";
            seletionArgs = null;
        }
        else if(type.equals("moi")) {
            tvNameList.setText("Truyện mới cập nhật");
            sql = "select * from manga ORDER BY RANDOM()";
            seletionArgs = null;
        }
        else if(type.equals("history")) {
            tvNameList.setText("Lịch sử của bạn");
            sql = "select m.mangaID, m.mangaName, m.coverImage, h.lastReadChapter, h.lastReadTime from history h join manga m " +
                    "on h.mangaID = m.mangaID where h.email = ? ORDER BY h.lastReadTime DESC";
            seletionArgs = new String[]{preferences.getString("Email", "")};
        }
        else {
            tvNameList.setText("Tìm kiếm");
            sql = "select * from manga WHERE mangaName LIKE ? COLLATE NOCASE";
            seletionArgs = new String[]{"%" + type + "%"};
        }
    }

    public void callRecycleView(String sql, String[] selectionArgs) {
        rv = findViewById(R.id.rvListManga);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        lstManga = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("mangaID"));
                String tenTruyen;
                if(!type.equals("history")) {
                    tenTruyen = cursor.getString(cursor.getColumnIndexOrThrow("mangaName"));
                }
                else {
                    tenTruyen = "Chapter " + cursor.getString(cursor.getColumnIndexOrThrow("lastReadChapter")) +
                            "\n" + cursor.getString(cursor.getColumnIndexOrThrow("mangaName"));
                }
                String src = cursor.getString(cursor.getColumnIndexOrThrow("coverImage"));
                Manga manga = new Manga(id, tenTruyen, src);
                lstManga.add(manga);
            } while (cursor.moveToNext());
            cursor.close();
        }

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Manga selected = lstManga.get(i);
                Intent intent = new Intent(ShowListManga.this, MangaDetail.class);
                intent.putExtra("id", selected.getId());
                startActivity(intent);
            }
        };

        rvDoubleAdapter = new RvDoubleAdapter(lstManga, itemClickListener);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(rvDoubleAdapter);
    }
}