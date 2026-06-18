// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.hp.workpath.sample.statisticsample.Logger
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.StatisticActivity.Companion.REQUEST_KEY
import com.hp.workpath.sample.statisticsample.task.CommitTask
import com.hp.workpath.sample.statisticsample.task.LastJobSequenceTask
import com.hp.workpath.sample.statisticsample.task.TotalCountTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class CommitDialogFragment : DialogFragment() {

    private lateinit var mLastJobSequenceTextView: TextView
    private lateinit var mTotalTextView: TextView
    private lateinit var mNumberPicker: NumberPicker

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_commit, null)
        findViewElements(view)
        val dialogBuilder = AlertDialog.Builder(requireActivity())
            .setTitle(R.string.commit)
            .setView(view)
            .setPositiveButton(android.R.string.ok, mOKListener)
            .setNegativeButton(android.R.string.cancel, mCancelListener)
            .setCancelable(false)
        return dialogBuilder.create()
    }

    private fun findViewElements(view: View) = runBlocking {
        mLastJobSequenceTextView = view.findViewById(R.id.lastJobSequenceTextView)
        mTotalTextView = view.findViewById(R.id.totalTextView)
        mNumberPicker = view.findViewById(R.id.numberPicker)

        try {
            val lastJobSequenceDeferred = lifecycleScope.async(Dispatchers.Default) {
                context?.let { LastJobSequenceTask(it).execute() }
            }
            val lastJobSequence = lastJobSequenceDeferred.await() ?: 0
            mLastJobSequenceTextView.text = getString(R.string.committed_job_sequence, lastJobSequence)

            val totalCountDeferred = lifecycleScope.async(Dispatchers.Default) {
                context?.let { TotalCountTask(it).execute() }
            }
            val totalCount = totalCountDeferred.await()
            mTotalTextView.text = getString(R.string.total, totalCount)
            mNumberPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

            if (totalCount != null && totalCount > 0) {
                mNumberPicker.minValue = lastJobSequence + 1
                mNumberPicker.maxValue = lastJobSequence + totalCount
            } else {
                mNumberPicker.isEnabled = false
            }
        } catch (t: Throwable) {
            Logger.showResult(activity, "LastJobSequenceTask, TotalCountTask ${t.message}")
        }
    }

    private val mOKListener = DialogInterface.OnClickListener { dialog, _ ->
        try {
            runBlocking {
                val commit = mNumberPicker.value
                val isCommitted = lifecycleScope.async(Dispatchers.Default) {
                    context?.let { CommitTask(it, commit).execute() }
                }
                Logger.showResult(activity, "commit result: ${isCommitted.await()}")
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY, // Same request key StoreJobFragment used to register its listener
                    bundleOf() // The data to be passed to StoreJobFragment
                )
            }
        } catch (t: Throwable) {
            Logger.showResult(activity, "StatisticsService.commit ${t.message}")
        }
        dialog.dismiss()
    }
    private val mCancelListener =
        DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() }

    companion object {
        fun newInstance(): CommitDialogFragment {
            return CommitDialogFragment()
        }
    }
}