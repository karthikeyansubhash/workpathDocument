// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.eventnotificationsample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hp.workpath.api.Workpath;
import com.hp.workpath.sample.eventnotificationsample.service.ForegroundService;
import com.hp.workpath.sample.eventnotificationsample.util.PreferenceManager;

import org.json.JSONObject;

import java.util.List;


/**
 * Main activity for EventNotification Sample.
 */
public final class MainActivity extends AppCompatActivity {

    public static final String TAG = "[SAMPLE]" + "EventNotification";

    private ListView mListView;

    private String SCREEN_4_3_INCH = "Screen_4.3_Inch";

    private SharedPreferences.OnSharedPreferenceChangeListener mPreferenceChangeListener = (sharedPreferences, key) -> {
        List<JSONObject> events = PreferenceManager.getAllEvents(MainActivity.this);
        mListView.setAdapter(new EventListAdapter(MainActivity.this, events));
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View view = findViewById(R.id.layout);
        if (SCREEN_4_3_INCH.equals(view.getTag())) {
            Toolbar toolBar = findViewById(R.id.toolbar);
            setSupportActionBar(toolBar);
        }

        mListView = findViewById(R.id.listView);

        List<JSONObject> events = PreferenceManager.getAllEvents(this);
        mListView.setAdapter(new EventListAdapter(this, events));

        mListView.setOnItemClickListener((parent, view1, position, id) -> {
            JSONObject event = (JSONObject) parent.getItemAtPosition(position);
            Intent intent = new Intent(MainActivity.this, EventInfoActivity.class);
            intent.putExtra("data", event.toString());
            startActivity(intent);
        });

        PreferenceManager.registerPreferenceChangeListener(this, mPreferenceChangeListener);

        // To prevent the app process being killed by reset,
        //   the app should start a foreground service as below.
        // If the app does not starts the service
        //   then the app would be killed
        //   when it gets and processes an event on a receiver.
        Intent startServiceIntent = new Intent(getApplicationContext(), ForegroundService.class);
        getApplicationContext().startService(startServiceIntent);

        // find the text and button
        findViewElements();
    }

    private void findViewElements() {
        // setting headers
        ((TextView) findViewById(R.id.headerVersion)).setText(R.string.header_version);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.unregisterPreferenceChangeListener(this, mPreferenceChangeListener);
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
            version = getString(R.string.version_code, pInfo.versionName, pInfo.versionCode, sdkInfo.getVersionName(), sdkInfo.getVersionCode());
        } catch (Throwable t) {
            Log.e(TAG, "Failed to get version info: " + t.getMessage());
        }
        versionMenu.setTitle(version);
        return true;
    }
}
