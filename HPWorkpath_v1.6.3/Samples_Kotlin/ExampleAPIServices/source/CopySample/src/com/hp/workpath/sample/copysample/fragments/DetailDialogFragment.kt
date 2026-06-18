// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.hp.workpath.api.copier.StoredJobInfo
import com.hp.workpath.sample.copysample.R

class DetailDialogFragment : DialogFragment() {

    private var storedJobInfo: StoredJobInfo? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_detail, null)
        storedJobInfo = requireArguments().getParcelable(STORED_JOB_INFO)
        findViewElements(view)
        val dialogBuilder = AlertDialog.Builder(requireActivity())
                .setTitle(R.string.stored_job)
                .setView(view)
                .setNegativeButton(android.R.string.cancel, mCancelListener)
                .setCancelable(false)
        return dialogBuilder.create()
    }

    private fun findViewElements(view: View) {
        storedJobInfo?.let {
            setChildView(view.findViewById(R.id.layoutStoreJobId), R.string.stored_job_id, it.storedJobId)
            setChildView(view.findViewById(R.id.layoutStoredJobFolderName), R.string.stored_job_folder_name, it.storedJobFolderName)
            setChildView(view.findViewById(R.id.layoutStoredJobName), R.string.stored_job_name, it.storedJobName)
            setChildView(view.findViewById(R.id.layoutStoredJobUserName), R.string.stored_job_username, it.storedJobUserName)
            if (it.storedJobPasswordType != null) {
                setChildView(view.findViewById(R.id.layoutStoredJobPasswordType), R.string.stored_job_password_type, it.storedJobPasswordType.name)
            }
            setChildView(view.findViewById(R.id.layoutStoreJobTimestamp), R.string.stored_job_timestamp, it.storeJobTimestamp)
            setChildView(view.findViewById(R.id.layoutCopies), R.string.copies, it.copies.toString())
            if (it.colorMode != null) {
                setChildView(view.findViewById(R.id.layoutColorMode), R.string.color_mode, it.colorMode.name)
            }
            if (it.originalMediaSize != null) {
                setChildView(view.findViewById(R.id.layoutOriginalMediaSize), R.string.original_media_size, it.originalMediaSize.name)
            }
            if (it.outputSides != null) {
                setChildView(view.findViewById(R.id.layoutOutputSides), R.string.output_sides, it.outputSides.name)
            }
            setChildView(view.findViewById(R.id.layoutTotalPages), R.string.total_pages, it.totalPages.toString())
        }
    }

    private val mCancelListener = DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() }

    private fun setChildView(viewGroup: ViewGroup, id: Int, text: String) {
        (viewGroup.findViewById<View>(R.id.titleTextView) as TextView).setText(id)
        (viewGroup.findViewById<View>(R.id.summaryTextView) as TextView).text = text
    }

    companion object {
        const val STORED_JOB_INFO = "storedJobInfo"

        fun newInstance(storedJobInfo: StoredJobInfo?): DetailDialogFragment {
            val f = DetailDialogFragment()
            val args = Bundle()
            args.putParcelable(STORED_JOB_INFO, storedJobInfo)
            f.arguments = args
            return f
        }
    }
}