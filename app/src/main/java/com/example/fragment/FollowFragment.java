package com.example.fragment;

import android.content.Context;
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

import com.example.adapter.RvDoubleAdapter;
import com.example.activity.MangaDetail;
import com.example.btl_appmanga.R;
import com.example.dbhelper.DatabaseHelper;
import com.example.object.Manga;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FollowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowFragment extends Fragment {

    RecyclerView rv;
    ArrayList<Manga> lstManga;
    LinearLayoutManager linearLayoutManager;
    RvDoubleAdapter rvDoubleAdapter;
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

    public FollowFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FollowFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FollowFragment newInstance(String param1, String param2) {
        FollowFragment fragment = new FollowFragment();
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
        View view = inflater.inflate(R.layout.fragment_follow, container, false);
        preferences = requireContext().getSharedPreferences("UserPref", Context.MODE_PRIVATE);
        callRecycleView(view);
        return view;
    }

    public void callRecycleView(View view) {
        rv = view.findViewById(R.id.rvFollow);

        dbHelper = new DatabaseHelper(requireContext());
        db = dbHelper.getWritableDatabase();

        lstManga = new ArrayList<>();
        Cursor cursor = db.rawQuery("select m.mangaID, m.mangaName, m.coverImage from favorites f join manga m on f.mangaID = m.mangaID " +
                "where f.email = ?", new String[]{preferences.getString("Email", "")});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("mangaID"));
                String tenTruyen = cursor.getString(cursor.getColumnIndexOrThrow("mangaName"));
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

        rvDoubleAdapter = new RvDoubleAdapter(lstManga, itemClickListener);
        linearLayoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(rvDoubleAdapter);
    }
}