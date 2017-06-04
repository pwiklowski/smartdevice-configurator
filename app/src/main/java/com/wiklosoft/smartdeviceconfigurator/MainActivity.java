package com.wiklosoft.smartdeviceconfigurator;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.wiklosoft.smartdeviceconfigurator.fragments.NameChangeFragment;
import com.wiklosoft.smartdeviceconfigurator.fragments.SetDeviceWifiFragment;
import com.wiklosoft.smartdeviceconfigurator.fragments.SplashScreenFragment;
import com.wiklosoft.smartdeviceconfigurator.fragments.SuccessFragment;
import com.wiklosoft.smartdeviceconfigurator.fragments.WifiResultsFragment;
import com.wiklosoft.smartdeviceconfigurator.fragments.WizardAuthorizationFragment;

import java.util.List;


public class MainActivity extends AppCompatActivity implements IActionResult {
    private String TAG = "MainActivity";
    FrameLayout mFrame;

    List<ScanResult> mScanResults;
    ProgressBar mProgresbar;
    Handler mHandler = new Handler();

    public static String CONNECT_TO_DEVICE_WIFI = "CONNECT_TO_DEVICE_WIFI";
    public static String SET_NAME = "SET_NAME";
    public static String WIZARD_WIFI_SETTINGS = "WIZARD_WIFI_SETTINGS";
    public static String WIZARD_OAUTH = "WIZARD_OAUTH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFrame = (FrameLayout) findViewById(R.id.frame);
        mProgresbar = (ProgressBar) findViewById(R.id.wizardProgress);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame, new SplashScreenFragment());
        transaction.commit();



        mHandler.postDelayed(()-> showWifiResultsFragment(), 5000);
    }

    @Override
    public void onResume(){
        super.onResume();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, 0);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CHANGE_WIFI_STATE},0);
        }
        askPermissionIfNeeded(Manifest.permission.ACCESS_COARSE_LOCATION);
        askPermissionIfNeeded(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0x12345) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
        }
    }
    void askPermissionIfNeeded(String permission){
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, 0);
        }
    }



    private void showWifiResultsFragment(){
        WifiResultsFragment fragment = new WifiResultsFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, fragment);
        transaction.addToBackStack(null).commit();

        fragment.init(this);
    }


    private void showWifiConfigFragment(){
        SetDeviceWifiFragment fragment = new SetDeviceWifiFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, fragment);
        transaction.addToBackStack(null).commit();

        fragment.init(this);
    }

    private void showNameSetupFragment(){
        NameChangeFragment fragment = new NameChangeFragment();
        fragment.init(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, fragment);
        transaction.addToBackStack(null).commit();
    }

    private void showAuthorizationFragment(){
        WizardAuthorizationFragment fragment = new WizardAuthorizationFragment();
        fragment.init(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, fragment);
        transaction.addToBackStack(null).commit();
    }
    private void showSuccessFragment(){
        SuccessFragment fragment = new SuccessFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, fragment);
        transaction.addToBackStack(null).commit();
    }

    @Override
    public void onSuccess(String name) {
        Log.d(TAG, "onSuccess "+ name);

        if (name.equals(CONNECT_TO_DEVICE_WIFI)){
            showNameSetupFragment();
            mProgresbar.setProgress(1);
        }else if (name.equals(SET_NAME)){
            showWifiConfigFragment();
            mProgresbar.setProgress(2);
        }else if (name.equals(WIZARD_WIFI_SETTINGS)){
            showAuthorizationFragment();
            mProgresbar.setProgress(3);
        }else if (name.equals(WIZARD_OAUTH)){
            showSuccessFragment();
            mProgresbar.setProgress(4);
        }

    }

    @Override
    public void onFailure(String name) {

    }
}
