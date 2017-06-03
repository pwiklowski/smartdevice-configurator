package com.wiklosoft.smartdeviceconfigurator;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.wiklosoft.smartdeviceconfigurator.fragments.WifiResultsFragment;

import static android.R.attr.key;
import static android.content.Context.WIFI_SERVICE;

/**
 * Created by pwiklowski on 01.06.17.
 */

public class WifiPasswordDialog extends DialogFragment{
    public interface WifiPasswordDialogListener {
        void onDialogPositiveClick(ScanResult result, String password);
    }
    public static String SSID = "SSID";
    ScanResult mScanResult;
    EditText mPassword;

    // Use this instance of the interface to deliver action events
    WifiPasswordDialogListener mListener;

    public void setWifiPasswordDialogListener(WifiPasswordDialogListener listener) {
        mListener = listener;
    }

    public void setScanResult(ScanResult result){
        mScanResult = result;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.fragment_wifi_password, null);

        mPassword = (EditText) v.findViewById(R.id.wifiPassword);

        builder.setView(v).setTitle("Enter password to selected Wifi")
               .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                       mListener.onDialogPositiveClick(mScanResult, mPassword.getText().toString());
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       WifiPasswordDialog.this.getDialog().cancel();
                   }
               });
        return builder.create();
    }


}
