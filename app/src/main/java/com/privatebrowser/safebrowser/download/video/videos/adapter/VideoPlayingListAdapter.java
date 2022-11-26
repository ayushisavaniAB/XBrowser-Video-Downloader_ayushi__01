package com.privatebrowser.safebrowser.download.video.videos.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.ItemVideoPlayingBinding;
import com.privatebrowser.safebrowser.download.video.utils.Converters;
import com.privatebrowser.safebrowser.download.video.videos.GlobalVar;
import com.privatebrowser.safebrowser.download.video.videos.model.VideoModel;

public class VideoPlayingListAdapter extends RecyclerView.Adapter<VideoPlayingListAdapter.ItemHolder> {

    Activity context;
    private ArrayList<VideoModel> videoItems = new ArrayList<>();

    public VideoPlayingListAdapter(Activity context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        ItemVideoPlayingBinding playBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_video_playing, null,false);
        ItemVideoPlayingBinding playBinding = ItemVideoPlayingBinding.inflate(LayoutInflater.from(context));
        return new ItemHolder(playBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {

        ItemVideoPlayingBinding binding = ((ItemHolder) itemHolder).playBinding;

        VideoModel videoModel = videoItems.get(i);
        if (videoModel.getPath() != null) {
            binding.txtVideoTitle.setText(videoModel.getName());
            Glide.with(context.getApplicationContext())
                    .load(videoModel.getPath())
                    .placeholder(R.drawable.no_video)
                    .into(binding.videoImage);
            binding.txtVideoPath.setText(videoModel.getPath());
        }

        binding.videoDurationText.setText(Converters.makeShortTimeString(videoModel.getDuration()));

        binding.itemLayout.setOnClickListener(v -> {
            GlobalVar.getInstance().playingVideo = videoModel;
            GlobalVar.getInstance().videoService.playVideo(0, false);
        });

        binding.removeVideoButton.setOnClickListener(v -> {
            videoItems.remove(i);
            GlobalVar.getInstance().videoItemsPlaylist = videoItems;
            notifyDataSetChanged();
            updateData(videoItems);
        });

    }

    public void updateData(ArrayList<VideoModel> items) {
        if (items == null) items = new ArrayList<>();
        ArrayList<VideoModel> r = new ArrayList<>(items);
        int currentSize = videoItems.size();
        if (currentSize != 0) {
            this.videoItems.clear();
            this.videoItems.addAll(r);
            notifyItemRangeRemoved(0, currentSize);
            //tell the recycler view how many new items we added
            notifyItemRangeInserted(0, r.size());
        } else {
            this.videoItems.addAll(r);
            notifyItemRangeInserted(0, r.size());
        }

    }

    @Override
    public int getItemCount() {
        return videoItems.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        ItemVideoPlayingBinding playBinding;

        public ItemHolder(ItemVideoPlayingBinding playBinding) {
            super(playBinding.getRoot());
            this.playBinding = playBinding;
//            this.playBinding.executePendingBindings();
        }

    }

}
