package com.privatebrowser.safebrowser.download.video.download;

import static android.os.Build.VERSION.SDK_INT;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.privatebrowser.safebrowser.download.video.databinding.ActivityShowDownloadBinding;
import com.privatebrowser.safebrowser.download.video.databinding.DialogDeleteBinding;
import com.privatebrowser.safebrowser.download.video.databinding.DialogRenameBinding;
import com.privatebrowser.safebrowser.download.video.mainUi.BaseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.utils.AndroidXI;
import com.privatebrowser.safebrowser.download.video.utils.Utility;
import com.privatebrowser.safebrowser.download.video.videos.GlobalVar;
import com.privatebrowser.safebrowser.download.video.videos.activity.PlayVideoActivity;
import com.privatebrowser.safebrowser.download.video.videos.model.VideoModel;
import think.outside.the.box.callback.AdsCallback;
import think.outside.the.box.handler.APIManager;

public class ShowDownloadActivity extends BaseActivity {

    ActivityShowDownloadBinding downloadBinding;
    File[] filesarray;
    String rootPath;
    DownloadVideoListAdapter downloadVideosAdapter;
    ArrayList<VideoModel> videoModelArrayList;
    ActivityResultLauncher<IntentSenderRequest> launcher;
    VideoModel deleteModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        downloadBinding = ActivityShowDownloadBinding.inflate(getLayoutInflater());
        setContentView(downloadBinding.getRoot());

        downloadBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        downloadBinding.nameText.getPaint().setShader(Utility.setTextGradient(ShowDownloadActivity.this, downloadBinding.nameText));

        rootPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + getResources().getString(R.string.app_name_text);

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // All required changes were successfully made
                            MediaScannerConnection.scanFile(ShowDownloadActivity.this, new String[]{deleteModel.getPath()}, new String[]{"video/*"}, (MediaScannerConnection.OnScanCompletedListener) null);
                            videoModelArrayList.remove(deleteModel);
                            downloadVideosAdapter.notifyDataSetChanged();
                            Toast.makeText(ShowDownloadActivity.this, getString(R.string.video_deleted_toast), Toast.LENGTH_SHORT).show();
                            downloadBinding.swipeRefreshLayout.setRefreshing(true);
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    new GetDownloadVideo().execute(new Boolean[]{Boolean.FALSE});
                                }
                            },500);
                        } else {
                            Log.e("call else", "onActivityResult: " + result.getResultCode());
                            // The user was asked to change settings, but chose not to
                        }
                    }
                });

        downloadBinding.swipeRefreshLayout.setRefreshing(true);
        downloadBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetDownloadVideo().execute(new Boolean[]{Boolean.FALSE});
            }
        });
        new GetDownloadVideo().execute(new Boolean[]{Boolean.FALSE});
    }

    private class GetDownloadVideo extends AsyncTask<Boolean, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            if (!downloadBinding.swipeRefreshLayout.isRefreshing()) {
                downloadBinding.swipeRefreshLayout.setRefreshing(true);
            }
        }

        @Override
        protected String doInBackground(Boolean... params) {
            videoModelArrayList = new ArrayList<>();
            filesarray = new File(rootPath).listFiles();
            if (filesarray != null) {
                Arrays.sort(filesarray, new sorting());
                for (File file : filesarray) {
                    VideoModel videoModel = new VideoModel();
                    videoModel.setTitle(Utility.getNameFromFile(file.getName()));
                    videoModel.setName(file.getName());
                    videoModel.setPath(file.getPath());
                    videoModel.setDuration(Utility.getDuration(ShowDownloadActivity.this, file));
                    videoModel.setLength(String.valueOf(file.length()));
                    videoModel.setModifiedDate(String.valueOf(file.lastModified()));
                    videoModelArrayList.add(videoModel);
                }
            }
            return null;
        }

        protected void onPostExecute(String img) {
//            Log.e("TAG", "onPostExecute: " + videoList.size());
            if (downloadBinding.swipeRefreshLayout.isRefreshing()) {
                downloadBinding.swipeRefreshLayout.setRefreshing(false);
            }
            if (videoModelArrayList.size() == 0) {
                downloadBinding.noDataText.setVisibility(View.VISIBLE);
                downloadBinding.downloadVideoList.setVisibility(View.GONE);
            } else {
                downloadBinding.noDataText.setVisibility(View.GONE);
                downloadBinding.downloadVideoList.setVisibility(View.VISIBLE);

                downloadVideosAdapter = new DownloadVideoListAdapter(ShowDownloadActivity.this, videoModelArrayList);
                downloadBinding.downloadVideoList.setLayoutManager(new LinearLayoutManager(ShowDownloadActivity.this, LinearLayoutManager.VERTICAL, false));
                downloadBinding.downloadVideoList.setAdapter(downloadVideosAdapter);

                downloadVideosAdapter.setOnclickListener(new DownloadVideoListAdapter.VideoClick() {
                    @Override
                    public void onClick(int pos) {
                        GlobalVar.getInstance().videoItemsPlaylist = videoModelArrayList;
                        GlobalVar.getInstance().playingVideo = videoModelArrayList.get(pos);
                        if (!GlobalVar.getInstance().isPlayingAsPopup()) {
                            if (GlobalVar.getInstance().videoService != null) {
                                GlobalVar.getInstance().videoService.playVideo(GlobalVar.getInstance().seekPosition, false);
                            }
                            Intent intent = new Intent(ShowDownloadActivity.this, PlayVideoActivity.class);
                            startActivity(intent);

                            if (GlobalVar.getInstance().videoService != null)
                                GlobalVar.getInstance().videoService.releasePlayerView();
                        } else {
                            showFloatingView(ShowDownloadActivity.this, true);
                        }
                    }

                    @Override
                    public void onRenameClick(int pos) {
                        renameDialog(pos);
                    }

                    @Override
                    public void onDeleteClick(int pos) {
                        deleteDialog(pos);
                    }
                });

            }
        }
    }

    public void renameDialog(int pos) {
        VideoModel videoModel = videoModelArrayList.get(pos);
        final Dialog dialog = new Dialog(ShowDownloadActivity.this, R.style.WideDialog);
//        DialogRenameBinding renameBinding = DataBindingUtil.inflate(LayoutInflater.from(ShowDownloadActivity.this), R.layout.dialog_rename, null, false);
        DialogRenameBinding renameBinding = DialogRenameBinding.inflate(LayoutInflater.from(ShowDownloadActivity.this),null,false);
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
                    Toast.makeText(ShowDownloadActivity.this, getString(R.string.empty_edit_toast), Toast.LENGTH_SHORT).show();
                } else if (newFile.exists()) {
                    Toast.makeText(ShowDownloadActivity.this, getString(R.string.same_name_toast), Toast.LENGTH_SHORT).show();
                } else {
                    File oldFile = new File(videoModel.getPath());
                    if (oldFile.exists()) {
                        String str = newName + "." + ext;
                        boolean bln = false;
                        if (SDK_INT >= Build.VERSION_CODES.R) {
                            bln = AndroidXI.getInstance().with(ShowDownloadActivity.this).rename(oldFile.getPath(), str);
                        } else {
                            bln = oldFile.renameTo(newFile);
                        }
                        MediaScannerConnection.scanFile(ShowDownloadActivity.this, new String[]{oldFile.getPath(), newFile.getPath()}, new String[]{"video/*", "video/*"}, (MediaScannerConnection.OnScanCompletedListener) null);
                        videoModel.setPath(newFile.getPath());
                        videoModel.setName(newFile.getName());
                        downloadVideosAdapter.notifyItemChanged(pos, videoModel);
                        downloadBinding.swipeRefreshLayout.setRefreshing(true);
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new GetDownloadVideo().execute(new Boolean[]{Boolean.FALSE});
                            }
                        },500);
                        Toast.makeText(ShowDownloadActivity.this, getString(R.string.rename_toast), Toast.LENGTH_SHORT).show();
                        APIManager.showInter(ShowDownloadActivity.this, false, b -> {
                            dialog.dismiss();
                        });
                    }
                }
            }
        });
    }

    public void deleteDialog(int pos) {
        deleteModel = videoModelArrayList.get(pos);
        final Dialog dialog = new Dialog(ShowDownloadActivity.this, R.style.WideDialog);
        DialogDeleteBinding deleteBinding = DialogDeleteBinding.inflate(LayoutInflater.from(ShowDownloadActivity.this),  null, false);
        dialog.setContentView(deleteBinding.getRoot());
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        deleteBinding.msgText.setText(getString(R.string.confirm) + "\" " + deleteModel.getPath() + " \" ?");
        deleteBinding.noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        deleteBinding.yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File oldFile = new File(deleteModel.getPath());
                if (oldFile.exists()) {
                    boolean bln = false;
                    if (SDK_INT >= Build.VERSION_CODES.R) {
                        AndroidXI.getInstance().with(ShowDownloadActivity.this).delete(launcher,oldFile.getPath());
                    } else {
                        bln = oldFile.delete();
                        MediaScannerConnection.scanFile(ShowDownloadActivity.this, new String[]{deleteModel.getPath()}, new String[]{"video/*"}, (MediaScannerConnection.OnScanCompletedListener) null);
                        videoModelArrayList.remove(deleteModel);
                        downloadVideosAdapter.notifyItemChanged(pos);
                        downloadVideosAdapter.notifyDataSetChanged();
                        Toast.makeText(ShowDownloadActivity.this, getString(R.string.video_deleted_toast), Toast.LENGTH_SHORT).show();
                        downloadBinding.swipeRefreshLayout.setRefreshing(true);
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new GetDownloadVideo().execute(new Boolean[]{Boolean.FALSE});
                            }
                        },500);
                    }
                    dialog.dismiss();
                } else {
                    Toast.makeText(ShowDownloadActivity.this, getString(R.string.not_exist_toast), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class sorting implements Comparator {
        sorting() {
        }

        public int compare(Object o1, Object o2) {
            if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                return -1;
            }
            if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                return 1;
            }
            return 0;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetDownloadVideo().execute(new Boolean[]{Boolean.FALSE});
    }

    @Override
    public void onBackPressed() {
        APIManager.showInter(ShowDownloadActivity.this, true, (AdsCallback) b -> {
            finish();
        });
    }
}