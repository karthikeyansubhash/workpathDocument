package com.hp.workpath.sample.eventnotificationsample

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hp.workpath.api.Workpath
import com.hp.workpath.sample.eventnotificationsample.databinding.ActivityMainBinding
import com.hp.workpath.sample.eventnotificationsample.service.ForegroundService
import com.hp.workpath.sample.eventnotificationsample.util.PreferenceManager
import com.hp.workpath.sample.eventnotificationsample.util.PreferenceManager.Companion.getAllEvents
import com.hp.workpath.sample.eventnotificationsample.util.PreferenceManager.Companion.registerPreferenceChangeListener
import com.hp.workpath.sample.eventnotificationsample.util.PreferenceManager.Companion.unregisterPreferenceChangeListener
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "[SAMPLE]EventNotification"
    }
    private val SCREEN_4_3_INCH = "Screen_4.3_Inch"
    private lateinit var mBindingActivityMain: ActivityMainBinding
    lateinit var mListView: ListView


    private val mPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        val events = getAllEvents(this@MainActivity)
        mListView.adapter = EventListAdapter(this@MainActivity, events)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBindingActivityMain = ActivityMainBinding.inflate(layoutInflater)
        val tag = mBindingActivityMain.layout?.tag
        if (SCREEN_4_3_INCH.equals(tag)) {
            setSupportActionBar(mBindingActivityMain.toolbar)
        }
        setContentView(mBindingActivityMain.root)

        mListView = findViewById(R.id.listView)

        val events = getAllEvents(this)
        mListView.adapter = EventListAdapter(this, events)

        mListView.onItemClickListener =
            OnItemClickListener { parent: AdapterView<*>, view1: View?, position: Int, id: Long ->
                val event = parent.getItemAtPosition(position) as JSONObject
                val intent =
                    Intent(this@MainActivity, EventInfoActivity::class.java)
                intent.putExtra("data", event.toString())
                startActivity(intent)
            }
        registerPreferenceChangeListener(this, mPreferenceChangeListener)

        // To prevent the app process being killed by reset,
        //   the app should start a foreground service as below.
        // If the app does not starts the service
        //   then the app would be killed
        //   when it gets and processes an event on a receiver.
        val startServiceIntent = Intent(applicationContext, ForegroundService::class.java)
        applicationContext.startService(startServiceIntent)

        // find the text and button
        findViewElements()
    }

    private fun findViewElements() {
        // setting headers
        (findViewById<View>(R.id.headerVersion) as TextView).setText(R.string.header_version)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterPreferenceChangeListener(this, mPreferenceChangeListener)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.version, menu)
        val versionMenu = menu.findItem(R.id.menuVersion)
        var version = ""
        try {
            val sdkInfo = Workpath.getInstance()
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            version = getString(
                R.string.version_code,
                pInfo.versionName,
                pInfo.versionCode,
                sdkInfo.versionName,
                sdkInfo.versionCode
            )
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to get version info: " + t.message)
        }
        versionMenu.setTitle(version)
        return true
    }
}