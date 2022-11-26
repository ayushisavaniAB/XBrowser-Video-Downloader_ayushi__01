package com.privatebrowser.safebrowser.download.video.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import com.privatebrowser.safebrowser.download.video.R;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.Myclass> {

    private Context context;
    private String[] languageList;
    private int[] countryFlag;
    private Callback callback;
    int selectItem = 0;

    public interface Callback{
        void onItemClick(int position);
    }

    public LanguageAdapter(Context context, String[] languageList, int[] countryFlag, Callback callback) {

        this.context = context;
        this.languageList = languageList;
        this.countryFlag = countryFlag;
        this.callback = callback;
    }

    @NonNull
    @Override
    public Myclass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.language_item, parent, false);
        Myclass myclass = new Myclass(view);
        return myclass;
    }

    @Override
    public void onBindViewHolder(@NonNull Myclass holder, @SuppressLint("RecyclerView") int position) {
        holder.flagImage.setImageResource(countryFlag[position]);
        holder.language_text.setText(languageList[position]);
        holder.languageRelative.setOnClickListener(v -> {
            selectItem = position;
            callback.onItemClick(selectItem);
        });
        if (selectItem == position) {
            holder.languageRelative.setBackgroundColor(context.getResources().getColor(R.color.selctorColor));
        } else {
            holder.languageRelative.setBackgroundColor(context.getResources().getColor(R.color.unselectedColor));
        }
    }

    @Override
    public int getItemCount() {
        return countryFlag.length;
    }

    public class Myclass extends RecyclerView.ViewHolder {
        private RelativeLayout languageRelative;
        private ImageView flagImage;
        MaterialTextView language_text;

        public Myclass(@NonNull View itemView) {
            super(itemView);
            languageRelative = (RelativeLayout) itemView.findViewById(R.id.languageRelative);
            flagImage = (ImageView) itemView.findViewById(R.id.flag_image);
            language_text = (MaterialTextView) itemView.findViewById(R.id.language_text);
        }
    }
}
