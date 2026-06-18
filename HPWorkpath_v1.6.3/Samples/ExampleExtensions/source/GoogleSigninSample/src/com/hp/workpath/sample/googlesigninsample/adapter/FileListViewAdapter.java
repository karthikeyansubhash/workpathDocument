// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.googlesigninsample.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.api.services.drive.model.File;
import com.hp.workpath.sample.googlesigninsample.R;

import java.util.ArrayList;
import java.util.List;

public class FileListViewAdapter extends BaseAdapter {

    List<File> files;

    private final LayoutInflater mInflater;

    public FileListViewAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        files = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public File getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(List<File> files) {
        this.files = files;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            row = mInflater.inflate(R.layout.adapter_file_list, parent, false);
        }

        TextView name = row.findViewById(R.id.name_text_view);
        TextView id = row.findViewById(R.id.id_text_view);
        TextView mimeType = row.findViewById(R.id.mime_type_text_view);

        // Get the file at the current position
        File file = getItem(position);

        // Set the TextView as the file name
        name.setText(file.getName());
        mimeType.setText(file.getMimeType());
        id.setText(file.getId());

        return row;
    }
}
