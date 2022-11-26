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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;


import java.io.File;
import java.util.ArrayList;

import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.ActivitySearchVideoBinding;
import com.privatebrowser.safebrowser.download.video.databinding.DialogDeleteBinding;
import com.privatebrowser.safebrowser.download.video.databinding.DialogRenameBinding;
import com.privatebrowser.safebrowser.download.video.mainUi.BaseActivity;
import com.privatebrowser.safebrowser.download.video.utils.AndroidXI;
import com.privatebrowser.safebrowser.download.video.utils.DataUtils;
import com.privatebrowser.safebrowser.download.video.utils.PreferenceUtil;
import com.privatebrowser.safebrowser.download.video.utils.Utility;
import com.privatebrowser.safebrowser.download.video.videos.GlobalVar;
import com.privatebrowser.safebrowser.download.video.videos.adapter.VideoListAdapter;
import com.privatebrowser.safebrowser.download.video.videos.model.VideoModel;
import com.privatebrowser.safebrowser.download.video.videos.utils.DataBaseHelper;
import think.outside.the.box.callback.AdsCallback;
import think.outside.the.box.handler.APIManager;

public class SearchVideoActivity extends BaseActivity {

    ActivitySearchVideoBinding searchVideoBinding;
    VideoListAdapter videoListAdapter;
    ArrayList<VideoModel> videoModelArrayList;
    ArrayList<VideoModel> searchVideoList;
    Context context;
    InputMethodManager mImm;
    String queryString;
    ActivityResultLauncher<IntentSenderRequest> launcher;
    VideoModel deleteModel;
    DataBaseHelper dataBaseHelper;
    PreferenceUtil prefereceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searchVideoBinding = ActivitySearchVideoBinding.inflate(getLayoutInflater());
        setContentView(searchVideoBinding.getRoot());

        context = SearchVideoActivity.this;

        videoModelArrayList = new ArrayList<>();

        dataBaseHelper = new DataBaseHelper(context);
        prefereceUtil = new PreferenceUtil(context);

        APIManager.showBanner(searchVideoBinding.bannerRelative);

        videoModelArrayList = DataUtils.getAllVideos(context, "name_asc");

        mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // All required changes were successfully made
                            MediaScannerConnection.scanFile(SearchVideoActivity.this, new String[]{deleteModel.getPath()}, new String[]{"video/*"}, (MediaScannerConnection.OnScanCompletedListener) null);
                            dataBaseHelper.isVideoExistandDelete(deleteModel.getPath());
                            videoModelArrayList.remove(deleteModel);
                            videoListAdapter.notifyDataSetChanged();
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    SetData(searchVideoList);
                                }
                            }, 500);

                            Toast.makeText(SearchVideoActivity.this, getString(R.string.video_deleted_toast), Toast.LENGTH_SHORT).show();
                            if (videoModelArrayList.size() == 0) {
                                finish();
                            }
                        } else {
                            Log.e("call else", "onActivityResult: " + result.getResultCode());
                            // The user was asked to change settings, but chose not to
                        }
                    }
                });

        searchVideoBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        searchVideoBinding.searchView.onActionViewExpanded();
        searchVideoBinding.searchView.setQueryHint(getString(R.string.search_title));
        searchVideoBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onQueryTextChange(query);
                hideInputManager();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals(queryString)) {
                    return true;
                }
                queryString = newText;
                if (queryString.trim().equals("")) {
                    searchVideoList.clear();
                    SetData(searchVideoList);
                } else {
                    searchVideo(queryString);
                }
                return true;
            }
        });
    }

    private void searchVideo(String queryString) {
        String regex = "(?i)(bea).*".replace("bea", queryString);
        searchVideoList = new ArrayList<>();
        if (videoModelArrayList == null || videoModelArrayList.size() == 0) return;
        for (VideoModel video : GlobalVar.getInstance().allVideoItems) {
            // higher search result
            if (video.getName().matches(regex)) {
                searchVideoList.add(video);
            }
        }
        for (VideoModel video : videoModelArrayList) {
            if (video.getName().contains(queryString)) {
                searchVideoList.add(video);
            }
        }
        SetData(searchVideoList);
    }

    public void SetData(ArrayList<VideoModel> searchVideoList) {
        if (searchVideoList.size() == 0) {
            searchVideoBinding.noDataText.setVisibility(View.VISIBLE);
            searchVideoBinding.videoList.setVisibility(View.GONE);
        } else {
            searchVideoBinding.noDataText.setVisibility(View.GONE);
            searchVideoBinding.videoList.setVisibility(View.VISIBLE);

            videoListAdapter = new VideoListAdapter(context, searchVideoList);

            searchVideoBinding.videoList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
//            searchVideoBinding.videoList.setAdapter(videoListAdapter);


            searchVideoBinding.videoList.setAdapter(videoListAdapter);

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
                        if (GlobalVar.getInstance().videoService != null) {
                            GlobalVar.getInstance().videoService.playVideo(GlobalVar.getInstance().seekPosition, false);
                        }
                        Intent intent = new Intent(SearchVideoActivity.this, PlayVideoActivity.class);
                        startActivity(intent);
                        if (GlobalVar.getInstance().videoService != null)
                            GlobalVar.getInstance().videoService.releasePlayerView();
                    } else {
                        showFloatingView(SearchVideoActivity.this, true);
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

    public void hideInputManager() {
        if (searchVideoBinding.searchView != null) {
            if (mImm != null) {
                mImm.hideSoftInputFromWindow(searchVideoBinding.searchView.getWindowToken(), 0);
            }
            searchVideoBinding.searchView.clearFocus();
        }
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
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SetData(searchVideoList);
                            }
                        }, 500);
                        dataBaseHelper.isVideoExistandRename(oldFile.getPath(), newFile.getPath(), newName, newName + "." + ext);
                        dataBaseHelper.renameHistoryVideo(oldFile.getPath(), newFile.getPath(), newName, newName + "." + ext);
                        Toast.makeText(context, context.getString(R.string.rename_toast), Toast.LENGTH_SHORT).show();
                        APIManager.showInter(SearchVideoActivity.this, false, b -> {
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
                        AndroidXI.getInstance().with(SearchVideoActivity.this).delete(launcher, oldFile.getPath());
                    } else {
                        bln = oldFile.delete();
                        Toast.makeText(SearchVideoActivity.this, getString(R.string.video_deleted_toast), Toast.LENGTH_SHORT).show();
                        MediaScannerConnection.scanFile(SearchVideoActivity.this, new String[]{deleteModel.getPath()}, new String[]{"video/*"}, (MediaScannerConnection.OnScanCompletedListener) null);
                        dataBaseHelper.isVideoExistandDelete(deleteModel.getPath());
                        videoModelArrayList.remove(deleteModel);
                        videoListAdapter.notifyItemChanged(pos);
                        videoListAdapter.notifyDataSetChanged();
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SetData(searchVideoList);
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
        APIManager.showInter(SearchVideoActivity.this, true, (AdsCallback) b -> {
            finish();
        });
    }
}