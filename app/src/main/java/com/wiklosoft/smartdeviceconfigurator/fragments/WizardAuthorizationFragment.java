package com.wiklosoft.smartdeviceconfigurator.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goebl.david.Webb;
import com.wiklosoft.smartdeviceconfigurator.IActionResult;
import com.wiklosoft.smartdeviceconfigurator.LoginActivity;
import com.wiklosoft.smartdeviceconfigurator.R;
import com.youview.tinydnssd.DiscoverResolver;
import com.youview.tinydnssd.MDNSDiscover;

import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.wiklosoft.smartdeviceconfigurator.MainActivity.WIZARD_OAUTH;

/**
 * A simple {@link Fragment} subclass.
 */
public class WizardAuthorizationFragment extends Fragment {
    private String TAG = "WizardAuthorization";
    private int GET_CODE_REQUEST = 1;
    IActionResult mResultListener;
    private static final String NSD_SERVICE_TYPE = "_wiklosoftconfig._tcp.";
    DiscoverResolver mResolver;
    Webb mClient = Webb.create();

    String mDeviceIP;
    String mDeviceUUID;

    public WizardAuthorizationFragment() {

    }
    DiscoverResolver.Listener listener = new DiscoverResolver.Listener() {
        @Override
        public void onServicesChanged(Map<String, MDNSDiscover.Result> services) {
            for (MDNSDiscover.Result result : services.values()) {
                Log.d(TAG,  result.toString());
                mDeviceUUID = result.txt.dict.get("uuid");
                mDeviceIP = result.a.ipaddr;
            }
            mResolver.stop();
            requestAccessToken();
        }
    };

    @Override
    public void onCreate(Bundle b){
        super.onCreate(b);
        mResolver = new DiscoverResolver(getContext(), NSD_SERVICE_TYPE,listener);
        mResolver.start();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (mResolver != null && mResolver.isRunning())
            mResolver.stop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wizard_authorization, container, false);
    }

    void requestAccessToken(){
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivityForResult(intent, GET_CODE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_CODE_REQUEST) {
            if (resultCode == RESULT_OK) {
                String access_token = data.getStringExtra(LoginActivity.ACCESS_TOKEN);
                String refresh_token = data.getStringExtra(LoginActivity.REFRESH_TOKEN);

                Thread t = new Thread(() -> {
                    mClient.post("http://" + mDeviceIP + "/code")
                            .param("access_token", access_token)
                            .param("refresh_token", refresh_token)
                            .asString();

                    mClient.post("http://" + mDeviceIP + "/restart").asString();
                    mResultListener.onSuccess(WIZARD_OAUTH);
                });
                t.start();

            }
        }
    }


    public void init(IActionResult listener){
        mResultListener = listener;
    }

}
