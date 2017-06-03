package com.wiklosoft.smartdeviceconfigurator.fragments;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wiklosoft.smartdeviceconfigurator.R;
import com.wiklosoft.smartdeviceconfigurator.WifiPasswordDialog;
import com.wiklosoft.smartdeviceconfigurator.utils.WiFi;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

public class WifiResultsFragment extends Fragment implements WifiPasswordDialog.WifiPasswordDialogListener{
    private String TAG = "WifiResultsFragment";
    List<ScanResult> mResults = new ArrayList<>();
    WifiListAdapter mAdapter;
    ListView mList;
    WifiManager mWifiManager;

    public WifiResultsFragment() {
    }

    public void setWifiScanResults(List<ScanResult> list){
        mResults.addAll(list);
        if (mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mWifiManager = (WifiManager)getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
        View v = inflater.inflate(R.layout.fragment_wifi_results, container, false);
        mAdapter = new WifiListAdapter(getActivity(), mResults);
        mList = (ListView) v.findViewById(R.id.wifiList);
        mList.setAdapter(mAdapter);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ScanResult res = mResults.get(i);
                List<WifiConfiguration> mWifiList = mWifiManager.getConfiguredNetworks();
                String ssid = WiFi.convertToQuotedString(res.SSID);
                boolean configured = false;
                WifiConfiguration configuration = null;
                for(WifiConfiguration config: mWifiList){
                    if (config.SSID.equals(ssid)){
                        configured = true;
                        configuration = config;
                        break;
                    }
                }

                if (!configured){
                    Log.d(TAG, "Wifi is not configured, ask for password");
                    WifiPasswordDialog dialog = new WifiPasswordDialog();
                    dialog.setWifiPasswordDialogListener(WifiResultsFragment.this);
                    dialog.setScanResult(mResults.get(i));
                    dialog.show(getFragmentManager(), "WifiPassword");
                }else{
                    Log.d(TAG, "Wifi is configured, connect");
                    if (WiFi.connectToConfiguredNetwork(mWifiManager,configuration, true)){
                        onSuccess();
                    }else{
                        onFailure();
                    }
                }

            }
        });
        return v;
    }

    @Override
    public void onDialogPositiveClick(ScanResult result, String password) {
        if (WiFi.connectToNewNetwork(mWifiManager, result, password)){
            onSuccess();
        }else{
            onFailure();
        }
    }

    void onSuccess(){
        Log.d(TAG, "onSuccess");
    }

    void onFailure(){
        Log.d(TAG, "onFailure");
    }
}
