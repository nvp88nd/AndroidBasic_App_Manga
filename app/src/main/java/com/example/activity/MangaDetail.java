package com.example.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.auth.Login;
import com.example.btl_appmanga.R;
import com.example.dbhelper.DatabaseHelper;

import java.io.File;
import java.util.ArrayList;

public class MangaDetail extends AppCompatActivity {

    ImageButton imgBtnBack;
    ImageView imageView;
    TextView txtNameManga, txtAuthor, txtGenres, txtFollow, txtDescrible;
    Button btnReadHead, btnFollow;
    ListView lstChapter;

    private SharedPreferences preferences;
    boolean isLogin;

    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manga_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preferences = getSharedPreferences("UserPref", MODE_PRIVATE);
        isLogin = preferences.getBoolean("isLogin", false);
        getObj();
        setBtn();

        Intent intent = getIntent();
        String mangaID = intent.getStringExtra("id");

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        showDetail(mangaID);
        showChapter(mangaID);
        if(checkFollow(preferences.getString("Email", ""), mangaID)) {
            btnFollow.setText("Bỏ theo dõi");
        }
    }

    private void getObj(){
        imgBtnBack = findViewById(R.id.imageButton);
        imageView = findViewById(R.id.imageView);
        txtNameManga = findViewById(R.id.txtNameManga);
        txtAuthor = findViewById(R.id.txtAuthor);
        txtGenres = findViewById(R.id.txtGenres);
        txtFollow = findViewById(R.id.txtFollow);
        txtDescrible = findViewById(R.id.txtDescrible);
        btnReadHead = findViewById(R.id.btnReadHead);
        btnFollow = findViewById(R.id.btnFollow);
        lstChapter = findViewById(R.id.lstChapter);
    }

    private void setBtn() {
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void showDetail(String id) {
        String src = String.valueOf(Environment.getExternalStorageDirectory());
        String tenTruyen = "", author = "", genres = "", genresfm = "", describle = "";
        String followCount = "0";

        Cursor cursor = db.query("manga", null, "mangaID = ?", new String[]{id}, null,  null,  null,  null);
        if(cursor != null && cursor.moveToFirst()) {
            tenTruyen = cursor.getString(cursor.getColumnIndexOrThrow("mangaName"));
            src += cursor.getString(cursor.getColumnIndexOrThrow("coverImage"));
            author = cursor.getString(cursor.getColumnIndexOrThrow("author"));
            describle = cursor.getString(cursor.getColumnIndexOrThrow("describle"));
            cursor.close();
        }

        String getGenres = "select g.genresName from genres g join manga_genres mg on g.genresID = mg.genresID where mg.mangaID = ?";
        Cursor cursor2 = db.rawQuery(getGenres, new String[]{id});
        if(cursor2 != null && cursor2.moveToFirst()) {
            do {
                genres += cursor2.getString(cursor2.getColumnIndexOrThrow("genresName")) + " - ";
            }while (cursor2.moveToNext());
            cursor2.close();
            genresfm = genres.substring(0, genres.length() - 3);
        }
        if(genresfm.trim().isEmpty()) genresfm = "Đang cập nhật";

        String getCountOfFollow = "select count(*) as followCount from favorites where mangaID = ?";
        Cursor cursor3 = db.rawQuery(getCountOfFollow, new String[]{id});
        if(cursor3 != null && cursor3.moveToFirst()) {
            followCount = cursor3.getString(cursor3.getColumnIndexOrThrow("followCount"));
            cursor3.close();
        }

        txtNameManga.setText(tenTruyen);
        txtAuthor.setText("Tác giả: " + author);
        txtGenres.setText("Thể loại: " + genresfm);
        txtFollow.setText("Lượt theo dõi: " + followCount);
        txtDescrible.setText(describle);

        File img = new File(src);
        if(img.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(img.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        }
    }

    private boolean checkFollow(String email, String mangaID) {
        if(isLogin) {
            String qrCheck = "select * from favorites where email = ? and mangaID = ?";
            Cursor cursor = db.rawQuery(qrCheck, new String[]{email, mangaID});
            if(cursor != null && cursor.moveToFirst()){
                cursor.close();
                return true;
            }
        }
        return false;
    }

    private void showChapter(String id) {
        String path = String.valueOf(Environment.getExternalStorageDirectory());
        int chapterTotal = 0;
        ArrayList<String> lstChap = new ArrayList<>();

        String getSrc = "select chapterTotal, srcChapter from manga where mangaID = ?";
        Cursor cursor = db.rawQuery(getSrc, new String[]{id});
        if(cursor != null && cursor.moveToFirst()) {
             path += cursor.getString(cursor.getColumnIndexOrThrow("srcChapter"));
             chapterTotal = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("chapterTotal")));
             cursor.close();
        }

        for(int i = chapterTotal; i >= 1; i-- ) {
            lstChap.add("Chapter " + i);
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lstChap);
        lstChapter.setAdapter(adapter);

        String finalPath = path;
        String finalChapterTotal = "" + chapterTotal;
        String finalMangaID = id;
        lstChapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String chapterName = lstChap.get(position);
                String numberOnly= chapterName.replaceAll("[^0-9]", "");

                Intent intent = new Intent(MangaDetail.this, Chapter.class);
                intent.putExtra("index", numberOnly);
                intent.putExtra("chapterTotal", finalChapterTotal);
                intent.putExtra("src", finalPath);
                intent.putExtra("mangaID", finalMangaID);
                startActivity(intent);
            }
        });

        btnReadHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MangaDetail.this, Chapter.class);
                intent.putExtra("index", "1");
                intent.putExtra("chapterTotal", finalChapterTotal);
                intent.putExtra("src", finalPath);
                intent.putExtra("mangaID", finalMangaID);
                startActivity(intent);
            }
        });

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isLogin) {
                    Intent intent = new Intent(MangaDetail.this, Login.class);
                    startActivity(intent);
                }
                else {
                    if(!checkFollow(preferences.getString("Email", ""), id)) {
                        ContentValues values = new ContentValues();
                        values.put("email", preferences.getString("Email", ""));
                        values.put("mangaID", id);
                        long result = db.insert("favorites", null, values);
                        if(result > 0) {
                            btnFollow.setText("Bỏ theo dõi");
                        }
                    }
                    else {
                        int result = db.delete("favorites", "email = ? and mangaID = ?", new String[]{preferences.getString("Email", ""), id});
                        if(result > 0) {
                            btnFollow.setText("Theo dõi");
                        }
                    }
                    String followCount = "0";
                    String getCountOfFollow = "select count(*) as followCount from favorites where mangaID = ?";
                    Cursor cursor3 = db.rawQuery(getCountOfFollow, new String[]{id});
                    if(cursor3 != null && cursor3.moveToFirst()) {
                        followCount = cursor3.getString(cursor3.getColumnIndexOrThrow("followCount"));
                        cursor3.close();
                    }

                    txtFollow.setText("Lượt theo dõi: " + followCount);
                }
            }
        });
    }
}