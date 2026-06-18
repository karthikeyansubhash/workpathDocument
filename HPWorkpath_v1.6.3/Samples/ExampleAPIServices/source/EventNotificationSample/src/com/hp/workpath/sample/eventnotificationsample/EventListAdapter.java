package com.hp.workpath.sample.eventnotificationsample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hp.workpath.sample.eventnotificationsample.util.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class EventListAdapter extends BaseAdapter {

    private Context mContext;

    private List<JSONObject> mEventList;

    public EventListAdapter(Context context, List<JSONObject> eventList) {
        mContext = context;
        mEventList = eventList;
    }

    @Override
    public int getCount() {
        return mEventList.size();
    }

    @Override
    public Object getItem(int position) {
        return mEventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        JSONObject event = mEventList.get(position);

        try {
            ((TextView) convertView.findViewById(android.R.id.text1)).setText(event.getString(PreferenceManager.EVENT));
            ((TextView) convertView.findViewById(android.R.id.text2)).setText(event.getString(PreferenceManager.DATE));
        } catch (JSONException e) {
            // ignore
        }

        return convertView;
    }
}
