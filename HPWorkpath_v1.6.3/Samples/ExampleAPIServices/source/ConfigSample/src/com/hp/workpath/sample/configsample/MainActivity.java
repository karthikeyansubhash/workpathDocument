// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.configsample;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.MasterKeys;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.api.config.ConfigService;
import com.hp.workpath.api.printer.PrintAttributes;
import com.hp.workpath.api.printer.PrintAttributesCaps;
import com.hp.workpath.sample.configsample.model.SimplePrintOption;
import com.hp.workpath.sample.configsample.task.ConfigReaderTask;
import com.hp.workpath.sample.configsample.task.ConfigUpdateTask;
import com.hp.workpath.sample.configsample.task.InitializationTask;
import com.hp.workpath.sample.configsample.task.LoadPrintCapabilitiesTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

/**
 * Main activity for Config Sample.
 */
public class MainActivity extends AppCompatActivity {

    public static final String TAG = "[SAMPLE]" + "Config";

    /* Background task for Workpath SDK API initialization */
    private InitializationTask mInitializationTask;
    private ConfigUpdateTask mConfigUpdateTask;

    private ConfigChangeObserver mConfigChangeObserver;

    private ProgressBar mProgress;
    private EditText mUrlEditText;
    private EditText mConfigEditText;
    private TextView mPrintOptionTextView;
    private ListView mPrintOptionListView;
    private Button mGetConfigButton;
    private Button mUpdateConfigButton;

    private LinearLayout mSecretLayout;
    private TextView mSecretEditText;

    private View mContainer;
    private OptionListAdapter mOptionListAdapter;

    private AlertDialog mAlertDialog;
    private Snackbar mSnackBar;

    private PrintAttributesCaps caps;
    private final String SCREEN_4_3_INCH = "Screen_4.3_Inch";
    private final String SECRET_KEY = "secret";
    private final String SECRET_FILE = "sensitive_data.txt";
    private boolean isSecretValueUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (SCREEN_4_3_INCH.equals(findViewById(R.id.layout).getTag())) {
            Toolbar toolBar = findViewById(R.id.toolbar);
            setSupportActionBar(toolBar);
        }
        // find the text and button
        findViewElements();
        addListener();
        mConfigChangeObserver = new ConfigChangeObserver(new Handler());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register ConfigChangeObserver to observe the event when application's config is changed
        mConfigChangeObserver.register(getApplicationContext());

        mInitializationTask = new InitializationTask(this);
        mInitializationTask.taskExecute();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // unregister ConfigChangeObserver
        mConfigChangeObserver.unregister(getApplicationContext());

        mInitializationTask.cancel();
        mInitializationTask = null;

