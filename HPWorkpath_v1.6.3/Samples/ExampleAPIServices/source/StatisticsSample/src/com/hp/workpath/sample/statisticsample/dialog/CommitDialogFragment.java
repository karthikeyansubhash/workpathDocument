// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.hp.workpath.sample.statisticsample.Logger;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.task.CommitTask;
import com.hp.workpath.sample.statisticsample.task.LastJobSequenceTask;

public class CommitDialogFragment extends DialogFragment implements LastJobSequenceTask.LastJobSequenceTaskCompletionListener,CommitTask.CommitTaskListener {

    private static int totalTaskCount;
    private TextView mLastJobSequenceTextView;
    private TextView mTotalTextView;
    private NumberPicker mNumberPicker;

    public static CommitDialogFragment newInstance(int totalTaskCount) {
        CommitDialogFragment.totalTaskCount = totalTaskCount;
        return new CommitDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_commit, null);
        findViewElements(view);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.commit)
                .setView(view)
                .setPositiveButton(android.R.string.ok, mOKListener)
                .setNegativeButton(android.R.string.cancel, mCancelListener)
                .setCancelable(false);
        return dialogBuilder.create();
    }

    private void findViewElements(View view) {
        mLastJobSequenceTextView = view.findViewById(R.id.lastJobSequenceTextView);
        mTotalTextView = view.findViewById(R.id.totalTextView);
        mNumberPicker = view.findViewById(R.id.numberPicker);
        new LastJobSequenceTask(getContext(), this).taskExecute();

    }

    private final DialogInterface.OnClickListener mOKListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            try {
                int commit = mNumberPicker.getValue();
                new CommitTask(getContext(), commit,CommitDialogFragment.this).taskExecute();
            } catch (Throwable t) {
                Logger.showResult(getActivity(), "StatisticsService.commit " + t.getMessage());
            }
            dialog.dismiss();
        }
    };

    private final DialogInterface.OnClickListener mCancelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    };

    @Override
    public void onLastJobSequenceCompleted(int lastJobSequence) {
        try {
            mLastJobSequenceTextView.setText(getString(R.string.committed_job_sequence, lastJobSequence));
            mTotalTextView.setText(getString(R.string.total, totalTaskCount));
            mNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);


            if (totalTaskCount > 0) {
                mNumberPicker.setMinValue(lastJobSequence + 1);
                mNumberPicker.setMaxValue(lastJobSequence + totalTaskCount);
            } else {
                mNumberPicker.setEnabled(false);
            }
        } catch (Throwable t) {
            Logger.showResult(getActivity(), "LastJobSequenceTask, TotalCountTask " + t.getMessage());
        }
    }

    @Override
    public void onCommitTask(Boolean isCommitted) {
        Logger.showResult(getActivity(), "commit result: " + isCommitted);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
    }
}
