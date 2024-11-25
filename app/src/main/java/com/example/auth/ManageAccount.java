package com.example.auth;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl_appmanga.R;
import com.example.dbhelper.DatabaseHelper;

public class ManageAccount extends AppCompatActivity {

    EditText etUserName, etEmail, etOldPass, etNewPass, etNewPass2;
    Button btnChangeName, btnUpdName, btnChangePass, btnUpdPass;
    ImageButton ibBack, ibCloseName,ibClosePass, ibShowPass;
    LinearLayout layoutPass;

    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        preferences = this.getSharedPreferences("UserPref", Context.MODE_PRIVATE);
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        getObj();
        setObj();
    }

    private void setObj() {
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Cursor cursor = db.rawQuery("select * from account where email = ?", new String[]{preferences.getString("Email", "")});
        if(cursor != null && cursor.moveToFirst()) {
            etUserName.setText(cursor.getString(cursor.getColumnIndexOrThrow("userName")));
            etEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            cursor.close();
        }
        String name = etUserName.getText().toString();
        btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etUserName.setFocusable(true);
                etUserName.setFocusableInTouchMode(true);
                etUserName.requestFocus();
                btnChangeName.setVisibility(View.GONE);
                ibCloseName.setVisibility(View.VISIBLE);
                btnUpdName.setVisibility(View.VISIBLE);
            }
        });

        ibCloseName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etUserName.setText(name);
                closeName();
            }
        });

        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutPass.setVisibility(View.VISIBLE);
                btnChangePass.setVisibility(View.GONE);
                ibShowPass.setVisibility(View.VISIBLE);
                ibClosePass.setVisibility(View.VISIBLE);
                btnUpdPass.setVisibility(View.VISIBLE);
            }
        });

        btnUpdName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                String newName = etUserName.getText().toString();
                if(!newName.trim().isEmpty()) {
                    values.put("userName", newName);
                    int result = db.update("account", values, "email = ?", new String[]{preferences.getString("Email", "")});
                    if(result > 0) {
                        etUserName.setText(newName);
                        closeName();
                        Toast.makeText(ManageAccount.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(ManageAccount.this, "Cập nhật thất bại! Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(ManageAccount.this, "Tên không được để trống!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ibClosePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closePass();
            }
        });
        ibShowPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable currentDrawable = ibShowPass.getDrawable();
                Drawable visibilityOff = getResources().getDrawable(R.drawable.baseline_visibility_off_24);
                if (currentDrawable.getConstantState().equals(visibilityOff.getConstantState())) {
                    ibShowPass.setImageResource(R.drawable.baseline_visibility_24);
                    etOldPass.setInputType(InputType.TYPE_CLASS_TEXT);
                    etNewPass.setInputType(InputType.TYPE_CLASS_TEXT);
                    etNewPass2.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                else {
                    ibShowPass.setImageResource(R.drawable.baseline_visibility_off_24);
                    etOldPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etNewPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etNewPass2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        btnUpdPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                String oldpass = etOldPass.getText().toString();
                String newpass = etNewPass.getText().toString();
                String newpass2 = etNewPass2.getText().toString();
                if(!oldpass.trim().isEmpty() && !newpass.trim().isEmpty() && !newpass2.trim().isEmpty()) {
                    Cursor cursor = db.rawQuery("select userName from account where email = ? and password = ?", new String[]{preferences.getString("Email", ""), oldpass});
                    if (cursor != null && cursor.moveToFirst()) {
                        if (newpass.equals(newpass2)) {
                            values.put("password", newpass);
                            int result = db.update("account", values, "email = ?", new String[]{preferences.getString("Email", "")});
                            if(result > 0) {
                                closePass();
                                Toast.makeText(ManageAccount.this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(ManageAccount.this, "Đổi mật khẩu thất bại! Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(ManageAccount.this, "Xác nhận mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
                        }
                        cursor.close();
                    }
                    else {
                        Toast.makeText(ManageAccount.this, "Mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(ManageAccount.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void closeName() {
        etUserName.clearFocus();
        etUserName.setFocusable(false);
        etUserName.setFocusableInTouchMode(false);
        btnChangeName.setVisibility(View.VISIBLE);
        ibCloseName.setVisibility(View.GONE);
        btnUpdName.setVisibility(View.GONE);
    }

    private void closePass() {
        layoutPass.setVisibility(View.GONE);
        btnChangePass.setVisibility(View.VISIBLE);
        ibShowPass.setVisibility(View.GONE);
        ibClosePass.setVisibility(View.GONE);
        btnUpdPass.setVisibility(View.GONE);
    }

    private void getObj() {
        etUserName = findViewById(R.id.etUserName);
        etEmail = findViewById(R.id.etEmail);
        etOldPass = findViewById(R.id.etOldPass);
        etNewPass = findViewById(R.id.etNewPass);
        etNewPass2 = findViewById(R.id.etNewPass2);
        btnChangeName = findViewById(R.id.btnChangeName);
        btnUpdName = findViewById(R.id.btnUpdName);
        btnChangePass = findViewById(R.id.btnChangePass);
        btnUpdPass = findViewById(R.id.btnUpdPass);
        ibBack = findViewById(R.id.ibBack);
        ibCloseName = findViewById(R.id.ibCloseName);
        ibClosePass = findViewById(R.id.ibClosePass);
        ibShowPass = findViewById(R.id.ibShowPass);
        layoutPass = findViewById(R.id.layoutPassword);
    }
}