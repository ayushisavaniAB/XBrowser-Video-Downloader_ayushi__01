package com.privatebrowser.safebrowser.download.video.download;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;

import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.BottomDownloadOptionBinding;
import com.privatebrowser.safebrowser.download.video.databinding.ItemDownloadVideoBinding;
import com.privatebrowser.safebrowser.download.video.utils.Converters;
import com.privatebrowser.safebrowser.download.video.utils.Utility;
import com.privatebrowser.safebrowser.download.video.videos.GlobalVar;
import com.privatebrowser.safebrowser.download.video.videos.PreferencesUtility;
import com.privatebrowser.safebrowser.download.video.videos.model.VideoModel;


public class DownloadVideoListAdapter extends RecyclerView.Adapter {

    public Context context;
    private ArrayList<VideoModel> videoModelList;
    VideoClick videoClick;

    public DownloadVideoListAdapter(Context context, ArrayList<VideoModel> videoModelList) {
        this.context = context;
        this.videoModelList = videoModelList;
    }

    public void add(VideoModel videoModel) {
        videoModelList.add(videoModel);
        notifyItemInserted(videoModelList.size() - 1);
    }

    public interface VideoClick {
        void onClick(int pos);

        void onRenameClick(int pos);

        void onDeleteClick(int pos);
    }

    public void setOnclickListener(VideoClick videoClick) {
        this.videoClick = videoClick;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ItemDownloadVideoBinding videoListBinding;

        public MyViewHolder(ItemDownloadVideoBinding videoListBinding) {
            super(videoListBinding.getRoot());
            this.videoListBinding = videoListBinding;
//            this.videoListBinding.executePendingBindings();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        ItemDownloadVideoBinding videoBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_download_video, null, false);
        ItemDownloadVideoBinding videoBinding = ItemDownloadVideoBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyViewHolder(videoBinding);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        ItemDownloadVideoBinding binding = ((MyViewHolder) holder).videoListBinding;
        VideoModel videoModel = videoModelList.get(position);

        ViewGroup.LayoutParams lp2 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        binding.item.setLayoutParams(lp2);

        if (videoModel.getPath() != null) {
            Glide.with(context).load(videoModel.getPath()).placeholder(R.drawable.no_video).into(binding.videoImage);
        }
        if (videoModel.getName() != null) {
            binding.videoTitleText.setText(videoModel.getName());
        }
        if (videoModel.getLength() != null) {
            binding.videoSizeText.setText(Converters.formatSize(Long.parseLong(videoModel.getLength())));
        }

        binding.videoFormatText.setText(Utility.getFormatType(videoModel.getPath()));
        binding.videoDurationText.setText(Converters.makeShortTimeString(videoModel.getDuration()));

        binding.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog(videoModel, position);
            }
        });

        binding.itemLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoClick != null) {
                    videoClick.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoModelList.size();
    }

    public void showBottomDialog(VideoModel videoModel, int pos) {
//        BottomDownloadOptionBinding optionBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_download_option, null, false);
        BottomDownloadOptionBinding optionBinding = BottomDownloadOptionBinding.inflate(LayoutInflater.from(context));
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(optionBinding.getRoot());
        dialog.show();

        optionBinding.bgPlayMenu.setOnClickListener(v -> {
            PreferencesUtility.getInstance(context).setAllowBackgroundAudio(true);
            GlobalVar.getInstance().videoItemsPlaylist = videoModelList;
            GlobalVar.getInstance().playingVideo = videoModel;
            GlobalVar.getInstance().videoService.playVideo(GlobalVar.getInstance().seekPosition, false);
            dialog.dismiss();
        });

        optionBinding.shareMenu.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            intent.setType("*/*");
            intent.putExtra("android.intent.extra.TEXT", videoModel.getName());
            intent.putExtra("android.intent.extra.STREAM", Utility.getRealUri(context, new File(videoModel.getPath())));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "something is wrong", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        optionBinding.renameMenu.setOnClickListener(v -> {
            if (videoClick != null) {
                videoClick.onRenameClick(pos);
            }
            dialog.dismiss();
        });
        optionBinding.detailMenu.setOnClickListener(v -> {
            dialog.dismiss();
            Utility.detailsDialog(context, videoModel);
        });
        optionBinding.deleteMenu.setOnClickListener(v -> {
            if (videoClick != null) {
                videoClick.onDeleteClick(pos);
            }
            dialog.dismiss();
        });

    }

}
