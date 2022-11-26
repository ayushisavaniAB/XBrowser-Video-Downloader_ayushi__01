package com.privatebrowser.safebrowser.download.video.videos.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;

import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.BottomAddPlaylistBinding;
import com.privatebrowser.safebrowser.download.video.databinding.BottomVideoOptionBinding;
import com.privatebrowser.safebrowser.download.video.databinding.DialogAddPlaylistBinding;
import com.privatebrowser.safebrowser.download.video.databinding.ItemVideoGridBinding;
import com.privatebrowser.safebrowser.download.video.databinding.ItemVideoListBinding;
import com.privatebrowser.safebrowser.download.video.mainUi.BaseActivity;
import com.privatebrowser.safebrowser.download.video.utils.Converters;
import com.privatebrowser.safebrowser.download.video.utils.PreferenceUtil;
import com.privatebrowser.safebrowser.download.video.utils.Utility;
import com.privatebrowser.safebrowser.download.video.videos.GlobalVar;
import com.privatebrowser.safebrowser.download.video.videos.PreferencesUtility;
import com.privatebrowser.safebrowser.download.video.videos.activity.VideoListActivity;
import com.privatebrowser.safebrowser.download.video.videos.model.VideoFolderModel;
import com.privatebrowser.safebrowser.download.video.videos.model.VideoModel;
import com.privatebrowser.safebrowser.download.video.videos.utils.DataBaseHelper;

public class VideoListAdapter extends RecyclerView.Adapter {

    public Context context;
    private ArrayList<VideoModel> videoModelList;
    DataBaseHelper dataBaseHelper;
    VideoClick videoClick;
    PreferencesUtility preferencesUtility;
    PreferenceUtil prefereceUtil;

    public VideoListAdapter(Context context, ArrayList<VideoModel> videoModelList) {
        this.context = context;
        this.videoModelList = videoModelList;
        dataBaseHelper = new DataBaseHelper(context);
        preferencesUtility = new PreferencesUtility(context);
        prefereceUtil = new PreferenceUtil(context);
    }

    public interface VideoClick {
        void onClick(int pos);

        void onDeleteClick(int pos);

        void onRenameClick(int pos);

    }

    public void setOnclickListener(VideoClick videoClick) {
        this.videoClick = videoClick;
    }

    public class ViewHolderList extends RecyclerView.ViewHolder {

        ItemVideoListBinding videoListBinding;

        public ViewHolderList(ItemVideoListBinding videoListBinding) {
            super(videoListBinding.getRoot());
            this.videoListBinding = videoListBinding;
//            this.videoListBinding.executePendingBindings();
        }
    }

    public class ViewHolderGrid extends RecyclerView.ViewHolder {

        ItemVideoGridBinding videoGridBinding;

