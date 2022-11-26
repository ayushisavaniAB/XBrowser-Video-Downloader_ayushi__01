package com.privatebrowser.safebrowser.download.video.videos.activity;

import static android.os.Build.VERSION.SDK_INT;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.nativead.NativeAd;

import java.io.File;
import java.util.ArrayList;

import com.privatebrowser.safebrowser.download.video.Ads.FBNativeAdAdapter;
import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.adapter.HomeNativeAdAdapter;
import com.privatebrowser.safebrowser.download.video.databinding.ActivityVideoListBinding;
import com.privatebrowser.safebrowser.download.video.databinding.DialogDeleteBinding;
import com.privatebrowser.safebrowser.download.video.databinding.DialogRenameBinding;
import com.privatebrowser.safebrowser.download.video.mainUi.BaseActivity;
import com.privatebrowser.safebrowser.download.video.utils.AndroidXI;
import com.privatebrowser.safebrowser.download.video.utils.DataUtils;
import com.privatebrowser.safebrowser.download.video.utils.PreferenceUtil;
import com.privatebrowser.safebrowser.download.video.utils.Utility;
import com.privatebrowser.safebrowser.download.video.videos.GlobalVar;
import com.privatebrowser.safebrowser.download.video.videos.PreferencesUtility;
import com.privatebrowser.safebrowser.download.video.videos.adapter.VideoListAdapter;
import com.privatebrowser.safebrowser.download.video.videos.model.VideoModel;
import com.privatebrowser.safebrowser.download.video.videos.utils.DataBaseHelper;
import j.b;
import think.outside.the.box.callback.AdsCallback;
import think.outside.the.box.handler.APIManager;

public class VideoListActivity extends BaseActivity {

    ActivityVideoListBinding binding;
    VideoListAdapter videoListAdapter;
    ArrayList<VideoModel> videoModelArrayList;
    Context context;
    Intent intent;
    String name;
    PreferencesUtility preferencesUtility;
    ActivityResultLauncher<IntentSenderRequest> launcher;
    VideoModel deleteModel;
    DataBaseHelper dataBaseHelper;
    PreferenceUtil prefereceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityVideoListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = VideoListActivity.this;
        preferencesUtility = new PreferencesUtility(context);
        prefereceUtil = new PreferenceUtil(context);
        dataBaseHelper = new DataBaseHelper(context);

        APIManager.showBanner(binding.bannerRelative);

        videoModelArrayList = new ArrayList<>();

        name = getIntent().getStringExtra("name");

