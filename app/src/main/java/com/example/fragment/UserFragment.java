package com.example.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.activity.ShowListManga;
import com.example.adapter.RvAdapter;
import com.example.btl_appmanga.MainActivity;
import com.example.auth.ManageAccount;
import com.example.activity.MangaDetail;
import com.example.btl_appmanga.R;
import com.example.dbhelper.DatabaseHelper;
import com.example.object.Manga;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    TextView tvUserName, tvEmail, tvDelAcc, tvLogout, tvShowAll;
    ImageButton imgBtnWriteProfile;
    RecyclerView rv;
    ArrayList<Manga> lstManga;
    LinearLayoutManager linearLayoutManager;
    RvAdapter rvAdapter;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    private SharedPreferences preferences;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        preferences = requireContext().getSharedPreferences("UserPref", Context.MODE_PRIVATE);
        dbHelper = new DatabaseHelper(getContext());
        db = dbHelper.getWritableDatabase();
        getObj(view);
        setObj();
        callHorizontalRV();
        return view;
    }
    private void callHorizontalRV() {
        lstManga = new ArrayList<>();
        Cursor cursor = db.rawQuery("select m.mangaID, m.mangaName, m.coverImage, h.lastReadChapter, h.lastReadTime from history h join manga m " +
                "on h.mangaID = m.mangaID where h.email = ? ORDER BY h.lastReadTime DESC limit 5", new String[]{preferences.getString("Email", "")});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("mangaID"));
                String tenTruyen = "Chapter " + cursor.getString(cursor.getColumnIndexOrThrow("lastReadChapter")) +
                        "\n" + cursor.getString(cursor.getColumnIndexOrThrow("mangaName"));
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
                Intent intent = new Intent(getContext(), MangaDetail.class);
                intent.putExtra("id", selected.getId());
                startActivity(intent);
            }
        };

        linearLayoutManager = new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false);
        rvAdapter =new RvAdapter(lstManga, itemClickListener);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(rvAdapter);
    }
    private void getObj(View view) {
        tvUserName = view.findViewById(R.id.tvUserName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvDelAcc = view.findViewById(R.id.tvDelAcc);
        tvLogout = view.findViewById(R.id.tvLogout);
        rv = view.findViewById(R.id.rvHistory);
        imgBtnWriteProfile = view.findViewById(R.id.imgBtnWriteProfile);
        tvShowAll = view.findViewById(R.id.tvShowAll);
    }
    private void setObj() {
        tvShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShowListManga.class);
                intent.putExtra("type", "history");
                startActivity(intent);
            }
        });

        imgBtnWriteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ManageAccount.class);
                startActivity(intent);
            }
        });

        Cursor cursor = db.rawQuery("select * from account where email = ?", new String[]{preferences.getString("Email", "")});
        if(cursor != null && cursor.moveToFirst()) {
            tvUserName.setText(cursor.getString(cursor.getColumnIndexOrThrow("userName")));
            tvEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            cursor.close();
        }
        tvDelAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Xóa tài khoản?");
                builder.setMessage("Bạn có chắc chắn muốn xóa tài khoản này?\nSau khi xóa tài khoản không thể khôi phục!");

                builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = preferences.getString("Email", "");
                        logout();
                        if(db.delete("account", "email = ?", new String[]{email}) > 0) {
                            Toast.makeText(getContext(), "Tài khoản đã bị xóa vĩnh viễn!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Đóng dialog
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
            }
        });
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
                Toast.makeText(getContext(), "Đã đăng xuất!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void logout() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // dọn dẹp các Activity trước đó
        startActivity(intent);
        getActivity().finish();  // Đóng Fragment hiện tại nếu cần
    }
}