        public ViewHolderGrid(ItemVideoGridBinding videoGridBinding) {
            super(videoGridBinding.getRoot());
            this.videoGridBinding = videoGridBinding;
//            this.videoGridBinding.executePendingBindings();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (preferencesUtility.getVideoViewType().equals("List")) {
            ItemVideoListBinding videoListBinding = ItemVideoListBinding.inflate(LayoutInflater.from(context), null, false);
            return new ViewHolderList(videoListBinding);
        } else {
            ItemVideoGridBinding videoGridBinding = ItemVideoGridBinding.inflate(LayoutInflater.from(context), null, false);
            return new ViewHolderGrid(videoGridBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (preferencesUtility.getVideoViewType().equals("List")) {
            ItemVideoListBinding binding = ((ViewHolderList) holder).videoListBinding;
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
            } else {
                binding.videoSizeText.setText(Converters.formatSize(new File(videoModel.getPath()).length()));
            }
            binding.videoFormatText.setText(Utility.getFormatType(videoModel.getPath()));
//            if (videoModel.getResolution() != null) {
//                binding.videoResolutionText.setText(videoModel.getResolution());
//            } else {
//                binding.videoResolutionText.setText(Utility.getVideoResolution(videoModel.getPath()));
//            }
            if (videoModel.getDuration() != 0) {
                binding.videoDurationText.setText(Converters.makeShortTimeString(videoModel.getDuration()));
            } else {
                binding.videoDurationText.setText(Converters.makeShortTimeString(Utility.getDuration(context, new File(videoModel.getPath()))));
            }

            if (dataBaseHelper.isVideoFavorite(videoModel.getPath())) {
                binding.favoriteImage.setVisibility(View.VISIBLE);
            } else {
                binding.favoriteImage.setVisibility(View.GONE);
            }

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
        } else {
            ItemVideoGridBinding binding = ((ViewHolderGrid) holder).videoGridBinding;
            VideoModel videoModel = videoModelList.get(position);

            if (videoModel.getPath() != null) {
                Glide.with(context).load(videoModel.getPath()).into(binding.videoImage);
            }
            if (videoModel.getName() != null) {
                binding.videoTitleText.setText(videoModel.getName());
            }

            if (videoModel.getDuration() != 0) {
                binding.videoDurationText.setText(Converters.makeShortTimeString(videoModel.getDuration()));
            } else {
                binding.videoDurationText.setText(Converters.makeShortTimeString(Utility.getDuration(context, new File(videoModel.getPath()))));
            }

            if (dataBaseHelper.isVideoFavorite(videoModel.getPath())) {
                binding.favoriteImage.setVisibility(View.VISIBLE);
            } else {
                binding.favoriteImage.setVisibility(View.GONE);
            }

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

    }

    @Override
    public int getItemCount() {
        return videoModelList.size();
    }

    public void showBottomDialog(VideoModel videoModel, int pos) {
        BottomVideoOptionBinding optionBinding = BottomVideoOptionBinding.inflate(LayoutInflater.from(context), null, false);
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(optionBinding.getRoot());
        dialog.show();

        if (dataBaseHelper.isVideoFavorite(videoModel.getPath())) {
            optionBinding.favoriteText.setText(context.getString(R.string.remove_favourite_text));
        } else {
            optionBinding.favoriteText.setText(context.getString(R.string.add_favorite_text));
        }

        if (prefereceUtil.getBoolean("show_popup", true)) {
            optionBinding.playPopupMenu.setVisibility(View.VISIBLE);
        } else {
            optionBinding.playPopupMenu.setVisibility(View.GONE);
        }

        if (preferencesUtility.isAllowBackgroundAudio()) {
            optionBinding.bgAudioText.setText("Background Audio On");
        } else {
            optionBinding.bgAudioText.setText("Background Audio Off");
        }

        optionBinding.removeMenu.setVisibility(View.GONE);

        optionBinding.playPopupMenu.setOnClickListener(v -> {
            GlobalVar.getInstance().playingVideo = videoModel;
            GlobalVar.getInstance().videoItemsPlaylist = videoModelList;
            if (context instanceof BaseActivity) {
                ((BaseActivity) context).showFloatingView(context, true);
            }
            dialog.dismiss();
        });
        optionBinding.addPlaylistMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showBottomPlayListDialog(videoModel);
            }
        });
        optionBinding.bgPlayMenu.setOnClickListener(v -> {
            PreferencesUtility.getInstance(context).setAllowBackgroundAudio(true);
            GlobalVar.getInstance().videoItemsPlaylist = videoModelList;
            GlobalVar.getInstance().playingVideo = videoModel;
            GlobalVar.getInstance().videoService.playVideo(GlobalVar.getInstance().seekPosition, false);
            dialog.dismiss();
        });
        optionBinding.favoriteMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataBaseHelper.isVideoFavorite(videoModel.getPath())) {
                    boolean bln = dataBaseHelper.removePlayListVideo(1, videoModel.getPath());
                    if (bln) {
                        dataBaseHelper.updateFavorite(0, videoModel.getPath());
                        Toast.makeText(context, context.getString(R.string.video_remove_favorite_toast), Toast.LENGTH_SHORT).show();
                    }
                    optionBinding.favoriteText.setText(context.getString(R.string.add_favorite_text));
                } else {
                    boolean bln = dataBaseHelper.insertPlayListData(1, Utility.getFileName(videoModel.getName()), videoModel.getName(), videoModel.getPath(), videoModel.getLength(), String.valueOf(videoModel.getDuration()), videoModel.getModifiedDate(), videoModel.getResolution(), 1);
                    dataBaseHelper.updateFavorite(1, videoModel.getPath());
                    Toast.makeText(context, context.getString(R.string.video_add_favorite_toast), Toast.LENGTH_SHORT).show();
                    optionBinding.favoriteText.setText(context.getString(R.string.remove_favourite_text));
                }
                notifyDataSetChanged();
                if (context instanceof VideoListActivity) {
                    ((VideoListActivity) context).SetData();
                }
                dialog.dismiss();
            }
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

    public void showBottomPlayListDialog(VideoModel videoModel) {
        BottomAddPlaylistBinding playlistBinding = BottomAddPlaylistBinding.inflate(LayoutInflater.from(context), null, false);
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(playlistBinding.getRoot());
        dialog.show();

        ArrayList<VideoFolderModel> folderModels;
        folderModels = dataBaseHelper.getPlayListFolderName();
        playlistBinding.folderList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        PlaylistFolderNameAdapter nameAdapter = new PlaylistFolderNameAdapter(context, folderModels);
        playlistBinding.folderList.setAdapter(nameAdapter);

        nameAdapter.setOnclickListener(new PlaylistFolderNameAdapter.FolderClick() {
            @Override
            public void onClick(int pos) {
                VideoFolderModel videoFolderModel = folderModels.get(pos);
                if (dataBaseHelper.isVideoAdded(videoFolderModel.getId(), videoModel.getPath())) {
                    Toast.makeText(context, context.getString(R.string.video_added_toast), Toast.LENGTH_SHORT).show();
                } else {
                    if (dataBaseHelper.isVideoFavorite(videoModel.getPath())) {
                        dataBaseHelper.insertPlayListData(videoFolderModel.getId(), Utility.getFileName(videoModel.getName()), videoModel.getName(), videoModel.getPath(), videoModel.getLength(), String.valueOf(videoModel.getDuration()), videoModel.getModifiedDate(), videoModel.getResolution(), 1);
                    } else {
                        dataBaseHelper.insertPlayListData(videoFolderModel.getId(), Utility.getFileName(videoModel.getName()), videoModel.getName(), videoModel.getPath(), videoModel.getLength(), String.valueOf(videoModel.getDuration()), videoModel.getModifiedDate(), videoModel.getResolution(), 0);
                    }
                    Toast.makeText(context, context.getString(R.string.video_added_toast), Toast.LENGTH_SHORT).show();
                    if (context instanceof VideoListActivity) {
                        ((VideoListActivity) context).SetData();
                    }
                }
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        playlistBinding.addNewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                AddPlayListDialog(videoModel);
            }
        });
    }

    public void AddPlayListDialog(VideoModel videoModel) {
        final Dialog dialog = new Dialog(context, R.style.WideDialog);
        DialogAddPlaylistBinding addPlaylistBinding = DialogAddPlaylistBinding.inflate(LayoutInflater.from(context), null, false);
        dialog.setContentView(addPlaylistBinding.getRoot());
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        addPlaylistBinding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        addPlaylistBinding.createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = addPlaylistBinding.nameEdit.getText().toString();

                if (name.equalsIgnoreCase("")) {
                    Toast.makeText(context, context.getString(R.string.empty_edit_toast), Toast.LENGTH_SHORT).show();
                } else if (dataBaseHelper.isFolderNameExist(name)) {
                    Toast.makeText(context, context.getString(R.string.same_name_toast), Toast.LENGTH_SHORT).show();
                } else {
                    boolean bln = dataBaseHelper.insertPlayListFolderData(name);
                    if (bln) {
                        int fid = dataBaseHelper.getFolderId(name);
                        if (dataBaseHelper.isVideoFavorite(videoModel.getPath())) {
                            dataBaseHelper.insertPlayListData(fid, Utility.getFileName(videoModel.getName()), videoModel.getName(), videoModel.getPath(), videoModel.getLength(), String.valueOf(videoModel.getDuration()), videoModel.getModifiedDate(), videoModel.getResolution(), 1);
                        } else {
                            dataBaseHelper.insertPlayListData(fid, Utility.getFileName(videoModel.getName()), videoModel.getName(), videoModel.getPath(), videoModel.getLength(), String.valueOf(videoModel.getDuration()), videoModel.getModifiedDate(), videoModel.getResolution(), 0);
                        }
                        if (context instanceof VideoListActivity) {
                            ((VideoListActivity) context).SetData();
                        }
                        Toast.makeText(context, context.getString(R.string.video_added_toast), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            }
        });
    }
}
