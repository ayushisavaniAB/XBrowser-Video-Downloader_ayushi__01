package com.privatebrowser.safebrowser.download.video.videos.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import com.privatebrowser.safebrowser.download.video.databinding.ItemVideoFolderGridBinding;
import com.privatebrowser.safebrowser.download.video.databinding.ItemVideoFolderListBinding;
import com.privatebrowser.safebrowser.download.video.videos.PreferencesUtility;
import com.privatebrowser.safebrowser.download.video.videos.model.VideoFolderModel;


public class VideoFolderAdapter extends RecyclerView.Adapter {

    public Context context;
    private List<VideoFolderModel> videoFolderModelList;
    public FolderClick folderClick;
    PreferencesUtility preferencesUtility;

    public VideoFolderAdapter(Context context, List<VideoFolderModel> videoFolderModelList) {
        this.context = context;
        this.videoFolderModelList = videoFolderModelList;
        preferencesUtility = new PreferencesUtility(context);
    }

    public interface FolderClick {
        void onClick(int pos);
    }

    public void setOnclickListener(FolderClick folderClick) {
        this.folderClick = folderClick;
    }

    public class ViewHolderGrid extends RecyclerView.ViewHolder {

        ItemVideoFolderGridBinding videoFolderGridBinding;

        public ViewHolderGrid(ItemVideoFolderGridBinding videoFolderGridBinding) {
            super(videoFolderGridBinding.getRoot());
            this.videoFolderGridBinding = videoFolderGridBinding;
//            this.videoFolderGridBinding.executePendingBindings();
        }
    }

    public class ViewHolderList extends RecyclerView.ViewHolder {

        ItemVideoFolderListBinding videoFolderListBinding;

        public ViewHolderList(ItemVideoFolderListBinding videoFolderListBinding) {
            super(videoFolderListBinding.getRoot());
            this.videoFolderListBinding = videoFolderListBinding;
//            this.videoFolderListBinding.executePendingBindings();

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (preferencesUtility.getVideoFolderViewType().equals("List")) {
            ItemVideoFolderListBinding videoFolderListBinding = ItemVideoFolderListBinding.inflate(LayoutInflater.from(context), null, false);
            return new ViewHolderList(videoFolderListBinding);
        } else {
            ItemVideoFolderGridBinding videoFolderGridBinding = ItemVideoFolderGridBinding.inflate(LayoutInflater.from(context),  null, false);
            return new ViewHolderGrid(videoFolderGridBinding);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (preferencesUtility.getVideoFolderViewType().equals("List")) {
            ItemVideoFolderListBinding binding = ((ViewHolderList) holder).videoFolderListBinding;
            VideoFolderModel videoFolderModel = videoFolderModelList.get(position);

            ViewGroup.LayoutParams lp2 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            binding.itemLinear.setLayoutParams(lp2);

            if (videoFolderModel.getName() != null) {
                binding.folderNameText.setText(videoFolderModel.getName());
            }
            binding.countText.setText("( " + videoFolderModel.getCount() + " )");

            binding.itemLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (folderClick != null) {
                        folderClick.onClick(position);
                    }
                }
            });
        } else {

            ItemVideoFolderGridBinding binding = ((ViewHolderGrid) holder).videoFolderGridBinding;
            VideoFolderModel videoFolderModel = videoFolderModelList.get(position);

            if (videoFolderModel.getName() != null) {
                binding.folderNameText.setText(videoFolderModel.getName());
            }
            binding.countText.setText(videoFolderModel.getCount() + "");

            binding.itemLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (folderClick != null) {
                        folderClick.onClick(position);
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return videoFolderModelList.size();
    }
}
