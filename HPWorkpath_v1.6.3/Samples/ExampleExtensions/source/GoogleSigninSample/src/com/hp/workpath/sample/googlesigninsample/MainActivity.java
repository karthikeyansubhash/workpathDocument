// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.googlesigninsample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.gson.Gson;
import com.hp.workpath.sample.googlesigninsample.model.AccountInfo;
import com.hp.workpath.sample.googlesigninsample.model.ClientInfo;
import com.hp.workpath.sample.googlesigninsample.task.NetworkStatusTask;
import com.hp.workpath.sample.googlesigninsample.task.RefreshTokenTask;
import com.hp.workpath.sample.googlesigninsample.task.RequestTokenTask;
import com.hp.workpath.sample.googlesigninsample.task.RevokeTokenTask;
import com.hp.workpath.sample.googlesigninsample.task.TaskInterface;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "[SAMPLE]" + "Google";

    private final String CLIENT_INFO_FILE = "client_secrets.json";

    private AccountInfo mAccountInfo;

    private WebView mWebView;
    private Button mLoadUrlButton;
    private Button mRevokeTokenButton;
    private Button mCheckButton;
    private Button mRefreshTokenButton;
    private TextView mTokenTextView;

    private ClientInfo mClientInfo;

    private String mCodeVerifier;
    private String mCodeChallenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        generateCodeVerifier();
        initView();
    }

    private void initView() {
        mWebView = findViewById(R.id.web_view);
        mLoadUrlButton = findViewById(R.id.load_button);
        mLoadUrlButton.setOnClickListener(this);
        mRevokeTokenButton = findViewById(R.id.revoke_token_button);
        mRevokeTokenButton.setOnClickListener(this);
        mCheckButton = findViewById(R.id.check_button);
        mCheckButton.setOnClickListener(this);
        mRefreshTokenButton = findViewById(R.id.refresh_token_button);
        mRefreshTokenButton.setOnClickListener(this);
        mTokenTextView = findViewById(R.id.token_text_view);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; rv:70.0) Gecko/20100101 Firefox/70.0");
        mWebView.setWebViewClient(new WebViewClient());
    }

    private void enableButtons() {
        mRevokeTokenButton.setEnabled(true);
        mCheckButton.setEnabled(true);
        mRefreshTokenButton.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mClientInfo = readClientInfo();
        } catch (Throwable t) {
            Log.e(TAG, "ClientInfo", t);
            if (!TextUtils.isEmpty(t.getMessage())) {
                Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mLoadUrlButton) {
            if (!isValidClientInfo(mClientInfo)) {
                Toast.makeText(this, "Please check client information in /assets/" + CLIENT_INFO_FILE, Toast.LENGTH_SHORT).show();
            } else {
                isNetworkAvailable();
            }
        } else if (v == mRevokeTokenButton) {
            new RevokeTokenTask(mAccountInfo.getAccessToken(), taskInterface).taskExecute();
        } else if (v == mCheckButton) {
            Intent intent = new Intent(this, FileListActivity.class);
            intent.putExtra(FileListActivity.INTENT_CREDENTIAL, mAccountInfo.toString());
            startActivity(intent);
        } else if (v == mRefreshTokenButton) {
            new RefreshTokenTask(mClientInfo, mAccountInfo.getRefreshToken(), taskInterface).taskExecute();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void startSignIn() {
        try {
            String requestUrl = getRequestUrl();
            if (!TextUtils.isEmpty(requestUrl)) {
                mWebView.loadUrl(requestUrl);
                mWebView.setVisibility(View.VISIBLE);
            }
        } catch (Throwable t) {
            Log.e(TAG, "RequestUrl", t);
        }
    }

    private class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(mClientInfo.getRedirectUri())) {
                view.setVisibility(View.GONE);
                Uri uri = Uri.parse(url);
                String code = uri.getQueryParameter("code");
                if (!TextUtils.isEmpty(code)) {
                    new RequestTokenTask(mClientInfo, uri.getQueryParameter("code"), taskInterface).taskExecute();
                }
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    TaskInterface taskInterface = new TaskInterface() {
        @Override
        public void onFailure(Throwable t) {
            if (t != null && !TextUtils.isEmpty(t.getMessage())) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(MainActivity.this, "onFailure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void refreshedToken(String token) {
            Log.i(TAG, "refreshedToken: " + token);
            Toast.makeText(MainActivity.this, "refreshedToken: " + token, Toast.LENGTH_SHORT).show();
            mTokenTextView.setText(token);
            mAccountInfo.setAccessToken(token);
        }

        @Override
        public void revokedToken() {
            Log.i(TAG, "token was revoked");
            Toast.makeText(MainActivity.this, "token was revoked", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void receivedToken(AccountInfo accountInfo) {
            Log.i(TAG, "receivedToken: " + accountInfo.getAccessToken());
            MainActivity.this.mAccountInfo = accountInfo;
            mTokenTextView.setText(accountInfo.getAccessToken());
            enableButtons();
        }
    };

    public String getRequestUrl() throws Throwable {
        GoogleAuthorizationCodeFlow flow = getBaseUrl();
        if (!TextUtils.isEmpty(mClientInfo.getRedirectUri())) {
            GoogleAuthorizationCodeRequestUrl requestUrl = flow.newAuthorizationUrl();
            requestUrl = requestUrl.setRedirectUri(mClientInfo.getRedirectUri());
            requestUrl.set("code_challenge_method", mClientInfo.getCodeChallengeMethod());
            requestUrl.set("code_challenge", mClientInfo.getCodeChallenge());
            return requestUrl.build();
        }

        throw new Exception("redirection url is empty");
    }

    private GoogleAuthorizationCodeFlow getBaseUrl() throws IOException {
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory,
                mClientInfo.getClientId(), null,
                Arrays.asList(DriveScopes.DRIVE, "https://www.googleapis.com/auth/userinfo.email"))
                .setAccessType("offline").setApprovalPrompt("force").build();
        return flow;
    }

    private void isNetworkAvailable() {
        new NetworkStatusTask(this, networkStatusInterface).taskExecute();
    }

    NetworkStatusTask.NetworkStatusInterface networkStatusInterface = new NetworkStatusTask.NetworkStatusInterface() {
        @Override
        public void isAvailable(boolean isAvailable) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isAvailable) {
                        Toast.makeText(MainActivity.this, "Please connect network", Toast.LENGTH_SHORT).show();
                    } else {
                        startSignIn();
                    }
                }
            });
        }
    };

    private ClientInfo readClientInfo() throws Throwable {
        InputStream inputStream = getAssets().open(CLIENT_INFO_FILE);
        int size = inputStream.available();
        byte[] buffer = new byte[size];
        inputStream.read(buffer);
        inputStream.close();
        String json = new String(buffer, "UTF-8");

        ClientInfo clientInfo = new Gson().fromJson(json, ClientInfo.class);
        if (!isValidClientInfo(clientInfo)) {
            throw new Exception("Please check client information in /assets/" + CLIENT_INFO_FILE);
        }
        clientInfo.setCodeChallenge(mCodeChallenge);
        clientInfo.setCodeVerifier(mCodeVerifier);
        return clientInfo;
    }

    private boolean isValidClientInfo(ClientInfo clientInfo) {
        if (clientInfo == null) return false;
        if (TextUtils.isEmpty(clientInfo.getClientId()) ||
                getString(R.string.json_client_id).equalsIgnoreCase(clientInfo.getClientId())) {
            return false;
        }
        if (TextUtils.isEmpty(clientInfo.getRedirectUri()) ||
                getString(R.string.json_redirect_uri).equalsIgnoreCase(clientInfo.getRedirectUri())) {
            return false;
        }
        return true;
    }

    private void generateCodeVerifier() {
        try {
            int len = 50;
            char[] ch = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
            char[] c = new char[len];
            SecureRandom random = new SecureRandom();
            for (int i = 0; i < len; i++) {
                c[i] = ch[random.nextInt(ch.length)];
            }

            mCodeVerifier = new String(c);
            Log.e(TAG, "generateCodeVerifier: " + mCodeVerifier);

            byte[] body = MessageDigest.getInstance("SHA-256")
                    .digest(mCodeVerifier.getBytes("UTF-8"));

            mCodeChallenge = Base64.encodeToString(body, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
            Log.e(TAG, "generateCodeChallenge: " + mCodeChallenge);
        } catch (Throwable t) {
            if (t != null && !TextUtils.isEmpty(t.getMessage())) {
                Log.e(TAG, t.getMessage());
            }
        }
    }
}
