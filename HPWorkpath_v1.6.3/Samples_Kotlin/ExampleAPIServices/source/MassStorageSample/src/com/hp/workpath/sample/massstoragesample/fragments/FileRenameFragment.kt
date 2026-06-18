// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.hp.workpath.api.massstorage.CustomerDataFile
import com.hp.workpath.api.massstorage.MassStorageInfo
import com.hp.workpath.sample.massstoragesample.Logger.showResult
import com.hp.workpath.sample.massstoragesample.MainActivity
import com.hp.workpath.sample.massstoragesample.R
import com.hp.workpath.sample.massstoragesample.task.RenameFileTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FileRenameFragment : DialogFragment() {

    private var mCustomerDataFile: CustomerDataFile? = null
    private var mMassStorageInfo: MassStorageInfo? = null

    private lateinit var nameTextView: EditText
    private lateinit var removeButton: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val displayView = inflater.inflate(R.layout.fragment_file_rename, null)
        findViewElements(displayView)

        val dialogBuilder = AlertDialog.Builder(requireActivity())
                .setTitle(R.string.rename_file)
                .setView(displayView)
                .setNegativeButton(android.R.string.cancel, mCancelListener)
                .setPositiveButton(R.string.rename, mCreateListener)
                .setCancelable(false)

        return dialogBuilder.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.setCanceledOnTouchOutside(false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun findViewElements(view: View) {
        mCustomerDataFile = arguments?.getParcelable(CUSTOMER_DATA_FILE_KEY)
        mMassStorageInfo = arguments?.getParcelable(MASS_STORAGE_DATA_KEY)

        val renameNameItem = view.findViewById<ViewGroup>(R.id.renameNameItem)
        (renameNameItem.findViewById<View>(R.id.titleTextView) as TextView).text = requireActivity().getString(R.string.name)

        nameTextView = renameNameItem.findViewById<View>(R.id.summaryTextView) as EditText
        nameTextView.setText(mCustomerDataFile?.name)
        nameTextView.isEnabled = true
        nameTextView.isClickable = true
        nameTextView.isCursorVisible = true
        removeButton = renameNameItem.findViewById<View>(R.id.removeButton) as Button
        removeButton.visibility = View.VISIBLE
        removeButton.setOnClickListener(mOnClickListener)
    }

    private val mOnClickListener = View.OnClickListener { nameTextView.setText("") }
    private val mCancelListener = DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() }
    private val mCreateListener = DialogInterface.OnClickListener { _, _ ->
        var path = ""
        mCustomerDataFile?.run {
            path += parentFile.path

            if ("/" != path.substring(path.length - 1)) {
                path += "/"
            }
            if (!TextUtils.isEmpty(nameTextView.text.toString())) {
                val filePath = path + nameTextView.text.toString()
                Log.i(MainActivity.TAG, "renameTo Path: $filePath")

                (activity as MainActivity).enableButton(false)
                (activity as MainActivity).showProgress(View.VISIBLE)
                val dest = CustomerDataFile(requireActivity(), mMassStorageInfo, filePath)
                lifecycleScope.launch(Dispatchers.Default) {
                    RenameFileTask(activity as MainActivity, this@run, dest).execute()
                }
            } else {
                context?.run {
                    showResult(activity, getString(R.string.input_value))
                }
            }
        }
    }

    companion object {
        const val CUSTOMER_DATA_FILE_KEY = "customerDataFile"
        const val MASS_STORAGE_DATA_KEY = "massStorageDataKey"

        @JvmStatic
        fun newInstance(massStorageInfo: MassStorageInfo?, customerDataFile: CustomerDataFile?): FileRenameFragment {
            val f = FileRenameFragment()
            val args = Bundle()
            args.putParcelable(CUSTOMER_DATA_FILE_KEY, customerDataFile)
            args.putParcelable(MASS_STORAGE_DATA_KEY, massStorageInfo)
            f.arguments = args
            return f
        }
    }
}