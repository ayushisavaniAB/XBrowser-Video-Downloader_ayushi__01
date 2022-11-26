package com.privatebrowser.safebrowser.download.video.download.browser;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.ActivityOpenWindowBinding;
import com.privatebrowser.safebrowser.download.video.databinding.DialogDeleteBinding;
import think.outside.the.box.callback.AdsCallback;
import think.outside.the.box.handler.APIManager;
import think.outside.the.box.ui.BaseActivity;

public class OpenWindowActivity extends BaseActivity {

    ActivityOpenWindowBinding openWindowBinding;
    BrowserManager browserManager;
    private List<BrowserWindow> windows;

    boolean aBoolean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openWindowBinding = ActivityOpenWindowBinding.inflate(getLayoutInflater());
        setContentView(openWindowBinding.getRoot());

        browserManager = BrowserWindow.browserManager;

        windows = browserManager.getAllWindow();

        if (windows.size() == 0) {
            openWindowBinding.noDataText.setVisibility(View.VISIBLE);
            openWindowBinding.windowList.setVisibility(View.GONE);
        } else {
            openWindowBinding.noDataText.setVisibility(View.GONE);
            openWindowBinding.windowList.setVisibility(View.VISIBLE);
        }

//        openWindowBinding.nameText.getPaint().setShader(Utility.setTextGradient(OpenWindowActivity.this, openWindowBinding.nameText));

        openWindowBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        openWindowBinding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(OpenWindowActivity.this, R.style.WideDialog);
                DialogDeleteBinding deleteBinding = DialogDeleteBinding.inflate(LayoutInflater.from(OpenWindowActivity.this), null, false);
                dialog.setContentView(deleteBinding.getRoot());
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                deleteBinding.titleText.setText(R.string.alert);
                deleteBinding.msgText.setText(R.string.all_window_close);

                deleteBinding.yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        browserManager.closeWindowAll();
                        windows = browserManager.getAllWindow();
                        dialog.dismiss();
                        APIManager.showInter(OpenWindowActivity.this, false, (AdsCallback) b -> {
                            if (windows.size() == 0) {
                                setResult(123);
                                finish();
                            }else {
                            finish();}
                        });
                    }
                });

                deleteBinding.noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        openWindowBinding.windowList.setLayoutManager(new LinearLayoutManager(OpenWindowActivity.this, RecyclerView.VERTICAL, false));
        openWindowBinding.windowList.setAdapter(new AllWindowsAdapter());
    }

    private class AllWindowsAdapter extends RecyclerView.Adapter<WindowItem> {

        @NonNull
        @Override
        public WindowItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(OpenWindowActivity.this);
            View item = inflater.inflate(R.layout.item_browser_openwindow, parent, false);
            return new WindowItem(item);
        }

        @Override
        public void onBindViewHolder(@NonNull WindowItem holder, @SuppressLint("RecyclerView") int position) {
            holder.bind(windows.get(position).getWebView());

            holder.closeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(OpenWindowActivity.this, R.style.WideDialog);
                    DialogDeleteBinding deleteBinding = DialogDeleteBinding.inflate(LayoutInflater.from(OpenWindowActivity.this), null, false);
                    dialog.setContentView(deleteBinding.getRoot());
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();

                    deleteBinding.titleText.setText(R.string.alert);
                    deleteBinding.msgText.setText(R.string.close_this_window);

                    deleteBinding.yesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            APIManager.showInter(OpenWindowActivity.this, false, isfail -> {
                                browserManager.closeWindow(windows.get(position));
                                windows = browserManager.getAllWindow();
                                notifyDataSetChanged();
                                dialog.dismiss();
                                if (windows.size() == 0) {
                                    finish();
                                }
                            });

                        }
                    });

                    deleteBinding.noButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            APIManager.showInter(OpenWindowActivity.this, false, isfail -> {
                                dialog.dismiss();
                            });

                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return windows.size();
        }
    }

    private class WindowItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView windowTitle;
        ImageView favicon;
        ImageView closeIcon;

        WindowItem(View itemView) {
            super(itemView);
            windowTitle = itemView.findViewById(R.id.windowTitle);
            favicon = itemView.findViewById(R.id.favicon);
            closeIcon = itemView.findViewById(R.id.closeButton);
            itemView.setOnClickListener(this);

        }

        void bind(WebView webView) {
            windowTitle.setText(webView.getTitle());
            favicon.setImageBitmap(webView.getFavicon());
            Glide.with(OpenWindowActivity.this).load(webView.getFavicon()).placeholder(R.drawable.google_icon).error(R.drawable.google_icon).into(favicon);

        }

        @Override
        public void onClick(View v) {
            browserManager.switchWindow(getAdapterPosition());
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        APIManager.showInter(OpenWindowActivity.this, true, (AdsCallback) b -> {
            finish();
        });
    }
}