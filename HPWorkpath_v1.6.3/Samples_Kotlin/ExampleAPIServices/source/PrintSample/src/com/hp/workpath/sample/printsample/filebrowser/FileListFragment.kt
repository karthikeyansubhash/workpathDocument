// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.printsample.filebrowser

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.fragment.app.ListFragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.hp.workpath.sample.printsample.R
import java.io.File

class FileListFragment : ListFragment(), LoaderManager.LoaderCallbacks<List<File>> {
    interface Callbacks {
        fun onFileSelected(file: File?)
    }

    private lateinit var mAdapter: FileListAdapter
    private lateinit var mListener: Callbacks
    private lateinit var mPath: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        var activity: Activity? =null
        if (context is Activity) {
            activity = context
        }
        mListener = try {
            activity as Callbacks
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString()
                    + " must implement FileListFragment.Callbacks")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter = FileListAdapter(activity)
        mPath = arguments?.getString(FileUtils.PATH) ?: requireActivity().filesDir.absolutePath
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setEmptyText(getString(R.string.empty_directory))
        listAdapter = mAdapter
        setListShown(false)
        LoaderManager.getInstance(this).initLoader(LOADER_ID_FILES, null, this)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        val adapter = l.adapter as FileListAdapter
        val file = adapter.getItem(position)
        mPath = file.absolutePath
        mListener.onFileSelected(file)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<File>> {
        return FileLoader(requireContext(), mPath)
    }

    override fun onLoadFinished(loader: Loader<List<File>>, data: List<File>) {
        mAdapter.setListItems(data)
        if (isResumed) {
            setListShown(true)
        } else {
            setListShownNoAnimation(true)
        }
    }

    override fun onLoaderReset(loader: Loader<List<File>>) {
        mAdapter.clear()
    }

    companion object {
        private const val LOADER_ID_FILES = 1

        @JvmStatic
        fun newInstance(path: String?): FileListFragment {
            val fragment = FileListFragment()
            val args = Bundle()
            args.putString(FileUtils.PATH, path)
            fragment.arguments = args
            return fragment
        }
    }
}