package com.privatebrowser.safebrowser.download.video.download.video;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Formatter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.DialogDeleteBinding;
import com.privatebrowser.safebrowser.download.video.databinding.DialogRenameBinding;
import com.privatebrowser.safebrowser.download.video.download.DownloadManager;
import com.privatebrowser.safebrowser.download.video.download.DownloadQueues;
import com.privatebrowser.safebrowser.download.video.download.DownloadVideo;
import com.privatebrowser.safebrowser.download.video.mainUi.PlayApp;


public abstract class VideoList {
    private Activity activity;
    private RecyclerView view;
    private List<Video> videos;
//    private VideoDetailsFetcher videoDetailsFetcher = new VideoDetailsFetcher();

    public class Video {
        String size, type, link, name, page, website, details;
        boolean chunked = false, checked = false, expanded = false;
    }

    public abstract void onItemDeleted();

    public VideoList(Activity activity, RecyclerView view) {
        this.activity = activity;
        this.view = view;

        view.setAdapter(new VideoListAdapter());
        view.setLayoutManager(new LinearLayoutManager(activity));
        view.setHasFixedSize(true);

        videos = new ArrayList<>();
    }

    public void recreateVideoList(RecyclerView view) {
        this.view = view;
        view.setAdapter(new VideoListAdapter());
        view.setLayoutManager(new LinearLayoutManager(activity));
//        view.addItemDecoration(Utils.createDivider(activity));
        view.setHasFixedSize(true);
    }

