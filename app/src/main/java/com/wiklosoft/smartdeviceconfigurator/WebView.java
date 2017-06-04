package com.wiklosoft.smartdeviceconfigurator;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebViewClient;


/**
 * Created by pwiklowski on 28.05.17.
 */

public class WebView extends WebViewClient {
    private String TAG = "WebView";
    private OnUrlChanged mCallback;

    interface OnUrlChanged{
        void onUrlChanged(String url);
    }

    public void setOnUrlChanged(OnUrlChanged callback){
        mCallback = callback;
    }

    public void onPageStarted(android.webkit.WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

        if(mCallback!=null){
            mCallback.onUrlChanged(url);
        }

        Log.d(TAG, "onPageStarted " + url);
    }
}
