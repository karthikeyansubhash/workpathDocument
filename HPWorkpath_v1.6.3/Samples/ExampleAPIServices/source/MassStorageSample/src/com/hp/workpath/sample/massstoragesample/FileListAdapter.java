// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hp.workpath.sample.massstoragesample.model.FileInfo;

import java.util.ArrayList;
import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> implements View.OnClickListener {

    public static final String BACK = "<< (UP)";

    private final FileListOnClickListener mFileListOnClickListener;
    private List<FileInfo> mData = new ArrayList<>();
    private boolean[] mCheckedItemArray;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView fileNameTextView;
        public CheckBox fileCheckBox;

        ViewHolder(View view) {
            super(view);
            fileNameTextView = view.findViewById(R.id.fileNameTextView);
            fileCheckBox = view.findViewById(R.id.checkBox);
        }
    }

    public interface FileListOnClickListener {
        void onDeleteButton(boolean enable);

        void onRenameButton(boolean enable);

        void onItemClick(View view);
    }

    public FileListAdapter(FileListOnClickListener fileListOnClickListener) {
        this.mFileListOnClickListener = fileListOnClickListener;
    }

    public void clear() {
        Log.i(MainActivity.TAG, "FileListAdapter clear");
        mCheckedItemArray = null;
        mData.clear();
        notifyItemRangeRemoved(0, mData.size());
    }

    public FileInfo getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_file, parent, false);
        view.setOnClickListener(this);
        view.findViewById(R.id.checkBox).setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FileInfo file = getItem(position);
        initViewHolder(holder);
        holder.fileCheckBox.setTag(position);
        holder.fileNameTextView.setText(file.getFileName());
        if (BACK.equals(file.getFileName())) {
            holder.fileCheckBox.setVisibility(View.INVISIBLE);
        }
        holder.fileCheckBox.setChecked(mCheckedItemArray[position]);
    }

    private void initViewHolder(ViewHolder holder) {
        if (holder != null) {
            holder.fileCheckBox.setVisibility(View.VISIBLE);
            holder.fileNameTextView.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.checkBox) {
            mCheckedItemArray[(int) v.getTag()] = ((CheckBox) v).isChecked();
            int checkedCount = 0;
            for (boolean b : mCheckedItemArray) {
                if (b) {
                    ++checkedCount;
                }
            }
            mFileListOnClickListener.onDeleteButton(checkedCount >= 1);
            mFileListOnClickListener.onRenameButton(checkedCount == 1);
        } else {
            mFileListOnClickListener.onItemClick(v);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public boolean[] getCheckedItemArray() {
        return mCheckedItemArray;
    }

    public void setListItems(List<FileInfo> dataList) {
        mData = dataList;
        mCheckedItemArray = new boolean[mData.size()];
        notifyDataSetChanged();
    }
}