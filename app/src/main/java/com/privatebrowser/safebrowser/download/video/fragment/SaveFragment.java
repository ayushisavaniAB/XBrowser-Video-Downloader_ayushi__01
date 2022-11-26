package com.privatebrowser.safebrowser.download.video.fragment;

import static android.os.Build.VERSION.SDK_INT;

import android.annotation.SuppressLint;
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
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.nativead.NativeAd;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.adapter.HomeNativeAdAdapter;
import com.privatebrowser.safebrowser.download.video.databinding.DialogDeleteBinding;
import com.privatebrowser.safebrowser.download.video.databinding.DialogRenameBinding;
import com.privatebrowser.safebrowser.download.video.databinding.FragmentSaveBinding;
import com.privatebrowser.safebrowser.download.video.download.DownloadVideoListAdapter;
import com.privatebrowser.safebrowser.download.video.mainUi.BaseActivity;
import com.privatebrowser.safebrowser.download.video.utils.AndroidXI;
import com.privatebrowser.safebrowser.download.video.utils.DataUtils;
import com.privatebrowser.safebrowser.download.video.utils.Utility;
import com.privatebrowser.safebrowser.download.video.videos.GlobalVar;
import com.privatebrowser.safebrowser.download.video.videos.activity.PlayVideoActivity;
import com.privatebrowser.safebrowser.download.video.videos.model.VideoModel;
import j.b;
import think.outside.the.box.handler.APIManager;


public class SaveFragment extends Fragment {

    FragmentSaveBinding downloadBinding;
    String rootPath;
    File[] filesarray;
    DownloadVideoListAdapter downloadVideosAdapter;
    ArrayList<VideoModel> videoModelArrayList;
    ActivityResultLauncher<IntentSenderRequest> launcher;
    VideoModel deleteModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        downloadBinding = FragmentSaveBinding.inflate(inflater, container, false);
        rootPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + getResources().getString(R.string.app_name_text);

