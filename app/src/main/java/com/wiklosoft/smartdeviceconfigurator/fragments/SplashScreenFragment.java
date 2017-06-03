package com.wiklosoft.smartdeviceconfigurator.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wiklosoft.smartdeviceconfigurator.R;

/**
 * Created by pwiklowski on 31.05.17.
 */

public class SplashScreenFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.splash_screen, container, false);
    }
}