        if (mConfigUpdateTask != null) {
            mConfigUpdateTask.cancel();
            mConfigUpdateTask = null;
        }

        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }

        if (mSnackBar != null) {
            mSnackBar.dismiss();
            mSnackBar = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.version, menu);
        MenuItem versionMenu = menu.findItem(R.id.menuVersion);
        String version = "";
        try {
            Workpath sdkInfo = Workpath.getInstance();
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = getString(R.string.version, pInfo.versionName, pInfo.versionCode, sdkInfo.getVersionName(), sdkInfo.getVersionCode());
        } catch (Throwable t) {
            handleException(t);
        }
        versionMenu.setTitle(version);
        return true;
    }

    private void findViewElements() {
        mContainer = findViewById(R.id.container);
        mUrlEditText = findViewById(R.id.urlEditText);
        mConfigEditText = findViewById(R.id.configEditText);
        mUpdateConfigButton = findViewById(R.id.updateConfigButton);
        mSecretLayout = findViewById(R.id.secretLayout);
        mSecretEditText = findViewById(R.id.secretEditText);
        mProgress = findViewById(R.id.progressBar);
        mPrintOptionTextView = findViewById(R.id.printOptionTextView);
        mPrintOptionListView = findViewById(R.id.printOptionListView);
        mGetConfigButton = findViewById(R.id.getConfigButton);
        mPrintOptionListView = findViewById(R.id.printOptionListView);
        mOptionListAdapter = new OptionListAdapter(this);
        mPrintOptionListView.setAdapter(mOptionListAdapter);
    }

    private void addListener() {
        mUpdateConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSetValue();
            }
        });
        mGetConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConfigReaderTask(MainActivity.this, false).taskExecute();
            }
        });
        mConfigEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing.
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    mUpdateConfigButton.setEnabled(true);
                } else {
                    mUpdateConfigButton.setEnabled(false);
                }
            }
        });
    }

    private void setPrintAttributes(SimplePrintOption option) {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        if (mPrintOptionTextView != null && option != null) {
            String defaultJson = gson.toJson(option);
            mPrintOptionTextView.setText(defaultJson);
            mOptionListAdapter.setItem(option);
            mOptionListAdapter.notifyDataSetChanged();
        }
    }

    public void setPrintCapabilities(PrintAttributesCaps caps) {
        this.caps = caps;
    }

    /**
     * Handles value set operation.
     * It can do some checks and launch AsyncTask to set value in ConfigService
     */
    private void handleSetValue() {
        if (mConfigEditText != null && mConfigEditText.isEnabled()) {
            if (mConfigUpdateTask != null) {
                mConfigUpdateTask.cancel();
            }

            // disable editing while updating
            mConfigEditText.setEnabled(false);

            // start updating task with new configuration value
            mConfigUpdateTask = new ConfigUpdateTask(this, mConfigEditText.getText().toString());
            mConfigUpdateTask.taskExecute();
            showProgress(View.VISIBLE);
        }
    }

    public void handleComplete() {
        mUpdateConfigButton.setEnabled(true);

        new LoadPrintCapabilitiesTask(MainActivity.this).taskExecute();
        new ConfigReaderTask(this, true).taskExecute();
    }

    public void setConfigComplete() {
        // enable editing
        if (mConfigEditText != null) {
            mConfigEditText.setEnabled(true);
        }
    }

    public void showProgress(int visibility) {
        if (mProgress != null) {
            mProgress.setVisibility(visibility);
        }
    }

    public void getConfigComplete(JSONObject configJsonObject) {
        isSecretValueUpdate = isExistSecretComponent(configJsonObject);

        if (!isSecretValueUpdate) {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(configJsonObject.toString());
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

            // enable editing
            if (mConfigEditText != null) {
                mConfigEditText.setText(gson.toJson(jsonObject));
            }
        }

        String secretValue = loadSecretValue();
        mSecretEditText.setText(secretValue);
        if (TextUtils.isEmpty(secretValue)) {
            mSecretLayout.setVisibility(View.GONE);
        } else {
            mSecretLayout.setVisibility(View.VISIBLE);
        }
    }

    public void updatePrintOption(JSONObject configJsonObject) {
        SimplePrintOption simplePrintOption = new SimplePrintOption();
        try {
            String paperSize = configJsonObject.getString("paperSize");
            if (caps != null && !TextUtils.isEmpty(paperSize)) {
                PrintAttributes.PaperSize psz = PrintAttributes.PaperSize.valueOf(paperSize.toUpperCase());
                if (caps.getPaperSizeList().contains(psz)) {
                    simplePrintOption.setPaperSize(psz.name());
                }
            }
        } catch (Throwable t) {
            if (getApplicationContext() != null) {
                Logger.showResult(MainActivity.this, getString(R.string.not_supported, "paperSize") + " " + t.getMessage());
            }
        }

        try {
            String colorMode = configJsonObject.getString("colorMode");
            if (caps != null && !TextUtils.isEmpty(colorMode)) {
                PrintAttributes.ColorMode color = PrintAttributes.ColorMode.valueOf(colorMode.toUpperCase());
                if (caps.getColorModeList().contains(color)) {
                    simplePrintOption.setColorMode(color.name());
                }
            }
        } catch (Throwable t) {
            if (getApplicationContext() != null) {
                Logger.showResult(MainActivity.this, getString(R.string.not_supported, "colorMode") + " " + t.getMessage());
            }
        }

        try {
            int copies = configJsonObject.getInt("copies");
            if (caps != null) {
                if (copies > 0 && copies <= caps.getMaxCopies()) {
                    simplePrintOption.setCopies(copies);
                } else {
                    if (getApplicationContext() != null) {
                        Logger.showResult(MainActivity.this, getString(R.string.not_supported, "copies: " + copies));
                    }
                }
            }
        } catch (JSONException e) {
            if (getApplicationContext() != null) {
                Logger.showResult(MainActivity.this, getString(R.string.not_supported, "copies") + " " + e.getMessage());
            }
        }

        try {
            mUrlEditText.setText(configJsonObject.getString("url"));
        } catch (JSONException e) {
            if (getApplicationContext() != null) {
                Logger.showResult(MainActivity.this, getString(R.string.not_supported, "url") + " " + e.getMessage());
            }
        }

        setPrintAttributes(simplePrintOption);
    }

    private boolean isExistSecretComponent(JSONObject jsonObject) {
        /**
         * This source code is one of example that how configuration can be used various way.
         */
        if (jsonObject.has(SECRET_KEY)){
            try {
                String secretValue = jsonObject.getString(SECRET_KEY);
                // remove SECRET_KEY will keep the previous secret value since API 9
                //jsonObject.remove(SECRET_KEY);
                if ("".equals(secretValue)) {
                    return false;
                }
                jsonObject.put(SECRET_KEY, "");

                mConfigUpdateTask = new ConfigUpdateTask(MainActivity.this, jsonObject.toString());
                mConfigUpdateTask.taskExecute();
                saveSecretValue(secretValue);
                Logger.showResult(MainActivity.this, getString(R.string.secret_value));
                return true;

            } catch (JSONException e) {
                if (getApplicationContext() != null) {
                    Logger.showResult(MainActivity.this, getString(R.string.not_supported, SECRET_KEY) + " " + e.getMessage());
                }
            }
        }
        return false;
    }

    private void saveSecretValue(String secretValue) {
        try {
            KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
            String mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);

            File secretFile = new File(getFilesDir(), SECRET_FILE);
            if (secretFile.exists()) {
                secretFile.delete();
            }

            EncryptedFile encryptedFile = new EncryptedFile.Builder(
                    secretFile,
                    MainActivity.this,
                    mainKeyAlias,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build();

            byte[] fileContent = secretValue.getBytes(StandardCharsets.UTF_8);
            OutputStream outputStream = encryptedFile.openFileOutput();
            outputStream.write(fileContent);
            outputStream.flush();
            outputStream.close();
        } catch (GeneralSecurityException | IOException e) {
            if (getApplicationContext() != null) {
                Logger.showResult(MainActivity.this, e.getMessage());
            }
        }
    }

    private String loadSecretValue() {
        try {
            KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
            String mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);

            File file = new File(getFilesDir(), SECRET_FILE);

            if (!file.exists())
                return null;

            EncryptedFile encryptedFile = new EncryptedFile.Builder(
                    file,
                    MainActivity.this,
                    mainKeyAlias,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build();

            InputStream inputStream = encryptedFile.openFileInput();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int nextByte = inputStream.read();
            while (nextByte != -1) {
                byteArrayOutputStream.write(nextByte);
                nextByte = inputStream.read();
            }

            return byteArrayOutputStream.toString();
        } catch (GeneralSecurityException | IOException e) {
            if (getApplicationContext() != null) {
                Logger.showResult(MainActivity.this, e.getMessage());
            }
        }
        return null;
    }

    /**
     * Receives notification about config update.
     */
    private class ConfigChangeObserver extends ConfigService.AbstractConfigChangeObserver {
        public ConfigChangeObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(JSONObject updatedData) {
            if (!isSecretValueUpdate) {
                showSnackBar("ConfigService onChange()");
                mAlertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.config_observer))
                        .setMessage(getString(R.string.config_change))
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getConfigComplete(updatedData);
                                updatePrintOption(updatedData);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getConfigComplete(updatedData);
                                dialog.dismiss();
                            }
                        })
                        .show();
            } else {
                isSecretValueUpdate = false;
                getConfigComplete(updatedData);
            }
        }
    }

    /**
     * Exception in could be because of following reasons
     * <ol>
     * <li>Library is not installed</li>
     * <li>Library update is needed</li>
     * <li>Version issue, unsupported</li>
     * </ol>
     */
    public void handleException(final Throwable t) {
        String errorMsg;
        showProgress(View.GONE);
        mUpdateConfigButton.setEnabled(false);

        if (t instanceof SsdkUnsupportedException) {
            switch (((SsdkUnsupportedException) t).getType()) {
                case SsdkUnsupportedException.LIBRARY_NOT_INSTALLED:
                case SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED:
                    errorMsg = getString(R.string.sdk_support_missing);
                    break;
                default:
                    errorMsg = getString(R.string.unknown_error);
            }
        } else {
            errorMsg = t.getMessage();
        }
        Log.e(TAG, errorMsg);
        mAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(errorMsg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .show();
    }

    public void showSnackBar(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSnackBar == null) {
                    mSnackBar = Snackbar.make(mContainer, "", Snackbar.LENGTH_INDEFINITE);
                    View snackBarView = mSnackBar.getView();
                    TextView tv = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
                    tv.setMaxLines(3);
                }
                mSnackBar.setText(text);
                mSnackBar.setActionTextColor(getResources().getColor(R.color.snackbar_button_color));
                mSnackBar.setAction(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mSnackBar != null) {
                            mSnackBar.dismiss();
                            mSnackBar = null;
                        }
                    }
                }).show();
            }
        });
    }
}