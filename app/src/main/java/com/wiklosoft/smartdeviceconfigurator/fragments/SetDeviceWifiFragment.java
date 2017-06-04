package com.wiklosoft.smartdeviceconfigurator.fragments;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.goebl.david.Webb;
import com.wiklosoft.smartdeviceconfigurator.R;
import com.wiklosoft.smartdeviceconfigurator.WifiPasswordDialog;
import com.wiklosoft.smartdeviceconfigurator.utils.WiFi;

import static android.content.Context.WIFI_SERVICE;
import static com.wiklosoft.smartdeviceconfigurator.MainActivity.SET_NAME;
import static com.wiklosoft.smartdeviceconfigurator.MainActivity.WIZARD_WIFI_SETTINGS;

/**
 * Created by pwiklowski on 03.06.17.
 */

public class SetDeviceWifiFragment extends WifiResultsFragment {
    Webb mClient = Webb.create();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mWifiManager = (WifiManager)getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
        View v = inflater.inflate(R.layout.fragment_wifi_results, container, false);
        mAdapter = new WifiListAdapter(getActivity(), mResults);
        mListContainer = (SwipeRefreshLayout) v.findViewById(R.id.wifiListContainer);
        mList = (ListView) v.findViewById(R.id.wifiList);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(mOnItemClickListener);

        TextView prompt = (TextView) v.findViewById(R.id.wifiPrompt);
        prompt.setText(R.string.select_device_wifi);

        mListContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWifiManager.startScan();
            }
        });

        return v;
    }
    AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ScanResult res = mResults.get(i);

            WifiPasswordDialog dialog = new WifiPasswordDialog();
            dialog.setWifiPasswordDialogListener(SetDeviceWifiFragment.this);
            dialog.setScanResult(res);
            dialog.show(getFragmentManager(), "WifiPassword");



        }
    };

    @Override
    public void onDialogPositiveClick(ScanResult result, String password) {
        saveWifiConfig(result, password);
    }
    void saveWifiConfig(ScanResult res, String pass){
        Thread t = new Thread(() -> {
            mClient.post("http://" + getGateway() + "/wifi")
                    .param("ssid", res.SSID)
                    .param("password", pass)
                    .asString();
            mResultListener.onSuccess(SET_NAME);

            WifiConfiguration configuration = isConfigured(res.SSID);
            if (configuration != null){
                if (WiFi.connectToConfiguredNetwork(mWifiManager,configuration, true)){
                    mResultListener.onSuccess(WIZARD_WIFI_SETTINGS);
                }else{
                    mResultListener.onFailure(WIZARD_WIFI_SETTINGS);
                }
            }else{
                if (WiFi.connectToNewNetwork(mWifiManager, res, pass)){
                    mResultListener.onSuccess(WIZARD_WIFI_SETTINGS);
                }else{
                    mResultListener.onFailure(WIZARD_WIFI_SETTINGS);
                }
            }

        });
        t.start();
    }

    String getGateway(){
        return "192.168.4.1";
    }
}
