package com.privatebrowser.safebrowser.download.video.videos.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.privatebrowser.safebrowser.download.video.databinding.ItemPlaylistFolderBinding;
import com.privatebrowser.safebrowser.download.video.videos.model.VideoFolderModel;


public class PlaylistFolderNameAdapter extends RecyclerView.Adapter {

    public Context context;
    private List<VideoFolderModel> videoFolderModelList;
    public FolderClick folderClick;

    public PlaylistFolderNameAdapter(Context context, List<VideoFolderModel> videoFolderModelList) {
        this.context = context;
        this.videoFolderModelList = videoFolderModelList;
    }

    public interface FolderClick {
        void onClick(int pos);
    }

    public void setOnclickListener(FolderClick folderClick) {
        this.folderClick = folderClick;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemPlaylistFolderBinding folderBinding;

        public ViewHolder(ItemPlaylistFolderBinding folderBinding) {
            super(folderBinding.getRoot());
            this.folderBinding = folderBinding;
//            this.folderBinding.executePendingBindings();

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPlaylistFolderBinding folderBinding = ItemPlaylistFolderBinding.inflate(LayoutInflater.from(context), null, false);
        return new ViewHolder(folderBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        ItemPlaylistFolderBinding binding = ((ViewHolder) holder).folderBinding;
        VideoFolderModel videoFolderModel = videoFolderModelList.get(position);

        ViewGroup.LayoutParams lp2 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        binding.item.setLayoutParams(lp2);

        if (videoFolderModel.getName() != null) {
            binding.folderText.setText(videoFolderModel.getName());
        }

        binding.folderMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (folderClick != null) {
                    folderClick.onClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return videoFolderModelList.size();
    }
}
