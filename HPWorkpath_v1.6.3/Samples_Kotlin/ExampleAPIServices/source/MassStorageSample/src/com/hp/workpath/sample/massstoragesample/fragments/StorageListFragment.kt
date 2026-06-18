// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.fragment.app.DialogFragment
import com.hp.workpath.api.massstorage.MassStorageInfo
import com.hp.workpath.sample.massstoragesample.MainActivity
import com.hp.workpath.sample.massstoragesample.R

class StorageListFragment : DialogFragment() {

    private var mMassStorageInfoList: ArrayList<MassStorageInfo>? = null
    private lateinit var mListener: ISelectedStorageListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lateinit var activity: Activity
        if (context is Activity) {
            activity = context
        }
        try {
            mListener = activity as ISelectedStorageListener
        } catch (e: ClassCastException) {
            Toast.makeText(activity, activity.javaClass.simpleName + " must implement ISelectedStorageListener", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "$activity must implement ISelectedStorageListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val displayView = inflater.inflate(R.layout.fragment_storage_list, null)
        findViewElements(displayView)

        val dialogBuilder = AlertDialog.Builder(requireActivity())
                .setTitle(R.string.storage_list)
                .setView(displayView)
                .setNegativeButton(android.R.string.cancel, mCancelListener)
                .setCancelable(false)
        return dialogBuilder.create()
    }

    override fun onResume() {
        super.onResume()
        if (::mListener.isInitialized.not()) {
            dismiss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.setCanceledOnTouchOutside(false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun findViewElements(view: View) {
        mMassStorageInfoList = arguments?.getParcelableArrayList(STORAGE_INFO_LIST)
        mMassStorageInfoList?.let {
            for (i in it.indices) {
                val radio = AppCompatRadioButton(activity)
                val params = RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.setMargins(0, 5, 0, 5)
                radio.layoutParams = params

                radio.text = it[i].name
                radio.tag = i
                radio.setOnCheckedChangeListener { _, _ ->
                    mListener.selectedStorage(radio.tag as Int)
                    dialog?.dismiss()
                }
                view.findViewById<RadioGroup>(R.id.storageRadioGroup).addView(radio)
            }
        }
    }

    private val mCancelListener = DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() }

    companion object {
        private const val TAG = MainActivity.TAG
        const val STORAGE_INFO_LIST = "massStorageInfoList"

        @JvmStatic
        fun newInstance(massStorageInfoList: List<MassStorageInfo>): StorageListFragment {
            val f = StorageListFragment()
            val args = Bundle()
            args.putParcelableArrayList(STORAGE_INFO_LIST, ArrayList(massStorageInfoList))
            f.arguments = args
            return f
        }
    }
}