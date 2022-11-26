package com.privatebrowser.safebrowser.download.video.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


import java.util.List;

import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.DialogDeleteBinding;
import com.privatebrowser.safebrowser.download.video.databinding.FragmentSettingBinding;
import com.privatebrowser.safebrowser.download.video.download.history.HistorySQLite;
import com.privatebrowser.safebrowser.download.video.download.history.VisitedPage;
import com.privatebrowser.safebrowser.download.video.mainUi.PrivacyActivity;
import com.privatebrowser.safebrowser.download.video.promoScreen.promoScreen2;
import think.outside.the.box.handler.APIManager;


public class SettingFragment extends Fragment {

    FragmentSettingBinding binding;
    private HistorySQLite historySQLite;
    private List<VisitedPage> visitedPages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        APIManager.showNative(binding.adNative);
        historySQLite = new HistorySQLite(getActivity());
        visitedPages = historySQLite.getAllVisitedPages();
        binding.clearHistory.setOnClickListener(v -> {
            Dialog dialog = new Dialog(getActivity(), R.style.WideDialog);
            DialogDeleteBinding deleteBinding = DialogDeleteBinding.inflate(LayoutInflater.from(getActivity()), null, false);
            dialog.setContentView(deleteBinding.getRoot());
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();

            deleteBinding.titleText.setText(R.string.delete);
            deleteBinding.msgText.setText(R.string.all_history_delete);

            deleteBinding.yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    APIManager.showInter(requireActivity(), false, isfail -> {
                        historySQLite.clearHistory();
                        visitedPages.clear();
                        dialog.dismiss();
                    });
                }
            });

            deleteBinding.noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        });

        binding.rate.setOnClickListener(v -> {
            APIManager.showRattingDialog(getActivity(), () -> {
            });
        });

        binding.changeLanguage.setOnClickListener(view -> {
            APIManager.showInter(requireActivity(), false, isfail -> {
                Intent intent = new Intent(requireActivity(), promoScreen2.class);
                intent.putExtra("settingScreen", true);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            });
        });

        binding.share.setOnClickListener(v -> {
            String app = getString(R.string.app_name);
            Intent share = new Intent("android.intent.action.SEND");
            share.setType("text/plain");
            share.putExtra("android.intent.extra.TEXT",
                    app + "\n\n" + "Open this Link on Play Store" + "\n\n" + "https://play.google.com/store/apps/details?id=" +
                            getActivity().getPackageName());
            startActivity(Intent.createChooser(share, "Share Application"));
        });

        binding.policy.setOnClickListener(v -> {
            APIManager.showInter(requireActivity(), false, isfail -> {
                startActivity(new Intent(getActivity(), PrivacyActivity.class));
            });
        });

        return binding.getRoot();
    }
}