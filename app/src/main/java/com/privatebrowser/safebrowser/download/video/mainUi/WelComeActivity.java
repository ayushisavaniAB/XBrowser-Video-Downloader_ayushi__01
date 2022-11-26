package com.privatebrowser.safebrowser.download.video.mainUi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.ActivityWelComeBinding;
import think.outside.the.box.handler.APIManager;
import think.outside.the.box.vpn.CONNECTION_STATE;
import think.outside.the.box.vpn.VpnConnection;

public class WelComeActivity extends BaseActivity {

    ActivityWelComeBinding binding;
    VpnConnection vpnConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLightTheme(false);
        super.onCreate(savedInstanceState);
        binding = ActivityWelComeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        vpnConnection = new VpnConnection(this);

        APIManager.showBanner(binding.adsBanner65);
        initListener();
    }

    private void initListener() {

        binding.btnNormal.setOnClickListener(v -> {
            if (vpnConnection.isConnected()) {
                vpnConnection.stopVpn();
            }
            APIManager.showInter(this, false, b -> {
                startActivity(new Intent(this, BrowserMainActivity.class));
            });
        });


        binding.btnSafe.setOnClickListener(v -> {
            if (vpnConnection.isConnected()) {
                vpnConnection.stopVpn();
            }
            prepareVpn();
        });

        binding.share.setOnClickListener(v -> {
            String app = getString(R.string.app_name);
            Intent share = new Intent("android.intent.action.SEND");
            share.setType("text/plain");
            share.putExtra("android.intent.extra.TEXT", app + "\n\n" + "Open this Link on Play Store" + "\n\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
            startActivity(Intent.createChooser(share, "Share Application"));
        });

        binding.cancel.setOnClickListener(v -> {
            vpnConnection.stopVpn();
            binding.connectingProcess.setVisibility(View.GONE);
        });

        binding.privacy.setOnClickListener(v -> {
            APIManager.showInter(this, false, b -> {
                startActivity(new Intent(this, PrivacyActivity.class));
            });
        });
    }

    private void prepareVpn() {
        binding.connectingProcess.setVisibility(View.VISIBLE);
        vpnConnection.connectVpnListener((connection_state, s) -> {
            if (connection_state == CONNECTION_STATE.CONNECTED) {
                binding.connectingProcess.setVisibility(View.GONE);
                APIManager.showInter(this, false, b -> {
                    startActivity(new Intent(this, BrowserMainActivity.class));
                });
            } else if (connection_state == CONNECTION_STATE.DISCONNECTED) {
                binding.connectingProcess.setVisibility(View.GONE);
//                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        APIManager.showRattingDialog(this, () -> {
            APIManager.showExitDialog(this);
        });

    }

    @Override
    protected void onDestroy() {
        vpnConnection.stopVpn();
        super.onDestroy();
    }
}