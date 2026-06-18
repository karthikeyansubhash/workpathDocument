// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.hp.workpath.api.copier.StoredJobInfo;
import com.hp.workpath.sample.copysample.R;

public class DetailDialogFragment extends DialogFragment {

    public static final String STORED_JOB_INFO = "storedJobInfo";

    private StoredJobInfo storedJobInfo;

    public static DetailDialogFragment newInstance(StoredJobInfo storedJobInfo) {
        DetailDialogFragment f = new DetailDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(STORED_JOB_INFO, storedJobInfo);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_detail, null);
        storedJobInfo = getArguments().getParcelable(STORED_JOB_INFO);
        findViewElements(view);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.stored_job)
                .setView(view)
                .setNegativeButton(android.R.string.cancel, mCancelListener)
                .setCancelable(false);
        return dialogBuilder.create();
    }

    private void findViewElements(View view) {
        if (storedJobInfo != null) {
            setChildView(view.findViewById(R.id.layoutStoreJobId), R.string.stored_job_id, storedJobInfo.getStoredJobId());
            setChildView(view.findViewById(R.id.layoutStoredJobFolderName), R.string.stored_job_folder_name, storedJobInfo.getStoredJobFolderName());
            setChildView(view.findViewById(R.id.layoutStoredJobName), R.string.stored_job_name, storedJobInfo.getStoredJobName());
            setChildView(view.findViewById(R.id.layoutStoredJobUserName), R.string.stored_job_username, storedJobInfo.getStoredJobUserName());
            if (storedJobInfo.getStoredJobPasswordType() != null) {
                setChildView(view.findViewById(R.id.layoutStoredJobPasswordType), R.string.stored_job_password_type, storedJobInfo.getStoredJobPasswordType().name());
            }
            setChildView(view.findViewById(R.id.layoutStoreJobTimestamp), R.string.stored_job_timestamp, storedJobInfo.getStoreJobTimestamp());
            setChildView(view.findViewById(R.id.layoutCopies), R.string.copies, String.valueOf(storedJobInfo.getCopies()));
            if (storedJobInfo.getColorMode() != null) {
                setChildView(view.findViewById(R.id.layoutColorMode), R.string.color_mode, storedJobInfo.getColorMode().name());
            }
            if (storedJobInfo.getOriginalMediaSize() != null) {
                setChildView(view.findViewById(R.id.layoutOriginalMediaSize), R.string.original_media_size, storedJobInfo.getOriginalMediaSize().name());
            }
            if (storedJobInfo.getOutputSides() != null) {
                setChildView(view.findViewById(R.id.layoutOutputSides), R.string.output_sides, storedJobInfo.getOutputSides().name());
            }
            setChildView(view.findViewById(R.id.layoutTotalPages), R.string.total_pages, String.valueOf(storedJobInfo.getTotalPages()));
        }
    }

    private DialogInterface.OnClickListener mCancelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    };

    private void setChildView(ViewGroup viewGroup, int id, String text) {
        ((TextView) viewGroup.findViewById(R.id.titleTextView)).setText(id);
        ((TextView) viewGroup.findViewById(R.id.summaryTextView)).setText(text);
    }
}
