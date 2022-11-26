package com.privatebrowser.safebrowser.download.video.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import com.privatebrowser.safebrowser.download.video.databinding.FragmentHistoryBinding;
import com.privatebrowser.safebrowser.download.video.download.history.HistorySQLite;
import com.privatebrowser.safebrowser.download.video.download.history.VisitedPage;
import com.privatebrowser.safebrowser.download.video.download.history.VisitedPagesAdapterFragment;

public class HistoryFragment extends Fragment {

    FragmentHistoryBinding historyBinding;
    private List<VisitedPage> visitedPages;
    private HistorySQLite historySQLite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        historyBinding = FragmentHistoryBinding.inflate(inflater,container,false);
        setData();
        return historyBinding.getRoot();
    }

    private void setData() {
        historySQLite = new HistorySQLite(getActivity());
        visitedPages = historySQLite.getAllVisitedPages();

        if (visitedPages.size() == 0) {
            historyBinding.historyList.setVisibility(View.GONE);
            historyBinding.noDataText.setVisibility(View.VISIBLE);
        } else {
            historyBinding.historyList.setVisibility(View.VISIBLE);
            historyBinding.noDataText.setVisibility(View.GONE);
        }

        historyBinding.historyList.setLayoutManager(new LinearLayoutManager(getActivity()));
        VisitedPagesAdapterFragment visitedPagesAdapter = new VisitedPagesAdapterFragment(getActivity(), visitedPages);
        historyBinding.historyList.setAdapter(visitedPagesAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        setData();
    }
}