        binding.nameText.setText(name);
        binding.nameText.setSingleLine(true);
        binding.nameText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        binding.nameText.setSelected(true);

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // All required changes were successfully made
                            MediaScannerConnection.scanFile(VideoListActivity.this, new String[]{deleteModel.getPath()}, new String[]{"video/*"}, (MediaScannerConnection.OnScanCompletedListener) null);
                            dataBaseHelper.isVideoExistandDelete(deleteModel.getPath());
                            videoModelArrayList.remove(deleteModel);
                            videoListAdapter.notifyDataSetChanged();

                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    SetData();
                                }
                            }, 500);
                            Toast.makeText(VideoListActivity.this, getString(R.string.video_deleted_toast), Toast.LENGTH_SHORT).show();
                            if (videoModelArrayList.size() == 0) {
                                finish();
                            }
                        } else {
                            Log.e("call else", "onActivityResult: " + result.getResultCode());
                            // The user was asked to change settings, but chose not to
                        }
                    }
                });

        binding.swipeRefreshLayout.setRefreshing(true);
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SetData();
            }
        });
        SetData();

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (preferencesUtility.getVideoViewType().equals("List")) {
            binding.viewButton.setImageResource(R.drawable.ic_grid_view);
        } else {
            binding.viewButton.setImageResource(R.drawable.ic_list_view);
        }

        binding.viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (preferencesUtility.getVideoViewType().equals("List")) {
                    binding.viewButton.setImageResource(R.drawable.ic_list_view);
                    preferencesUtility.setVideoViewType("Grid");
                } else {
                    binding.viewButton.setImageResource(R.drawable.ic_grid_view);
                    preferencesUtility.setVideoViewType("List");
                }
                SetData();
            }
        });


        binding.sortingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.inflate(R.menu.menu_sorting);
                popupMenu.show();

                if (preferencesUtility.getSortingOrder().equals("name_asc")) {
                    popupMenu.getMenu().findItem(R.id.sort_name_asc).setChecked(true);
                } else if (preferencesUtility.getSortingOrder().equals("name_dsc")) {
                    popupMenu.getMenu().findItem(R.id.sort_name_dsc).setChecked(true);
                } else if (preferencesUtility.getSortingOrder().equals("date_asc")) {
                    popupMenu.getMenu().findItem(R.id.sort_date_asc).setChecked(true);
                } else if (preferencesUtility.getSortingOrder().equals("date_dsc")) {
                    popupMenu.getMenu().findItem(R.id.sort_date_dsc).setChecked(true);
                } else if (preferencesUtility.getSortingOrder().equals("size_asc")) {
                    popupMenu.getMenu().findItem(R.id.sort_size_asc).setChecked(true);
                } else if (preferencesUtility.getSortingOrder().equals("size_dsc")) {
                    popupMenu.getMenu().findItem(R.id.sort_size_dsc).setChecked(true);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.sort_name_asc:
                                preferencesUtility.setSortingOrder("name_asc");
                                SetData();
                                break;

                            case R.id.sort_name_dsc:
                                preferencesUtility.setSortingOrder("name_dsc");
                                SetData();
                                break;

                            case R.id.sort_date_asc:
                                preferencesUtility.setSortingOrder("date_asc");
                                SetData();
                                break;

                            case R.id.sort_date_dsc:
                                preferencesUtility.setSortingOrder("date_dsc");
                                SetData();
                                break;

                            case R.id.sort_size_asc:
                                preferencesUtility.setSortingOrder("size_asc");
                                SetData();
                                break;

                            case R.id.sort_size_dsc:
                                preferencesUtility.setSortingOrder("size_dsc");
                                SetData();
                                break;
                        }
                        return true;
                    }
                });
            }
        });
    }

    public void SetData() {
        videoModelArrayList = DataUtils.getVideosByFolder(context, name, preferencesUtility.getSortingOrder());
        binding.swipeRefreshLayout.setRefreshing(false);
        if (videoModelArrayList.size() == 0) {
            binding.noHistory.setVisibility(View.VISIBLE);
            binding.videoList.setVisibility(View.GONE);
        } else {
            binding.noHistory.setVisibility(View.GONE);
            binding.videoList.setVisibility(View.VISIBLE);

            videoListAdapter = new VideoListAdapter(context, videoModelArrayList);

            if (preferencesUtility.getVideoViewType().equals("List")) {

                HomeNativeAdAdapter fbAdapter = HomeNativeAdAdapter.Builder.with(videoListAdapter, this)
                        .adItemInterval(6)
                        .build();


                APIManager.getNativeObjects(new b() {
                    @Override
                    public void a(@Nullable NativeAd nativeAd) {
                        Log.e("TAG", "a: " + nativeAd);
                        fbAdapter.updateNative(nativeAd);
                        videoListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void a(boolean b) {

                    }
                });

                binding.videoList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                binding.videoList.setAdapter(fbAdapter);
            } else {
                HomeNativeAdAdapter fbAdapter = HomeNativeAdAdapter.Builder.with(videoListAdapter, this)
                        .adItemInterval(6)
                        .build();


                APIManager.getNativeObjects(new b() {
                    @Override
                    public void a(@Nullable NativeAd nativeAd) {
                        fbAdapter.updateNative(nativeAd);
                        videoListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void a(boolean b) {

                    }
                });

                GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (fbAdapter.getItemViewType(position) == FBNativeAdAdapter.TYPE_FB_NATIVE_ADS)
                            return 2;
                        return 1;
                    }
                });
                binding.videoList.setLayoutManager(gridLayoutManager);
                binding.videoList.setAdapter(fbAdapter);
            }

            videoListAdapter.setOnclickListener(new VideoListAdapter.VideoClick() {
                @Override
                public void onClick(int pos) {
                    VideoModel videoModel = videoModelArrayList.get(pos);
                    if (prefereceUtil.getBoolean("show_history", true)) {
                        dataBaseHelper.insertHistoryData(videoModel.getTitle(), videoModel.getName(), videoModel.getPath(), videoModel.getLength(), String.valueOf(videoModel.getDuration()), videoModel.getModifiedDate(), videoModel.getResolution());
                    }
                    GlobalVar.getInstance().videoItemsPlaylist = videoModelArrayList;
                    GlobalVar.getInstance().playingVideo = videoModel;
                    if (!GlobalVar.getInstance().isPlayingAsPopup()) {
                        startPlayingActivity();
                    } else {
                        stopPopupMode();
                        startPlayingActivity();
//                        showFloatingView(VideoListActivity.this, false);
                    }
                }

                @Override
                public void onDeleteClick(int pos) {
                    deleteDialog(pos);
                }

                @Override
                public void onRenameClick(int pos) {
                    renameDialog(pos);
                }

            });
        }
    }

    private void startPlayingActivity() {
        if (GlobalVar.getInstance().videoService != null) {
            GlobalVar.getInstance().videoService.playVideo(GlobalVar.getInstance().seekPosition, false);
        }
        Intent intent = new Intent(VideoListActivity.this, PlayVideoActivity.class);
        APIManager.showInter(VideoListActivity.this, false, b -> {
            startActivity(intent);
        });
        if (GlobalVar.getInstance().videoService != null)
            GlobalVar.getInstance().videoService.releasePlayerView();
    }

    public void renameDialog(int pos) {
        VideoModel videoModel = videoModelArrayList.get(pos);
        final Dialog dialog = new Dialog(context, R.style.WideDialog);
        DialogRenameBinding renameBinding = DialogRenameBinding.inflate(LayoutInflater.from(context), null, false);
        dialog.setContentView(renameBinding.getRoot());
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        renameBinding.nameEdit.setText(Utility.getFileName(videoModel.getName()));
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
                String newName = renameBinding.nameEdit.getText().toString().trim();
                File folder = new File(videoModel.getPath()).getParentFile();
                String ext = Utility.getFileExtension(videoModel.getName());
                File newFile = new File(folder + "/" + newName + "." + ext);
                if (newName.equalsIgnoreCase("")) {
                    Toast.makeText(context, context.getString(R.string.empty_edit_toast), Toast.LENGTH_SHORT).show();
                } else if (newFile.exists()) {
                    Toast.makeText(context, context.getString(R.string.same_name_toast), Toast.LENGTH_SHORT).show();
                } else {
                    File oldFile = new File(videoModel.getPath());
                    String str = newName + "." + ext;
                    if (oldFile.exists()) {
                        boolean bln = false;
                        if (SDK_INT >= Build.VERSION_CODES.R) {
                            bln = AndroidXI.getInstance().with(context).rename(oldFile.getPath(), str);
                        } else {
                            bln = oldFile.renameTo(newFile);
                        }
                        MediaScannerConnection.scanFile(context, new String[]{oldFile.getPath(), newFile.getPath()}, new String[]{"video/*", "video/*"}, (MediaScannerConnection.OnScanCompletedListener) null);
                        videoModel.setPath(newFile.getPath());
                        videoModel.setName(newFile.getName());
                        videoListAdapter.notifyItemChanged(pos, videoModel);
                        videoListAdapter.notifyDataSetChanged();
                        binding.swipeRefreshLayout.setRefreshing(true);
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SetData();
                            }
                        }, 500);
                        dataBaseHelper.isVideoExistandRename(oldFile.getPath(), newFile.getPath(), newName, newName + "." + ext);
                        dataBaseHelper.renameHistoryVideo(oldFile.getPath(), newFile.getPath(), newName, newName + "." + ext);
                        Toast.makeText(context, context.getString(R.string.rename_toast), Toast.LENGTH_SHORT).show();
                        APIManager.showInter(VideoListActivity.this, false, b -> {
                            dialog.dismiss();
                        });
                    }
                }
            }
        });
    }

    public void deleteDialog(int pos) {
        deleteModel = videoModelArrayList.get(pos);
        final Dialog dialog = new Dialog(context, R.style.WideDialog);
        DialogDeleteBinding deleteBinding = DialogDeleteBinding.inflate(LayoutInflater.from(context), null, false);
        dialog.setContentView(deleteBinding.getRoot());
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        deleteBinding.msgText.setText(context.getString(R.string.confirm) + " \"" + deleteModel.getPath() + "\" ?");
        deleteBinding.noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        deleteBinding.yesButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onClick(View view) {
                File oldFile = new File(deleteModel.getPath());
                if (oldFile.exists()) {
                    boolean bln = false;
                    if (SDK_INT >= Build.VERSION_CODES.R) {
                        AndroidXI.getInstance().with(VideoListActivity.this).delete(launcher, oldFile.getPath());
                    } else {
                        bln = oldFile.delete();
                        Toast.makeText(VideoListActivity.this, getString(R.string.video_deleted_toast), Toast.LENGTH_SHORT).show();
                        MediaScannerConnection.scanFile(VideoListActivity.this, new String[]{deleteModel.getPath()}, new String[]{"video/*"}, (MediaScannerConnection.OnScanCompletedListener) null);
                        dataBaseHelper.isVideoExistandDelete(deleteModel.getPath());
                        videoModelArrayList.remove(deleteModel);
                        videoListAdapter.notifyItemChanged(pos);
                        videoListAdapter.notifyDataSetChanged();
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SetData();
                            }
                        }, 500);
                        if (videoModelArrayList.size() == 0) {
                            finish();
                        }
                    }
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, context.getString(R.string.not_exist_toast), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        APIManager.showInter(VideoListActivity.this, true, (AdsCallback) b -> {
            finish();
        });
    }
}