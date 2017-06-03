package com.wiklosoft.smartdeviceconfigurator.fragments;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wiklosoft.smartdeviceconfigurator.R;

import java.util.List;

import static android.view.LayoutInflater.*;

/**
 * Created by pwiklowski on 01.06.17.
 */

public class WifiListAdapter  extends ArrayAdapter<ScanResult> {

    WifiListAdapter(Context context, List<ScanResult> results){
        super(context, 0, results);
    }

    public WifiListAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ScanResult result = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = from(getContext()).inflate(R.layout.scan_item, parent, false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.wifiName);
        name.setText(result.SSID);
        return convertView;
    }
}
