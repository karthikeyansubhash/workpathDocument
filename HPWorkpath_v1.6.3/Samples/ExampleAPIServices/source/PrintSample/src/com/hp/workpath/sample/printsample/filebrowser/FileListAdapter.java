// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.printsample.filebrowser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hp.workpath.sample.printsample.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileListAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;

    private List<File> mData = new ArrayList<File>();

    public FileListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }

    @Override
    public File getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    public void setListItems(List<File> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_file, parent, false);
        }

        TextView fileNameTextView = convertView.findViewById(R.id.fileNameTextView);

        File file = getItem(position);
        fileNameTextView.setText(file.getName());
        int icon = file.isDirectory() ? R.drawable.ic_folder : R.drawable.ic_file;
        fileNameTextView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);

        return convertView;
    }

}