package com.hp.workpath.sample.eventnotificationsample

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.hp.workpath.sample.eventnotificationsample.util.PreferenceManager
import org.json.JSONException
import org.json.JSONObject

class EventListAdapter(context: Context, eventList: List<JSONObject>) : BaseAdapter() {

    private var mContext: Context = context

    private var mEventList: List<JSONObject> = eventList

    override fun getCount(): Int {
        return mEventList.size
    }

    override fun getItem(position: Int): Any {
        return mEventList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView =
            LayoutInflater.from(mContext).inflate(R.layout.simple_list_item_2, parent, false)

        val event = mEventList[position]

        try {
            (convertView.findViewById<View>(R.id.text1) as TextView).text =
                event.getString(PreferenceManager.EVENT)
            (convertView.findViewById<View>(R.id.text2) as TextView).text =
                event.getString(PreferenceManager.DATE)
        } catch (e: JSONException) {
            // ignore
        }

        return convertView
    }
}