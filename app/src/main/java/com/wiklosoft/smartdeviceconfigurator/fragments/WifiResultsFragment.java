package com.wiklosoft.smartdeviceconfigurator.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wiklosoft.smartdeviceconfigurator.IActionResult;
import com.wiklosoft.smartdeviceconfigurator.MainActivity;
import com.wiklosoft.smartdeviceconfigurator.R;
import com.wiklosoft.smartdeviceconfigurator.WifiPasswordDialog;
import com.wiklosoft.smartdeviceconfigurator.utils.WiFi;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;
import static com.wiklosoft.smartdeviceconfigurator.MainActivity.CONNECT_TO_DEVICE_WIFI;

public class WifiResultsFragment extends Fragment implements WifiPasswordDialog.WifiPasswordDialogListener {
    private String TAG = "WifiResultsFragment";
    List<ScanResult> mResults = new ArrayList<>();
    WifiListAdapter mAdapter;
    ListView mList;
    WifiManager mWifiManager;
    IActionResult mResultListener;
    SwipeRefreshLayout mListContainer;

    public WifiResultsFragment() {
    }

    public void init(IActionResult listener){
        mResultListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mWifiManager = (WifiManager)getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
        View v = inflater.inflate(R.layout.fragment_wifi_results, container, false);
        mAdapter = new WifiListAdapter(getActivity(), mResults);
        mListContainer = (SwipeRefreshLayout) v.findViewById(R.id.wifiListContainer);
        mList = (ListView) v.findViewById(R.id.wifiList);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(mOnItemClickListener);

        mListContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWifiManager.startScan();
            }
        });

        mWifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        getActivity().registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiManager.startScan();
        return v;
    }

    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ScanResult res = mResults.get(i);
            WifiConfiguration configuration = isConfigured(res.SSID);

            if (configuration == null){
                Log.d(TAG, "Wifi is not configured, ask for password");
                WifiPasswordDialog dialog = new WifiPasswordDialog();
                dialog.setWifiPasswordDialogListener(WifiResultsFragment.this);
                dialog.setScanResult(mResults.get(i));
                dialog.show(getFragmentManager(), "WifiPassword");
            }else{
                Log.d(TAG, "Wifi is configured, connect");
                if (WiFi.connectToConfiguredNetwork(mWifiManager,configuration, true)){
                    mResultListener.onSuccess(CONNECT_TO_DEVICE_WIFI);
                }else{
                    mResultListener.onFailure(CONNECT_TO_DEVICE_WIFI);
                }
            }

        }
    };

    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                mListContainer.setRefreshing(false);
                mResults = mWifiManager.getScanResults();
                mAdapter.clear();
                mAdapter.addAll(mResults);
                mAdapter.notifyDataSetChanged();
                Log.d(TAG, mResults.toString());
            }
        }
    };

    protected WifiConfiguration isConfigured(String SSID){
        List<WifiConfiguration> mWifiList = mWifiManager.getConfiguredNetworks();
        String ssid = WiFi.convertToQuotedString(SSID);
        for(WifiConfiguration config: mWifiList){
            if (config.SSID.equals(ssid)){
                return config;
            }
        }
        return null;
    }

    @Override
    public void onDialogPositiveClick(ScanResult result, String password) {
        if (WiFi.connectToNewNetwork(mWifiManager, result, password)){
            mResultListener.onSuccess(CONNECT_TO_DEVICE_WIFI);
        }else{
            mResultListener.onFailure(CONNECT_TO_DEVICE_WIFI);
        }
    }
}
