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

public class SuccessFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_success, container, false);
        return v;
    }
}
