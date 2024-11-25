package com.example.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl_appmanga.R;
import com.example.dbhelper.DatabaseHelper;

public class Login extends AppCompatActivity {

    TextView tvRedictRegister;
    EditText etEmail, etPass;
    Button btnLogin;
    ImageButton ibBack;

    private SharedPreferences preferences;

    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preferences = getSharedPreferences("UserPref", MODE_PRIVATE);
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        getObj();
        setBtn();
    }

    private void getObj() {
        tvRedictRegister = findViewById(R.id.tvRedictRegister);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        btnLogin = findViewById(R.id.btnLogin);
        ibBack = findViewById(R.id.ibBack);
    }
    private void setBtn() {
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvRedictRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tk = etEmail.getText().toString();
                String mk = etPass.getText().toString();
                if(tk.trim().isEmpty() || mk.trim().isEmpty()) {
                    Toast.makeText(Login.this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (checkAcc(tk, mk)) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isLogin", true);
                        editor.putString("Email", tk);
                        editor.apply();

                        Toast.makeText(Login.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(Login.this, "Tài khoản hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean checkAcc(String tk, String mk) {
        String query = "select userName from account where email = ? and password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{tk, mk});
        if(cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        return false;
    }
}