    public void addItem(@Nullable String size, String type, String link, String name, String page,
                        boolean chunked, String website) {
        Video video = new Video();
        video.size = size;
        video.type = type;
        video.link = link;
        video.name = name;
        video.page = page;
        video.chunked = chunked;
        video.website = website;

        boolean duplicate = false;
        for (Video v : videos) {
            if (v.name.equals(video.name)) {
                duplicate = true;
                break;
            }
        }
        if (!duplicate) {
            videos.add(video);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    view.getAdapter().notifyDataSetChanged();
                }
            });
        }
    }

    public int getSize() {
        return videos.size();
    }

    public void deleteCheckedItems() {
        for (int i = 0; i < videos.size(); ) {
            if (videos.get(i).checked) {
                videos.remove(i);
            } else i++;
        }
        ((VideoListAdapter) view.getAdapter()).expandedItem = -1;
        view.getAdapter().notifyDataSetChanged();
    }

    public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoItem> {
        int expandedItem = -1;

        List getVideos() {
            return videos;
        }

        @NonNull
        @Override
        public VideoItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            return (new VideoItem(inflater.inflate(R.layout.item_videos_found, parent,
                    false)));
        }

        @Override
        public void onBindViewHolder(@NonNull VideoItem holder, int position) {
            holder.bind(videos.get(position));
        }

        @Override
        public int getItemCount() {
            return videos.size();
        }

        class VideoItem extends RecyclerView.ViewHolder implements CompoundButton
                .OnCheckedChangeListener, View.OnClickListener, ViewTreeObserver.OnGlobalLayoutListener {
            TextView size;
            TextView name;
            TextView ext;
            CheckBox check;
            View expand;
            ImageView more;
            ProgressBar progress;

            boolean adjustedLayout;

            VideoItem(View itemView) {
                super(itemView);
                adjustedLayout = false;
                size = itemView.findViewById(R.id.videoFoundSize);
                name = itemView.findViewById(R.id.videoFoundName);
                ext = itemView.findViewById(R.id.videoFoundExt);
                check = itemView.findViewById(R.id.videoFoundCheck);
                expand = itemView.findViewById(R.id.videoFoundExpand);
                more = itemView.findViewById(R.id.img_more);
                progress = itemView.findViewById(R.id.videoFoundExtractDetailsProgress);
                check.setOnCheckedChangeListener(this);
                itemView.setOnClickListener(this);
                more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(expand.getVisibility() == View.GONE){
                            expand.setVisibility(View.VISIBLE);
                        }else {
                            expand.setVisibility(View.GONE);
                        }
                    }
                });
                itemView.getViewTreeObserver().addOnGlobalLayoutListener(this);
                size.getViewTreeObserver().addOnGlobalLayoutListener(this);
                ext.getViewTreeObserver().addOnGlobalLayoutListener(this);
                check.getViewTreeObserver().addOnGlobalLayoutListener(this);
            }

            void bind(Video video) {
                if (video.size != null) {
                    String sizeFormatted = Formatter.formatShortFileSize(activity,
                            Long.parseLong(video.size));
                    size.setText(sizeFormatted);
                }
                size.setText(" ");
                String extStr = "." + video.type;
                ext.setText(extStr);
                check.setChecked(video.checked);
                name.setText(video.name);
                if (video.expanded) {
                    expand.setVisibility(View.VISIBLE);
                } else {
                    expand.setVisibility(View.GONE);
                }
                expand.findViewById(R.id.videoFoundRename).setOnClickListener(this);
                expand.findViewById(R.id.videoFoundDownload).setOnClickListener(this);
                expand.findViewById(R.id.videoFoundDelete).setOnClickListener(this);
            }

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                videos.get(getAdapterPosition()).checked = isChecked;
            }

            @Override
            public void onClick(View v) {
                if (v == expand.findViewById(R.id.videoFoundRename)) {

                    final Dialog dialog = new Dialog(activity, R.style.WideDialog);
//                    DialogRenameBinding renameBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.dialog_rename, null, false);
                    DialogRenameBinding renameBinding = DialogRenameBinding.inflate(LayoutInflater.from(activity), null, false);
                    dialog.setContentView(renameBinding.getRoot());
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();

                    renameBinding.nameEdit.setText(name.getText().toString());
                    renameBinding.nameEdit.setSelection(renameBinding.nameEdit.length());

                    renameBinding.cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    renameBinding.okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String name = renameBinding.nameEdit.getText().toString().trim();
                            if(name.equals("")){
                                Toast.makeText(activity,activity.getString(R.string.empty_edit_toast),Toast.LENGTH_SHORT).show();
                            }else {
                                adjustedLayout = false;
                                videos.get(getAdapterPosition()).name = name;
                                notifyItemChanged(getAdapterPosition());
                                dialog.dismiss();
                            }
                        }
                    });
                } else if (v == expand.findViewById(R.id.videoFoundDownload)) {
                        startDownload();
                } else if (v == expand.findViewById(R.id.videoFoundDelete)) {
                    final Dialog dialog = new Dialog(activity, R.style.WideDialog);
                    DialogDeleteBinding deleteBinding = DialogDeleteBinding.inflate(LayoutInflater.from(activity),null, false);
                    dialog.setContentView(deleteBinding.getRoot());
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();

                    deleteBinding.msgText.setText(R.string.delete_this_item);

                    deleteBinding.yesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            videos.remove(getAdapterPosition());
                            expandedItem = -1;
                            notifyDataSetChanged();
                            onItemDeleted();
                            dialog.dismiss();
                        }
                    });

                    deleteBinding.noButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                } else {
                    if (expandedItem != -1) {
                        videos.get(expandedItem).expanded = false;
                        if (expandedItem != getAdapterPosition()) {
                            expandedItem = getAdapterPosition();
                            videos.get(getAdapterPosition()).expanded = true;
                        } else {
                            expandedItem = -1;
                        }
                    } else {
                        expandedItem = getAdapterPosition();
                        videos.get(getAdapterPosition()).expanded = true;
                    }
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onGlobalLayout() {
                if (!adjustedLayout) {
                    if (itemView.getWidth() != 0 && size.getWidth() != 0 && ext.getWidth() != 0 && check
                            .getWidth() != 0) {
                        int totalMargin = (int) TypedValue.applyDimension(TypedValue
                                        .COMPLEX_UNIT_DIP, 12,
                                activity.getResources().getDisplayMetrics());
                        int nameMaxWidth = itemView.getMeasuredWidth() - size.getMeasuredWidth() - ext
                                .getMeasuredWidth() - check.getMeasuredWidth() - totalMargin;
                        name.setMaxWidth(nameMaxWidth);
                        adjustedLayout = true;
                    }
                }

            }

            void startDownload() {
                Video video = videos.get(getAdapterPosition());
                DownloadQueues queues = DownloadQueues.load(activity);
                queues.insertToTop(video.size, video.type, video.link, video.name, video
                        .page, video.chunked, video.website);
                queues.save(activity);
                DownloadVideo topVideo = queues.getTopVideo();
                Intent downloadService = PlayApp.getInstance().getDownloadService();
                DownloadManager.stop();
                downloadService.putExtra("link", topVideo.link);
                downloadService.putExtra("name", topVideo.name);
                downloadService.putExtra("type", topVideo.type);
                downloadService.putExtra("size", topVideo.size);
                downloadService.putExtra("page", topVideo.page);
                downloadService.putExtra("chunked", topVideo.chunked);
                downloadService.putExtra("website", topVideo.website);
                PlayApp.getInstance().startService(downloadService);
                videos.remove(getAdapterPosition());
                expandedItem = -1;
                notifyDataSetChanged();
                onItemDeleted();
//                Toast.makeText(activity, "Downloading video in the background. Check the " +
//                        "Downloads panel to see progress", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void saveCheckedItemsForDownloading() {
        DownloadQueues queues = DownloadQueues.load(activity);
        if (videos.size() == 0) {
            Toast.makeText(activity, "Please select video to download..", Toast.LENGTH_LONG).show();
        } else {
            for (Video video : videos) {
                if (video.checked) {
                    queues.add(video.size, video.type, video.link, video.name, video.page, video
                            .chunked, video.website);
                }
            }

            queues.save(activity);
        }
    }

    public void closeVideoDetailsFetcher() {
//        videoDetailsFetcher.close();
    }
}
