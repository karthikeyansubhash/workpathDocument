// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample.filebrowser;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.fragment.app.ListFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.hp.workpath.sample.emailsample.R;

import java.io.File;
import java.util.List;

import static com.hp.workpath.sample.emailsample.filebrowser.FileUtils.copyAssets;

public class FileListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<File>> {

    private static final int LOADER_ID_FILES = 1;

    public interface Callbacks {
        void onFileSelected(File file);
    }

    private FileListAdapter mAdapter;
    private String mPath;

    private Callbacks mListener;

    public static FileListFragment newInstance(String path) {
        FileListFragment fragment = new FileListFragment();
        Bundle args = new Bundle();
        args.putString(FileUtils.PATH, path);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (Callbacks) activity;
        } catch (ClassCastException cce) {
            throw new ClassCastException(activity.toString()
                    + " must implement FileListFragment.Callbacks");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        copyAssets(getActivity());
        mAdapter = new FileListAdapter(getActivity());
        mPath = getArguments() != null ? getArguments().getString(FileUtils.PATH) :
                getActivity().getFilesDir().getAbsolutePath();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setEmptyText(getString(R.string.empty_directory));
        setListAdapter(mAdapter);
        setListShown(false);

        getLoaderManager().initLoader(LOADER_ID_FILES, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        FileListAdapter adapter = (FileListAdapter) l.getAdapter();

        if (adapter != null) {
            File file = adapter.getItem(position);
            mPath = file.getAbsolutePath();
            mListener.onFileSelected(file);
        }
    }

    @Override
    public Loader<List<File>> onCreateLoader(int id, Bundle args) {
        return new FileLoader(getActivity(), mPath);
    }

    @Override
    public void onLoadFinished(Loader<List<File>> loader, List<File> data) {
        mAdapter.setListItems(data);

        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<File>> loader) {
        mAdapter.clear();
    }
}
