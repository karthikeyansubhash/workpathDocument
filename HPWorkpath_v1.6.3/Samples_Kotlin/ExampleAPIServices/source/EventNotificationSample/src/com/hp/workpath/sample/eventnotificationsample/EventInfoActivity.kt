package com.hp.workpath.sample.eventnotificationsample

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hp.workpath.sample.eventnotificationsample.util.PreferenceManager
import org.json.JSONException
import org.json.JSONObject

class EventInfoActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_info)

        val data: JSONObject?
        try {
            data = JSONObject(intent.getStringExtra("data"))
            if (data.has(PreferenceManager.DATA)) {
                val eventInfoTextView = findViewById<TextView>(R.id.event_info)
                eventInfoTextView.movementMethod = ScrollingMovementMethod.getInstance()
                eventInfoTextView.text = data.getString(PreferenceManager.DATA).replace(", ", ",\n")
            }
        } catch (e: JSONException) {
            // ignore
        }
    }
}