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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_appmanga.R;
import com.example.object.Manga;

import java.io.File;
import java.util.ArrayList;

public class RvDoubleAdapter extends RecyclerView.Adapter<RvDoubleAdapter.Holder2> {
    ArrayList<Manga> data;
    private AdapterView.OnItemClickListener mListener;
    public RvDoubleAdapter(ArrayList<Manga> data, AdapterView.OnItemClickListener mListener) {
        this.data = data;
        this.mListener = mListener;
    }
    @NonNull
    @Override
    public Holder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_double_item, parent, false);
        return new Holder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder2 holder, int position) {
        int index = position * 2;

        // Đặt dữ liệu cho CardView thứ nhất
        if (index < data.size()) {
            Manga manga1 = data.get(index);
            holder.tvTitle1.setText(manga1.getName());

            String imagePath = Environment.getExternalStorageDirectory() + manga1.getSrc();
            File imgFile = new File(imagePath);
            if(imgFile.exists()){
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.imageView1.setImageBitmap(bitmap);
            }
            holder.bind1(manga1, mListener);
        } else {
            holder.cardView1.setVisibility(View.INVISIBLE); // Ẩn nếu không có dữ liệu
        }

        // Đặt dữ liệu cho CardView thứ hai
        if (index + 1 < data.size()) {
            Manga manga2 = data.get(index + 1);
            holder.tvTitle2.setText(manga2.getName());

            String imagePath = Environment.getExternalStorageDirectory() + manga2.getSrc();
            File imgFile = new File(imagePath);
            if(imgFile.exists()){
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.imageView2.setImageBitmap(bitmap);
            }
            holder.bind2(manga2, mListener);
        } else {
            holder.cardView2.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return (data.size() + 1) / 2; // Chia làm 2 cột
    }

    class Holder2 extends RecyclerView.ViewHolder {
        TextView tvTitle1, tvTitle2;
        ImageView imageView1, imageView2;
        CardView cardView1, cardView2;
        public Holder2(@NonNull View itemView) {
            super(itemView);
            cardView1 = itemView.findViewById(R.id.cardView1);
            cardView2 = itemView.findViewById(R.id.cardView2);
            tvTitle1 = itemView.findViewById(R.id.tvTitle1);
            tvTitle2 = itemView.findViewById(R.id.tvTitle2);
            imageView1 = itemView.findViewById(R.id.imageView1);
            imageView2 = itemView.findViewById(R.id.imageView2);
        }
        public void bind1(final Manga manga, final AdapterView.OnItemClickListener listener) {
            cardView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onItemClick(null, view, getAdapterPosition() * 2, getItemId());
                    }
                }
            });
        }

        public void bind2(final Manga manga, final AdapterView.OnItemClickListener listener) {
            cardView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onItemClick(null, view, getAdapterPosition() * 2 + 1, getItemId());
                    }
                }
            });
        }
    }
}
