package com.privatebrowser.safebrowser.download.video.fragment;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.FragmentHomeBinding;
import com.privatebrowser.safebrowser.download.video.download.browser.BrowsingActivity;
import com.privatebrowser.safebrowser.download.video.utils.Utility;
import think.outside.the.box.callback.AdsCallback;
import think.outside.the.box.handler.APIManager;

public class HomeFragment extends Fragment {

    FragmentHomeBinding downloadBinding;
    Intent intent;

    public HomeFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        downloadBinding = FragmentHomeBinding.inflate(inflater, container, false);
        downloadBinding.editSearch.setText("");
        downloadBinding.editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (!downloadBinding.editSearch.getText().toString().trim().equals("")) {
                    setIntentActivity(downloadBinding.editSearch.getText().toString());
                } else {
                    downloadBinding.editSearch.setError("It can't be Empty");
                }
                return true;
            }
        });
        downloadBinding.editSearch.setLongClickable(true);
        downloadBinding.editSearch.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipMan = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                downloadBinding.editSearch.setText(clipMan.getText());
                return true;
            }
        });

        downloadBinding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!downloadBinding.editSearch.getText().toString().trim().equals("")) {
                    downloadBinding.editSearch.setFocusable(true);
                    downloadBinding.editSearch.setFocusableInTouchMode(true);
                    downloadBinding.editSearch.requestFocus();
                    Utility.hideSoftKeyboard(getActivity(), downloadBinding.editSearch.getWindowToken());
//                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(downloadBinding.editSearch.getWindowToken(), 0);
                    setIntentActivity(downloadBinding.editSearch.getText().toString());
//                    getActivity().getWindow().getDecorView().setSystemUiVisibility(
//                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                } else {
                    downloadBinding.editSearch.setError("It can't be Empty");
                }
            }
        });


        downloadBinding.facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setIntentActivity("https://m.facebook.com/");
            }
        });

        downloadBinding.dailymotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setIntentActivity("https://www.dailymotion.com/");
            }
        });

        downloadBinding.twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setIntentActivity("https://mobile.twitter.com/");
            }
        });

        downloadBinding.google.setOnClickListener(v -> {
            setIntentActivity("https://www.google.com/");
        });

        downloadBinding.tiktok.setOnClickListener(v -> {
            setIntentActivity("https://www.tiktok.com/");
        });

        downloadBinding.instagram.setOnClickListener(v -> {
            setIntentActivity("https://www.instagram.com/videos/?hl=en");
        });

        downloadBinding.history.setOnClickListener(v -> {
             downloadBinding.historyText.setTextColor(getActivity().getResources().getColor(R.color.darkBlue));
             downloadBinding.bookmarkText.setTextColor(getActivity().getResources().getColor(R.color.colorWhite));
             downloadBinding.historyView.setVisibility(View.VISIBLE);
             downloadBinding.bookmarkView.setVisibility(View.GONE);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.historyFragment, new HistoryFragment()).commit();
        });

        downloadBinding.bookmark.setOnClickListener(v -> {
            downloadBinding.bookmarkText.setTextColor(getActivity().getResources().getColor(R.color.darkBlue));
            downloadBinding.historyText.setTextColor(getActivity().getResources().getColor(R.color.colorWhite));
            downloadBinding.bookmarkView.setVisibility(View.VISIBLE);
            downloadBinding.historyView.setVisibility(View.GONE);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.historyFragment, new BookmarkFragment()).commit();
        });

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.historyFragment, new HistoryFragment()).commit();
        return downloadBinding.getRoot();
    }

    public void setIntentActivity(String data) {
        if (Utility.isNetworkConnected(getActivity())) {

            intent = new Intent(getActivity(), BrowsingActivity.class);
            intent.setData(Uri.parse(data));
            APIManager.showInter(getActivity(), false, (AdsCallback) b -> {
                startActivity(intent);
            });
        } else {
            Toast.makeText(getActivity(), "No Internet Connection, Please Try Again..", Toast.LENGTH_SHORT).show();
        }
    }
}