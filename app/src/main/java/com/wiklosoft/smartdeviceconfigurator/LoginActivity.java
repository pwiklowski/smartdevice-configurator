package com.wiklosoft.smartdeviceconfigurator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;

import com.goebl.david.Response;
import com.goebl.david.Webb;

import org.json.JSONObject;

/**
 * Created by pwiklowski on 28.05.17.
 */

public class LoginActivity extends AppCompatActivity {
    private String TAG = "LoginActivity";
    WebView mWebView;
    private String oauth_client = "hub_client";
    private String oauth_secret = "hub_client_secret";
    Webb client = Webb.create();

    public static String ACCESS_TOKEN = "access_token";
    public static String REFRESH_TOKEN = "refresh_token";

    com.wiklosoft.smartdeviceconfigurator.WebView.OnUrlChanged mOnUrlChanged = new com.wiklosoft.smartdeviceconfigurator.WebView.OnUrlChanged() {
        @Override
        public void onUrlChanged(String url) {
            if (url.contains("https://www.example.com")){
                final String[] parts = url.split("code=");
                Log.d(TAG, parts[1]);

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        byte[] credentials;
                        Intent data = new Intent();
                        try {
                            credentials = (oauth_client+ ":" +oauth_secret).getBytes("UTF-8");
                            String auth = "Basic " + Base64.encodeToString(credentials, 0);

                            Response<JSONObject> result = client.post("https://auth.wiklosoft.com/v1/oauth/tokens")
                                    .header(Webb.HDR_AUTHORIZATION, auth)
                                    .param("grant_type", "authorization_code")
                                    .param("redirect_uri", "https://www.example.com")
                                    .param("code", parts[1])
                                    .asJsonObject();

                            Log.d(TAG, result.getResponseMessage() + " " + result.getBody());

                            data.putExtra(ACCESS_TOKEN, result.getBody().getString("access_token"));
                            data.putExtra(REFRESH_TOKEN, result.getBody().getString("refresh_token"));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        setResult(RESULT_OK, data);
                        finish();
                    }

                });
                t.start();

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mWebView = (WebView) findViewById(R.id.loginView);
        com.wiklosoft.smartdeviceconfigurator.WebView view = new com.wiklosoft.smartdeviceconfigurator.WebView();
        view.setOnUrlChanged(mOnUrlChanged);
        mWebView.setWebViewClient(view);
        mWebView.loadUrl("https://auth.wiklosoft.com/web/authorize?client_id=hub_client&login_redirect_uri=%2Fweb%2Fauthorize&redirect_uri=https%3A%2F%2Fwww.example.com&response_type=code&scope=read");
    }

}
