// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.hp.workpath.api.copier.StoredJobInfo
import com.hp.workpath.sample.copysample.R
import com.hp.workpath.sample.copysample.databinding.DialogRadioListBinding
import com.hp.workpath.sample.copysample.fragments.StoreJobFragment.Companion.REQUEST_KEY

class ListDialogFragment : DialogFragment() {

    private var storedJobInfoList: ArrayList<StoredJobInfo>? = null
    private var currentStoredJobId: String? = null
    private lateinit var mRadioGroup: RadioGroup
    private var mBindingFragment: DialogRadioListBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val mBindingDialogRadioList get() = mBindingFragment!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        mBindingFragment = DialogRadioListBinding.inflate(inflater)
        val view = mBindingDialogRadioList.root
        storedJobInfoList = requireArguments().getParcelableArrayList(STORED_JOB_INFO_LIST)
        currentStoredJobId = requireArguments().getString(CURRENT_SELECTED)
        findViewElements(view)
        val dialogBuilder = AlertDialog.Builder(requireActivity())
            .setTitle(R.string.stored_job)
            .setView(view)
            .setNegativeButton(android.R.string.cancel, mCancelListener)
            .setCancelable(false)
        return dialogBuilder.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBindingFragment = null
    }

    private fun findViewElements(view: View) {
        mRadioGroup = view.findViewById(R.id.radioGroup)
        storedJobInfoList?.let {
            if (it.size == 0) {
                mBindingDialogRadioList.emptyTextView.visibility = View.VISIBLE
            } else {
                for (i in it.indices) {
                    val radio = AppCompatRadioButton(activity)
                    val params = RadioGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(0, 5, 0, 5)
                    radio.layoutParams = params
                    val storedJobInfo = it[i]
                    val infoStringBuilder = StringBuilder()
                    infoStringBuilder.append("(").append(storedJobInfo.storedJobId).append(")\n")
                        .append("FolderName: ").append(storedJobInfo.storedJobFolderName)
                        .append(", ")
                        .append("JobName: ").append(storedJobInfo.storedJobName)
                    radio.text = infoStringBuilder.toString()
                    radio.tag = storedJobInfo.storedJobId
                    if (!TextUtils.isEmpty(currentStoredJobId)
                        && currentStoredJobId == storedJobInfo.storedJobId
                    ) {
                        radio.isChecked = true
                    }
                    radio.setOnCheckedChangeListener { _, _ ->
                        parentFragmentManager.setFragmentResult(
                            REQUEST_KEY, // Same request key StoreJobFragment used to register its listener
                            bundleOf(CURRENT_SELECTED to radio.tag as String) // The data to be passed to StoreJobFragment
                        )
                        dialog?.dismiss()
                    }
                    mRadioGroup.addView(radio)
                }
            }
        } ?: run {
            mBindingDialogRadioList.emptyTextView.visibility = View.VISIBLE
        }
    }

    private val mCancelListener = DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() }

    companion object {
        const val CURRENT_SELECTED = "currentSelected"
        const val STORED_JOB_INFO_LIST = "storedJobInfoList"

        @JvmStatic
        fun newInstance(
            storedJobInfoList: List<StoredJobInfo>?,
            currentStoredJobId: String?
        ): ListDialogFragment {
            val f = ListDialogFragment()
            val args = Bundle()
            args.putString(CURRENT_SELECTED, currentStoredJobId)
            args.putParcelableArrayList(
                STORED_JOB_INFO_LIST,
                storedJobInfoList?.let { ArrayList(it) })
            f.arguments = args
            return f
        }
    }
}