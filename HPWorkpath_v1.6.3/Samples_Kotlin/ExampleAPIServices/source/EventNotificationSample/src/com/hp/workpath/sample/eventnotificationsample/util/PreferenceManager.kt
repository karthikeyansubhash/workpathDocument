package com.hp.workpath.sample.eventnotificationsample.util

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PreferenceManager {
    companion object {
        private const val PREFS_NAME = "EventPrefs"

        private const val KEY_INDEX = "keyIndex"

        const val DATE = "date"

        const val EVENT = "event"

        const val DATA = "data"

        @JvmStatic
        fun saveEvent(context: Context?, event: String?, data: String?) {
            val prefs = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs?.edit()
            val time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
            val jsonObject = JSONObject()
            try {
                jsonObject.put(DATE, time)
                jsonObject.put(EVENT, event)
                if (!data.isNullOrEmpty()) {
                    jsonObject.put(DATA, data)
                }
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            val keyIndex = prefs?.getInt(KEY_INDEX, 0)
            editor?.putString(keyIndex.toString(), jsonObject.toString())
            if (keyIndex != null) {
                editor?.putInt(KEY_INDEX, keyIndex + 1)?.apply()
            }
        }

        @JvmStatic
        fun getAllEvents(context: Context): List<JSONObject> {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val allEntries = prefs.all
            val sortedKeys = mutableListOf<Int>()

            for (key in allEntries.keys) {
                try {
                    sortedKeys.add(key.toInt())
                } catch (e: NumberFormatException) {
                    // ignore
                }
            }
            sortedKeys.sortDescending()

            val events = mutableListOf<JSONObject>()
            for (key in sortedKeys) {
                try {
                    events.add(JSONObject(prefs.getString(key.toString(), "") ?: ""))
                } catch (e: JSONException) {
                    // ignore
                }
            }
            return events
        }

        @JvmStatic
        fun registerPreferenceChangeListener(context: Context, listener: SharedPreferences.OnSharedPreferenceChangeListener) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.registerOnSharedPreferenceChangeListener(listener)
        }

        @JvmStatic
        fun unregisterPreferenceChangeListener(context: Context, listener: SharedPreferences.OnSharedPreferenceChangeListener) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
}