package com.example.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.btl_appmanga.R;

import java.io.File;
import java.util.List;

public class ImgAdapter extends ArrayAdapter<String> {
    public ImgAdapter(Context context, List<String> imgPath) {
        super(context, 0, imgPath);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String s = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.img_item, parent, false);
        }
        ImageView imgView = convertView.findViewById(R.id.imgViewItemChapter);
        File img = new File(s);
        if(img.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(img.getAbsolutePath());
            imgView.setImageBitmap(bitmap);
        }
        return convertView;
    }
}
