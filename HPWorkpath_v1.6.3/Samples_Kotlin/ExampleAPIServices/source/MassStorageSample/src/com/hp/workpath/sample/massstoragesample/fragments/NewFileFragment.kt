// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
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
import com.hp.workpath.sample.massstoragesample.MainActivity
import com.hp.workpath.sample.massstoragesample.R
import com.hp.workpath.sample.massstoragesample.task.CreateNewFileTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar

class NewFileFragment : DialogFragment() {

    private var mCustomerDataFile: CustomerDataFile? = null
    private var mMassStorageInfo: MassStorageInfo? = null

    private lateinit var fileNameEditText: EditText
    private lateinit var removeButton: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val displayView = inflater.inflate(R.layout.fragment_new_file, null)
        findViewElements(displayView)
        val dialogBuilder = AlertDialog.Builder(requireActivity())
                .setTitle(R.string.create_new_file)
                .setView(displayView)
                .setNegativeButton(android.R.string.cancel, mCancelListener)
                .setPositiveButton(R.string.create, mCreateListener)
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

        val createNameItem = view.findViewById<ViewGroup>(R.id.createNameItem)
        (createNameItem.findViewById<View>(R.id.titleTextView) as TextView).text = requireActivity().getString(R.string.name)

        fileNameEditText = createNameItem.findViewById<View>(R.id.summaryTextView) as EditText
        fileNameEditText.isEnabled = true
        fileNameEditText.isClickable = true
        fileNameEditText.isCursorVisible = true
        removeButton = createNameItem.findViewById<View>(R.id.removeButton) as Button
        removeButton.visibility = View.VISIBLE
        removeButton.setOnClickListener(mOnClickListener)
        setTempFilename()
    }

    private val mOnClickListener = View.OnClickListener { fileNameEditText.setText("") }

    private fun setTempFilename() {
        val fileName = SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().time) + ".txt"
        fileNameEditText.setText(fileName)
    }

    private val mCancelListener = DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() }
    private val mCreateListener = DialogInterface.OnClickListener { _, _ ->
        mCustomerDataFile?.run {
            var path = this.path
            if ("/" != path.substring(path.length - 1)) {
                path += "/"
            }
            val filePath = path + fileNameEditText.text.toString()
            Log.i(MainActivity.TAG, "CreateNewFile Path: $filePath")

            (activity as MainActivity).enableButton(false)
            (activity as MainActivity).showProgress(View.VISIBLE)

            val customerDataFile = CustomerDataFile(requireActivity(), mMassStorageInfo, filePath)
            lifecycleScope.launch (Dispatchers.Default) {
                CreateNewFileTask(activity as MainActivity, customerDataFile).execute()
            }
        }
    }

    companion object {
        const val CUSTOMER_DATA_FILE_KEY = "customerDataFile"
        const val MASS_STORAGE_DATA_KEY = "massStorageDataKey"

        @JvmStatic
        fun newInstance(massStorageInfo: MassStorageInfo?, customerDataFile: CustomerDataFile?): NewFileFragment {
            val f = NewFileFragment()
            val args = Bundle()
            args.putParcelable(CUSTOMER_DATA_FILE_KEY, customerDataFile)
            args.putParcelable(MASS_STORAGE_DATA_KEY, massStorageInfo)
            f.arguments = args
            return f
        }
    }
}