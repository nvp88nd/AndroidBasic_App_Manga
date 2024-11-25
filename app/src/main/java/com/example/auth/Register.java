package com.example.auth;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl_appmanga.R;
import com.example.dbhelper.DatabaseHelper;

public class Register extends AppCompatActivity {

    EditText etEmail, etName, etPass, etPass2;
    Button btnRegister;
    ImageButton ibBack;

    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        getObj();
        setBtn();
    }
    private void getObj() {
        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        etPass = findViewById(R.id.etPass);
        etPass2 = findViewById(R.id.etPass2);
        btnRegister = findViewById(R.id.btnRegister);
        ibBack = findViewById(R.id.ibBack);
    }
    private void setBtn() {
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tk = etEmail.getText().toString();
                String name = etName.getText().toString();
                String mk = etPass.getText().toString();
                String mk2 = etPass2.getText().toString();

                if(tk.trim().isEmpty() || name.trim().isEmpty() || mk.trim().isEmpty() || mk2.trim().isEmpty()) {
                    Toast.makeText(Register.this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(!mk.equals(mk2)) {
                        Toast.makeText(Register.this, "Mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        ContentValues values = new ContentValues();
                        values.put("email", tk);
                        values.put("password", mk);
                        values.put("userName", name);

                        if(db.insert("account", null, values) > 0) {
                            Toast.makeText(Register.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(Register.this, "Email đã được sử dụng! Vui lòng sử dụng Email khác!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
}