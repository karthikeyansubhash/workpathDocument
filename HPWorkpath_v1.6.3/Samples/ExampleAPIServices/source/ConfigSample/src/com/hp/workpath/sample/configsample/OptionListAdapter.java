// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.configsample;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hp.workpath.sample.configsample.model.SimplePrintOption;

import java.util.ArrayList;

public class OptionListAdapter extends ArrayAdapter {

    Context mContext;
    ArrayList<OptionData> mOptions;

    public OptionListAdapter(Context context) {
        super(context, 0);
        mContext = context;
        mOptions = new ArrayList<>();
    }

    public void setItem(SimplePrintOption option) {
        mOptions = new ArrayList<>();
        if (option != null) {
            if (!TextUtils.isEmpty(option.getColorMode())) {
                OptionData data = new OptionData(getContext().getString(R.string.print_color_mode), option.getColorMode());
                mOptions.add(data);
            }

            if (!TextUtils.isEmpty(option.getPaperSize())) {
                OptionData data = new OptionData(getContext().getString(R.string.print_paper_size), option.getPaperSize());
                mOptions.add(data);
            }

            OptionData data = new OptionData(getContext().getString(R.string.print_copies), Integer.toString(option.getCopies()));
            mOptions.add(data);
        }
    }

    @Override
    public int getCount() {
        return mOptions.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.layout_option, parent, false);
        TextView nameTextView = rowView.findViewById(R.id.optionNameTextView);
        TextView valueTextView = rowView.findViewById(R.id.optionValueTextView);

        nameTextView.setText(mOptions.get(position).getOptionName());
        valueTextView.setText(mOptions.get(position).getOptionValue());

        return rowView;
    }

    private class OptionData{
        String optionName;
        String optionValue;

        public OptionData (String optionName, String optionValue) {
            this.optionName = optionName;
            this.optionValue = optionValue;
        }

        public String getOptionName() {
            return optionName;
        }

        public String getOptionValue() {
            return optionValue;
        }
    }
}
