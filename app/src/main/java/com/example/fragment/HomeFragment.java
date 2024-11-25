package com.example.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.activity.ShowListManga;
import com.example.adapter.RvAdapter;
import com.example.activity.MangaDetail;
import com.example.btl_appmanga.R;
import com.example.dbhelper.DatabaseHelper;
import com.example.object.Manga;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    RecyclerView rv, rv1, rv2;
    ArrayList<Manga> lstManga, lstManga1, lstManga2;
    LinearLayoutManager linearLayoutManager, linearLayoutManager1, linearLayoutManager2;
    RvAdapter rvAdapter, rvAdapter1, rvAdapter2;
    TextView tvShowMore, tvShowMore1, tvShowMore2;
    AutoCompleteTextView edtTimTruyen;

    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    Handler handler = new Handler(); //chưa có tác dụng, thử lại sau
    Runnable searchRunnable = null; //chưa có tác dụng, thử lại sau

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        dbHelper = new DatabaseHelper(requireContext());
        db = dbHelper.getWritableDatabase();

        String sql = "select * from manga ORDER BY RANDOM() limit 5";
        String sql2 = "select * from manga limit 5";

        getObj(view);
        setObj(view);

        callHorizontalRV(lstManga, rv, linearLayoutManager, rvAdapter, sql);
        callHorizontalRV(lstManga1, rv1, linearLayoutManager1, rvAdapter1, sql2);
        callHorizontalRV(lstManga2, rv2, linearLayoutManager2, rvAdapter2, sql);

        return view;
    }

    private void setObj(View view) {
        ArrayList<String> suggestionList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select mangaName from manga", null);
        if(cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("mangaName"));
                suggestionList.add(name);
            } while (cursor.moveToNext());
            cursor.close();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestionList);
        edtTimTruyen.setAdapter(adapter);

        edtTimTruyen.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String inputText = edtTimTruyen.getText().toString();
                Intent intent = new Intent(requireContext(), ShowListManga.class);
                intent.putExtra("type", inputText);
                startActivity(intent);
                return true;
            }
            return false;
        });

        edtTimTruyen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedManga = (String) adapterView.getItemAtPosition(i);
                Cursor cursor = db.rawQuery("SELECT * FROM manga WHERE mangaName = ?", new String[]{selectedManga});
                if (cursor != null && cursor.moveToFirst()) {
                    String mangaId = cursor.getString(cursor.getColumnIndexOrThrow("mangaID"));
                    Intent intent = new Intent(requireContext(), MangaDetail.class);
                    intent.putExtra("id", mangaId);
                    startActivity(intent);
                    cursor.close();
                }
            }
        });

        tvShowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShowListManga.class);
                intent.putExtra("type", "decu");
                startActivity(intent);
            }
        });
        tvShowMore1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShowListManga.class);
                intent.putExtra("type", "hot");
                startActivity(intent);
            }
        });
        tvShowMore2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShowListManga.class);
                intent.putExtra("type", "moi");
                startActivity(intent);
            }
        });
    }

    private void getObj (View view) {
        edtTimTruyen = view.findViewById(R.id.edtTimTruyen);
        tvShowMore = view.findViewById(R.id.tvShowMore);
        tvShowMore1 = view.findViewById(R.id.tvShowMore1);
        tvShowMore2 = view.findViewById(R.id.tvShowMore2);
        rv = view.findViewById(R.id.horizontalRv);
        rv1 = view.findViewById(R.id.horizontalRv1);
        rv2 = view.findViewById(R.id.horizontalRv2);
        linearLayoutManager = new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false);
        linearLayoutManager1 = new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false);
        linearLayoutManager2 = new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false);
        rvAdapter = new RvAdapter();
        rvAdapter1 = new RvAdapter();
        rvAdapter2 = new RvAdapter();
    }

    private void callHorizontalRV(ArrayList<Manga> ds, RecyclerView rv, LinearLayoutManager lnlayoutmanager, RvAdapter rvAdapter, String sql) {
        ds = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("mangaID"));
                String tenTruyen = cursor.getString(cursor.getColumnIndexOrThrow("mangaName"));
                String src = cursor.getString(cursor.getColumnIndexOrThrow("coverImage"));
                Manga manga = new Manga(id, tenTruyen, src);
                ds.add(manga);
            } while (cursor.moveToNext());
            cursor.close();
        }

        ArrayList<Manga> finalDs = ds;
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Manga selected = finalDs.get(i);
                Intent intent = new Intent(getContext(), MangaDetail.class);
                intent.putExtra("id", selected.getId());
                startActivity(intent);
            }
        };
        rvAdapter.updateData(ds, itemClickListener);
        rv.setLayoutManager(lnlayoutmanager);
        rv.setAdapter(rvAdapter);
    }
}