package com.example.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.adapter.ImgAdapter;
import com.example.btl_appmanga.R;
import com.example.dbhelper.DatabaseHelper;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Chapter extends AppCompatActivity {

    ImageButton imgBtnBack, imgBtnBefore, imgBtnAfter;
    TextView txtNameChapter;
    Button btnChapSelect;
    ListView lstImg;
    String mangaID;

    private SharedPreferences preferences;
    boolean isLogin;

    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chapter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        preferences = getSharedPreferences("UserPref", MODE_PRIVATE);
        isLogin = preferences.getBoolean("isLogin", false);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        getObj();
        Intent intent = new Intent(getIntent());
        String path = intent.getStringExtra("src") + "chapter-";
        AtomicInteger index = new AtomicInteger(Integer.parseInt(intent.getStringExtra("index")));
        int chapterTotal = Integer.parseInt(intent.getStringExtra("chapterTotal"));
        mangaID = intent.getStringExtra("mangaID");

        loadImg(path, index.get(), chapterTotal);

        btnChapSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog dialog = new BottomSheetDialog(Chapter.this);
                View view1 = LayoutInflater.from(Chapter.this).inflate(R.layout.dialog_list_chap, null);
                dialog.setContentView(view1);

                ListView lv = view1.findViewById(R.id.listViewPopup);

                ArrayList<String> lstChap = new ArrayList<>();
                for(int i = chapterTotal; i >= 1; i-- ) {
                    lstChap.add("Chapter " + i);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Chapter.this, android.R.layout.simple_list_item_1, lstChap);
                lv.setAdapter(adapter);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String chapterName = lstChap.get(i);
                        String numberOnly= chapterName.replaceAll("[^0-9]", "");

                        loadImg(path, Integer.parseInt(numberOnly), chapterTotal);
                        index.set(Integer.parseInt(numberOnly));

                        dialog.dismiss();
                    }
                });

                dialog.setOnShowListener(dialogInterface -> {
                    BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                    View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                    if (bottomSheetInternal != null) {
                        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheetInternal);
                        behavior.setDraggable(false); //tắt vuốt xuống để ẩn
                    }
                });

                dialog.show();
            }
        });

        imgBtnBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index.decrementAndGet();
                loadImg(path, index.get(), chapterTotal);
            }
        });
        imgBtnAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index.incrementAndGet();
                loadImg(path, index.get(), chapterTotal);
            }
        });
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void insertHistory(String mangaID, String lastChapter) {
        if(isLogin) {
            db.execSQL("insert or replace into history(email, mangaID, lastReadChapter, lastReadTime) " +
                            "values (?, ?, ?, DATETIME('now', '+7 hours'))",
                    new String[]{preferences.getString("Email", ""), mangaID, lastChapter});
        }
    }

    private void loadImg(String path, int index, int chapterTotal) {
        if(index == 1) {
            imgBtnBefore.setVisibility(View.INVISIBLE);
        }
        else if (index == chapterTotal) {
            imgBtnAfter.setVisibility(View.INVISIBLE);
        }
        else {
            imgBtnBefore.setVisibility(View.VISIBLE);
            imgBtnAfter.setVisibility(View.VISIBLE);
        }
        btnChapSelect.setText("Chapter " + index);
        txtNameChapter.setText("Chapter " + index);
        ArrayList<String> lstPath = new ArrayList<>();
        int countImg = 0;
        File fd = new File(path + index);
        if(fd.exists() && fd.isDirectory()) {
            File[] jpgFiles = fd.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg"));
            countImg = jpgFiles.length;
        }
        for(int i = 0; i < countImg; i++ ) {
            String s = path + index + "/" + i + ".jpg";
            lstPath.add(s);
        }
        ImgAdapter adapter = new ImgAdapter(this, lstPath);
        lstImg.setAdapter(adapter);
        insertHistory(mangaID, index + "");
    }

    private void getObj(){
        imgBtnBack = findViewById(R.id.imageButton);
        imgBtnBefore = findViewById(R.id.imgBtnBefore);
        imgBtnAfter = findViewById(R.id.imgBtnAfter);
        txtNameChapter = findViewById(R.id.txtNameChapter);
        btnChapSelect = findViewById(R.id.btnChapSelect);
        lstImg = findViewById(R.id.lstImg);
    }
}