package com.hp.workpath.sample.eventnotificationsample.util;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PreferenceManager {

    private static final String PREFS_NAME = "EventPrefs";

    private static final String KEY_INDEX = "keyIndex";

    public static final String DATE = "date";

    public static final String EVENT = "event";

    public static final String DATA = "data";

    static public void saveEvent(Context context, String event, String data) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(DATE, time);
            jsonObject.put(EVENT, event);
            if (data != null && !data.isEmpty())
                jsonObject.put(DATA, data);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        int keyIndex = prefs.getInt(KEY_INDEX, 0);
        editor.putString(String.valueOf(keyIndex), jsonObject.toString());
        editor.putInt(KEY_INDEX, keyIndex + 1).apply();
    }

    static public List<JSONObject> getAllEvents(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();
        List<Integer> sortedKeys = new ArrayList<>();

        for (String key : allEntries.keySet()) {
            try {
                sortedKeys.add(Integer.parseInt(key));
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        Collections.sort(sortedKeys, Collections.reverseOrder());

        List<JSONObject> events = new ArrayList<>();
        for (int key: sortedKeys) {
            try {
                events.add(new JSONObject(prefs.getString(String.valueOf(key), "")));
            } catch (JSONException e) {
                // ignore
            }
        }
        return events;
    }

    static public void registerPreferenceChangeListener(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    static public void unregisterPreferenceChangeListener(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
