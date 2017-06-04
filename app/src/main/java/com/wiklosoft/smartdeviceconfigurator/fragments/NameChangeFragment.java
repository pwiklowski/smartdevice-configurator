package com.wiklosoft.smartdeviceconfigurator.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.goebl.david.Webb;
import com.wiklosoft.smartdeviceconfigurator.IActionResult;
import com.wiklosoft.smartdeviceconfigurator.MainActivity;
import com.wiklosoft.smartdeviceconfigurator.R;

public class NameChangeFragment extends Fragment {
    EditText mDeviceName;
    Button mSaveDeviceName;
    Webb mClient = Webb.create();
    IActionResult mResultListener;

    public void init(IActionResult listener){
        mResultListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_name_change, container, false);
        mDeviceName = (EditText) v.findViewById(R.id.deviceName);
        mSaveDeviceName = (Button) v.findViewById(R.id.saveName);
        mSaveDeviceName.setOnClickListener(view -> saveName(mDeviceName.getText().toString()));
        return v;
    }
    private void saveName(String name){
        Thread t = new Thread(() -> {
            mClient.post("http://" + getGateway() + "/name").param("name", name).asString();
            mResultListener.onSuccess(MainActivity.WIZARD_SET_NAME);
        });
        t.start();
    }

    String getGateway(){
        return "192.168.4.1";
    }
}
