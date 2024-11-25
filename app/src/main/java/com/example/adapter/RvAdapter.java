package com.example.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_appmanga.R;
import com.example.object.Manga;

import java.io.File;
import java.util.ArrayList;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.Holder> {
    private ArrayList<Manga> data;
    private AdapterView.OnItemClickListener mListener;
    public RvAdapter(ArrayList<Manga> data, AdapterView.OnItemClickListener mListener) {
        this.data = data;
        this.mListener = mListener;
    }
    public RvAdapter() {
        this.data = new ArrayList<>();
        this.mListener = null;
    }
    public void updateData(ArrayList<Manga> newLstManga, AdapterView.OnItemClickListener newListener) {
        this.data = newLstManga;
        this.mListener = newListener;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Manga manga = data.get(position);
        holder.tvTitle.setText(manga.getName());

        String imagePath = Environment.getExternalStorageDirectory() + manga.getSrc();
        File imgFile = new File(imagePath);
        if(imgFile.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.imageView.setImageBitmap(bitmap);
        }
        holder.bind(manga, mListener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView imageView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            imageView = itemView.findViewById((R.id.imageView));
        }
        public void bind(final Manga manga, final AdapterView.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(null, view, getAdapterPosition(), getItemId());
                }
            });
        }
    }
}
