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
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.hp.workpath.api.massstorage.CustomerDataFile
import com.hp.workpath.api.massstorage.MassStorageInfo
import com.hp.workpath.sample.massstoragesample.Logger
import com.hp.workpath.sample.massstoragesample.MainActivity
import com.hp.workpath.sample.massstoragesample.R
import com.hp.workpath.sample.massstoragesample.task.CreateFileTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar

class FileCreateFragment : DialogFragment() {

    private lateinit var filenameEditText: EditText
    private lateinit var removeButton: Button
    private lateinit var contentEditText: EditText
    private lateinit var fileTypeRadioGroup: RadioGroup

    private var mCustomerDataFile: CustomerDataFile? = null
    private var mMassStorageInfo: MassStorageInfo? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.fragment_file_create, null)
        findViewElements(dialogView)

        val dialogBuilder = AlertDialog.Builder(requireActivity())
                .setTitle(R.string.create_file)
                .setView(dialogView)
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

        fileTypeRadioGroup = view.findViewById(R.id.fileTypeRadioGroup)
        fileTypeRadioGroup.setOnCheckedChangeListener(mRadioListener)
        contentEditText = view.findViewById(R.id.contentEditText)
        filenameEditText = createNameItem.findViewById<View>(R.id.summaryTextView) as EditText
        filenameEditText.isEnabled = true
        filenameEditText.isClickable = true
        filenameEditText.isCursorVisible = true
        removeButton = createNameItem.findViewById<View>(R.id.removeButton) as Button
        removeButton.visibility = View.VISIBLE
        removeButton.setOnClickListener(mOnClickListener)
        setTempFilename()
    }

    private fun setTempFilename() {
        val fileName = SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().time) + ".txt"
        filenameEditText.setText(fileName)
    }

    private val mOnClickListener = View.OnClickListener { filenameEditText.setText("") }
    private val mRadioListener = RadioGroup.OnCheckedChangeListener { _, checkedId ->
        when (checkedId) {
            R.id.radioFileButton -> {
                setTempFilename()
                contentEditText.visibility = View.VISIBLE
            }
            R.id.radioDirectoryButton -> {
                filenameEditText.setText("")
                contentEditText.visibility = View.GONE
            }
        }
    }
    private val mCancelListener = DialogInterface.OnClickListener { _, _ -> dialog?.cancel() }
    private val mCreateListener = DialogInterface.OnClickListener { _, _ ->
        mCustomerDataFile?.run {
            var path = this.path
            if ("/" != path.substring(path.length - 1)) {
                path += "/"
            }
            val filePath = path + filenameEditText.text.toString()
            Log.i(MainActivity.TAG, "CreateFile Path: $filePath")

            val customerDataFile = CustomerDataFile(requireActivity(), mMassStorageInfo, filePath)
            val content = contentEditText.text.toString()

            (activity as MainActivity).enableButton(false)
            if (fileTypeRadioGroup.checkedRadioButtonId == R.id.radioFileButton) {
                lifecycleScope.launch(Dispatchers.Default) {
                    CreateFileTask(activity as MainActivity, customerDataFile, content).execute()
                }
            } else {
                val result = customerDataFile.mkdir()
                Logger.showResult(activity, "mkdir: $result")
                if (result) {
                    (activity as MainActivity).displayFileList(customerDataFile.parentFile)
                } else {
                    // do nothing
                }
            }
        }
    }

    companion object {
        const val CUSTOMER_DATA_FILE_KEY = "customerDataFile"
        const val MASS_STORAGE_DATA_KEY = "massStorageDataKey"

        @JvmStatic
        fun newInstance(massStorageInfo: MassStorageInfo?, customerDataFile: CustomerDataFile?): FileCreateFragment {
            val f = FileCreateFragment()
            val args = Bundle()
            args.putParcelable(CUSTOMER_DATA_FILE_KEY, customerDataFile)
            args.putParcelable(MASS_STORAGE_DATA_KEY, massStorageInfo)
            f.arguments = args
            return f
        }
    }
}