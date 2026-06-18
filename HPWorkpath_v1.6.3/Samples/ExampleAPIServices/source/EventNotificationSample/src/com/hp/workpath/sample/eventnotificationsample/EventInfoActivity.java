package com.hp.workpath.sample.eventnotificationsample;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hp.workpath.sample.eventnotificationsample.util.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

public class EventInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        JSONObject data = null;
        try {
            data = new JSONObject(getIntent().getStringExtra("data"));
            if (data.has(PreferenceManager.DATA)) {
                ((TextView) findViewById(R.id.event_info)).setMovementMethod(ScrollingMovementMethod.getInstance());
                ((TextView) findViewById(R.id.event_info)).setText(data.get(PreferenceManager.DATA).toString().replace(", ", ",\n"));
            }
        } catch (JSONException e) {
            // ignore
        }
    }
}