        launcher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // All required changes were successfully made
                    MediaScannerConnection.scanFile(requireContext(), new String[]{deleteModel.getPath()}, new String[]{"video/*"}, (MediaScannerConnection.OnScanCompletedListener) null);
                    videoModelArrayList.remove(deleteModel);
                    downloadVideosAdapter.notifyDataSetChanged();
                    Toast.makeText(requireContext(), getString(R.string.video_deleted_toast), Toast.LENGTH_SHORT).show();
                    downloadBinding.swipeRefreshLayout.setRefreshing(true);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new GetDownloadVideo().execute(new Boolean[]{Boolean.FALSE});
                        }
                    }, 500);
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
        return downloadBinding.getRoot();
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
            if (isAdded()) {
                File file = new File(rootPath);
                videoModelArrayList = new ArrayList<>();
                videoModelArrayList = DataUtils.getVideosByFolder(getActivity(), file.getName(), "date_asc");
            }
            return null;
        }

        protected void onPostExecute(String img) {
            if (downloadBinding.swipeRefreshLayout.isRefreshing()) {
                downloadBinding.swipeRefreshLayout.setRefreshing(false);
            }
            if (videoModelArrayList.size() == 0) {
                downloadBinding.noHistory.setVisibility(View.VISIBLE);
                downloadBinding.downloadVideoList.setVisibility(View.GONE);
            } else {
                downloadBinding.noHistory.setVisibility(View.GONE);
                downloadBinding.downloadVideoList.setVisibility(View.VISIBLE);
                if (!isAdded())
                    return;

                downloadVideosAdapter = new DownloadVideoListAdapter(requireContext(), videoModelArrayList);
                HomeNativeAdAdapter fbAdapter = HomeNativeAdAdapter.Builder.with(downloadVideosAdapter, getActivity())
                        .adItemInterval(4)
                        .build();

                APIManager.getNativeObjects(new b() {
                    @Override
                    public void a(@Nullable NativeAd nativeAd) {
                        Log.e("TAG", "a: " + nativeAd);
                        fbAdapter.updateNative(nativeAd);
                        downloadVideosAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void a(boolean b) {

                    }
                });


                downloadBinding.downloadVideoList.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
                downloadBinding.downloadVideoList.setAdapter(fbAdapter);


                downloadVideosAdapter.setOnclickListener(new DownloadVideoListAdapter.VideoClick() {
                    @Override
                    public void onClick(int pos) {
                        GlobalVar.getInstance().videoService.removePopup();
                        //        if (!GlobalVar.getInstance().isPlayingAsPopup()) {
                        APIManager.showInter(getActivity(), false, b -> {
                            GlobalVar.getInstance().videoItemsPlaylist = videoModelArrayList;
                            GlobalVar.getInstance().playingVideo = videoModelArrayList.get(pos);

                            if (GlobalVar.getInstance().videoService != null) {
                                GlobalVar.getInstance().videoService.playVideo(GlobalVar.getInstance().seekPosition, false);
                            }
                            if (GlobalVar.getInstance().videoService != null)
                                GlobalVar.getInstance().videoService.releasePlayerView();

                            Intent intent = new Intent(requireContext(), PlayVideoActivity.class);
                            startActivity(intent);
                        });
                        // }
                        /*     } else {*/
                        //   ((BaseActivity) requireContext()).showFloatingView(requireContext(), true);
                    }
                    //   }

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
        final Dialog dialog = new Dialog(requireContext(), R.style.WideDialog);
//        DialogRenameBinding renameBinding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), R.layout.dialog_rename, null, false);
        DialogRenameBinding renameBinding = DialogRenameBinding.inflate(LayoutInflater.from(requireContext()), null, false);
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
                    Toast.makeText(requireContext(), getString(R.string.empty_edit_toast), Toast.LENGTH_SHORT).show();
                } else if (newFile.exists()) {
                    Toast.makeText(requireContext(), getString(R.string.same_name_toast), Toast.LENGTH_SHORT).show();
                } else {
                    File oldFile = new File(videoModel.getPath());
                    if (oldFile.exists()) {
                        String str = newName + "." + ext;
                        boolean bln = false;
                        if (SDK_INT >= Build.VERSION_CODES.R) {
                            bln = AndroidXI.getInstance().with(requireContext()).rename(oldFile.getPath(), str);
                        } else {
                            bln = oldFile.renameTo(newFile);
                        }
                        MediaScannerConnection.scanFile(requireContext(), new String[]{oldFile.getPath(), newFile.getPath()}, new String[]{"video/*", "video/*"}, (MediaScannerConnection.OnScanCompletedListener) null);
                        videoModel.setPath(newFile.getPath());
                        videoModel.setName(newFile.getName());
                        downloadVideosAdapter.notifyItemChanged(pos, videoModel);
                        downloadBinding.swipeRefreshLayout.setRefreshing(true);
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new GetDownloadVideo().execute(new Boolean[]{Boolean.FALSE});
                            }
                        }, 500);
                        Toast.makeText(requireContext(), getString(R.string.rename_toast), Toast.LENGTH_SHORT).show();
                        APIManager.showInter(getActivity(), false, b -> {
                            dialog.dismiss();
                        });
                    }
                }
            }
        });
    }

    public void deleteDialog(int pos) {
        deleteModel = videoModelArrayList.get(pos);
        final Dialog dialog = new Dialog(requireContext(), R.style.WideDialog);
        DialogDeleteBinding deleteBinding = DialogDeleteBinding.inflate(LayoutInflater.from(requireContext()), null, false);
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
                        AndroidXI.getInstance().with(requireContext()).delete(launcher, oldFile.getPath());
                    } else {
                        bln = oldFile.delete();
                        MediaScannerConnection.scanFile(requireContext(), new String[]{deleteModel.getPath()}, new String[]{"video/*"}, (MediaScannerConnection.OnScanCompletedListener) null);
                        videoModelArrayList.remove(deleteModel);
                        downloadVideosAdapter.notifyItemChanged(pos);
                        downloadVideosAdapter.notifyDataSetChanged();
                        Toast.makeText(requireContext(), getString(R.string.video_deleted_toast), Toast.LENGTH_SHORT).show();
                        downloadBinding.swipeRefreshLayout.setRefreshing(true);
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new GetDownloadVideo().execute(new Boolean[]{Boolean.FALSE});
                            }
                        }, 500);
                    }
                    dialog.dismiss();
                } else {
                    Toast.makeText(requireContext(), getString(R.string.not_exist_toast), Toast.LENGTH_SHORT).show();
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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        if (isAdded()) {
            if (downloadVideosAdapter != null) {
                downloadVideosAdapter.notifyDataSetChanged();
            }
        }
    }
}