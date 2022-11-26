package com.privatebrowser.safebrowser.download.video.download.history;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;


import java.util.List;

import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.ActivityShowHistoryBinding;
import com.privatebrowser.safebrowser.download.video.databinding.DialogDeleteBinding;
import com.privatebrowser.safebrowser.download.video.mainUi.BaseActivity;
import think.outside.the.box.callback.AdsCallback;
import think.outside.the.box.handler.APIManager;

public class ShowHistoryActivity extends BaseActivity {

    ActivityShowHistoryBinding historyBinding;
    private List<VisitedPage> visitedPages;
    private HistorySQLite historySQLite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        historyBinding = DataBindingUtil.setContentView(ShowHistoryActivity.this, R.layout.activity_show_history);
        historyBinding = ActivityShowHistoryBinding.inflate(getLayoutInflater());
        setContentView(historyBinding.getRoot());

        historyBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        APIManager.showBanner(historyBinding.bannerRelative);

//        historyBinding.nameText.getPaint().setShader(Utility.setTextGradient(ShowHistoryActivity.this, historyBinding.nameText));

        historySQLite = new HistorySQLite(ShowHistoryActivity.this);
        visitedPages = historySQLite.getAllVisitedPages();

        if (visitedPages.size() == 0) {
            historyBinding.historyList.setVisibility(View.GONE);
            historyBinding.noDataText.setVisibility(View.VISIBLE);
        } else {
            historyBinding.historyList.setVisibility(View.VISIBLE);
            historyBinding.noDataText.setVisibility(View.GONE);
        }

        historyBinding.historyList.setLayoutManager(new LinearLayoutManager(ShowHistoryActivity.this));
        VisitedPagesAdapter visitedPagesAdapter = new VisitedPagesAdapter(ShowHistoryActivity.this, visitedPages);
        historyBinding.historyList.setAdapter(visitedPagesAdapter);

        historyBinding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(ShowHistoryActivity.this, R.style.WideDialog);
                DialogDeleteBinding deleteBinding = DialogDeleteBinding.inflate(LayoutInflater.from(ShowHistoryActivity.this),  null, false);
                dialog.setContentView(deleteBinding.getRoot());
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                deleteBinding.titleText.setText(R.string.delete);
                deleteBinding.msgText.setText(R.string.all_data_delete_from_history);

                deleteBinding.yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        historySQLite.clearHistory();
                        visitedPages.clear();
                        historyBinding.historyList.getAdapter().notifyDataSetChanged();
                        if (visitedPages.size() == 0) {
                            historyBinding.historyList.setVisibility(View.GONE);
                            historyBinding.noDataText.setVisibility(View.VISIBLE);
                        } else {
                            historyBinding.historyList.setVisibility(View.VISIBLE);
                            historyBinding.noDataText.setVisibility(View.GONE);
                        }
                        dialog.dismiss();
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

    }

    @Override
    public void onBackPressed() {
        APIManager.showInter(ShowHistoryActivity.this, true, (AdsCallback) b -> {
            finish();
        });
    }
}