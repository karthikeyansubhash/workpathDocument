// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.hp.workpath.api.massstorage.CustomerDataFile
import com.hp.workpath.sample.massstoragesample.R
import com.hp.workpath.sample.massstoragesample.task.ReadFileTask
import com.hp.workpath.sample.massstoragesample.task.ReadFileTask.ReadFileTaskInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class FileInfoFragment : DialogFragment() {

    private var mCustomerDataFile: CustomerDataFile? = null
    private lateinit var progressBar: ProgressBar
    private lateinit var nameEditText: EditText
    private lateinit var contentTextView: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_file_info, null)
        findViewElements(view)

        val dialogBuilder = AlertDialog.Builder(requireActivity())
                .setTitle(R.string.file_info)
                .setView(view)
                .setNegativeButton(android.R.string.cancel, mCancelListener)
                .setCancelable(false)
        return dialogBuilder.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.setCanceledOnTouchOutside(false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun findViewElements(view: View) {
        mCustomerDataFile = arguments?.getParcelable(CUSTOMER_DATA_FILE_KEY)
        mCustomerDataFile?.run {
            val getNameItem = view.findViewById<ViewGroup>(R.id.getNameItem)
            (getNameItem.findViewById<View>(R.id.titleTextView) as TextView).text = requireActivity().getString(R.string.name)
            progressBar = view.findViewById(R.id.progressBar)
            contentTextView = view.findViewById(R.id.contentTextView)
            contentTextView.isEnabled = false
            nameEditText = getNameItem.findViewById<View>(R.id.summaryTextView) as EditText
            nameEditText.setText(name)
            nameEditText.isEnabled = false

            val getPathItem = view.findViewById<ViewGroup>(R.id.getPathItem)
            (getPathItem.findViewById<View>(R.id.titleTextView) as TextView).text = requireActivity().getString(R.string.path_name)
            (getPathItem.findViewById<View>(R.id.summaryTextView) as TextView).text = path
            (getPathItem.findViewById<View>(R.id.summaryTextView) as TextView).isEnabled = false

            val lengthItem = view.findViewById<ViewGroup>(R.id.lengthItem)
            (lengthItem.findViewById<View>(R.id.titleTextView) as TextView).text = requireActivity().getString(R.string.length)
            (lengthItem.findViewById<View>(R.id.summaryTextView) as TextView).text = length().toString()
            (lengthItem.findViewById<View>(R.id.summaryTextView) as TextView).isEnabled = false

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val lastModified = lastModified().toString() + " (" + sdf.format(Date(lastModified())) + ")"
            val lastModifiedItem = view.findViewById<ViewGroup>(R.id.lastModifiedItem)
            (lastModifiedItem.findViewById<View>(R.id.titleTextView) as TextView).text = requireActivity().getString(R.string.last_modified)
            (lastModifiedItem.findViewById<View>(R.id.summaryTextView) as TextView).text = lastModified
            (lastModifiedItem.findViewById<View>(R.id.summaryTextView) as TextView).isEnabled = false

            if (length() > 0) {
                lifecycleScope.launch (Dispatchers.Default) {
                    ReadFileTask(requireContext(), this@run, mReadFileTaskInterface).execute()
                }
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }

    private var mReadFileTaskInterface: ReadFileTaskInterface = object : ReadFileTaskInterface {
        override fun fileContent(content: String?) {
            progressBar.visibility = View.GONE
            contentTextView.text = content
        }
    }

    private val mCancelListener = DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() }

    companion object {
        const val CUSTOMER_DATA_FILE_KEY = "customerDataFile"

        @JvmStatic
        fun newInstance(customerDataFile: CustomerDataFile): FileInfoFragment {
            val f = FileInfoFragment()
            val args = Bundle()
            args.putParcelable(CUSTOMER_DATA_FILE_KEY, customerDataFile)
            f.arguments = args
            return f
        }
    }
}