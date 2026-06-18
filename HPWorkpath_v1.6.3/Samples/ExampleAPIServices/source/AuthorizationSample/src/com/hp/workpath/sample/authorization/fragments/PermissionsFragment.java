// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization.fragments;

import static com.hp.workpath.sample.authorization.MainActivity.SCREEN_4_3_INCH;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hp.workpath.api.authorization.Permission;
import com.hp.workpath.api.authorization.PermissionToSignInMethod;
import com.hp.workpath.api.authorization.SignInMethod;
import com.hp.workpath.sample.authorization.DialogType;
import com.hp.workpath.sample.authorization.MainActivity;
import com.hp.workpath.sample.authorization.R;
import com.hp.workpath.sample.authorization.adapter.SelectableAdapter;
import com.hp.workpath.sample.authorization.databinding.DialogPermissionsBinding;
import com.hp.workpath.sample.authorization.exception.ResultException;
import com.hp.workpath.sample.authorization.interfaces.IDialogFragmentListener;
import com.hp.workpath.sample.authorization.task.GetPermissionsTask;
import com.hp.workpath.sample.authorization.task.GetSignInMethodsTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PermissionsFragment extends DialogFragment {
    private static final String TAG = MainActivity.TAG;
    private DialogPermissionsBinding binding;
    private SelectableAdapter<Parcelable> adapter;

    private IDialogFragmentListener listener;
    private DialogType.Data dataType;
    private boolean isVisibleCheckBox = false;

    private ArrayList<Parcelable> selectedItems;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (IDialogFragmentListener) context;
        } catch (ClassCastException e) {
            Toast.makeText(context, context.getClass().getSimpleName()
                    + " must implement IDialogFragmentListener", Toast.LENGTH_SHORT).show();
            Log.e(TAG, context.toString() + " must implement IDialogFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            dataType = (DialogType.Data) args.getSerializable(DialogType.DIALOG_TYPE);
            selectedItems = args.getParcelableArrayList(DialogType.DIALOG_DATA);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        binding = DialogPermissionsBinding.inflate(inflater, null, false);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireActivity(), R.style.DialogTheme);
        dialogBuilder.setTitle(dataType.name());
        dialogBuilder.setView(binding.getRoot());
        dialogBuilder.setCancelable(false);
        if (DialogType.Data.GET_PERMISSIONS.equals(dataType)
                || DialogType.Data.GET_SIGN_IN_METHODS.equals(dataType)) {
            dialogBuilder.setPositiveButton(android.R.string.ok, okListener);
        } else if (DialogType.Data.GUEST_PERMISSION_SET.equals(dataType)) {
            dialogBuilder.setPositiveButton(android.R.string.ok, okListener);
            dialogBuilder.setNegativeButton(android.R.string.cancel, cancelListener);
            isVisibleCheckBox = true;
        }
        return dialogBuilder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            Dialog dialog = getDialog();
            if (dialog != null) {
                Window window = dialog.getWindow();
                if (window != null) {
                    int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.7);
                    if (SCREEN_4_3_INCH.equals(binding.permissionsDialog.getTag())) {
                        width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
                    }
                    int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    window.setLayout(width, height);
                }
            }

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            SelectableAdapter.OnItemClickListener<Parcelable> clickListener = getOnItemClickListener();

            if (DialogType.Data.GET_PERMISSIONS.equals(dataType)
                    || DialogType.Data.GUEST_PERMISSION_SET.equals(dataType)
                    || DialogType.Data.PERMISSION_TO_SIGN_IN_METHOD__PERMISSION_ID.equals(dataType)) {
                Future<ArrayList<Permission>> future = executorService.submit(new GetPermissionsTask(requireContext()));
                ArrayList<Permission> permissions = future.get();
                adapter = new SelectableAdapter<>(new ArrayList<>(permissions), selectedItems, clickListener, isVisibleCheckBox);
            } else if (DialogType.Data.GET_SIGN_IN_METHODS.equals(dataType)
                    || DialogType.Data.DEFAULT_SIGN_IN_METHOD.equals(dataType)
                    || DialogType.Data.PERMISSION_TO_SIGN_IN_METHOD__SIGN_IN_METHOD.equals(dataType)) {
                Future<ArrayList<SignInMethod>> future = executorService.submit(new GetSignInMethodsTask(requireContext(), getString(R.string.en_us)));
                ArrayList<SignInMethod> signInMethods = future.get();
                adapter = new SelectableAdapter<>(new ArrayList<>(signInMethods), selectedItems, clickListener, isVisibleCheckBox);
            }
            binding.permissionList.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.permissionList.setAdapter(adapter);
        } catch (ExecutionException ee) {
            Throwable cause = ee.getCause();
            if (cause instanceof ResultException) {
                dismiss();
                listener.onDialogError(((ResultException) cause).getResult());
            }
            Log.e(TAG, cause != null ? cause.getMessage() : ee.getMessage());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Nullable
    private SelectableAdapter.OnItemClickListener<Parcelable> getOnItemClickListener() {
        SelectableAdapter.OnItemClickListener<Parcelable> clickListener = null;
        if (DialogType.Data.DEFAULT_SIGN_IN_METHOD.equals(dataType)
                || DialogType.Data.PERMISSION_TO_SIGN_IN_METHOD__PERMISSION_ID.equals(dataType)
                || DialogType.Data.PERMISSION_TO_SIGN_IN_METHOD__SIGN_IN_METHOD.equals(dataType)) {
            clickListener = item -> {
                HashMap<String, Object> result = new HashMap<>();
                result.put(DialogType.DIALOG_TYPE, dataType);
                result.put(dataType.name(), item);
                listener.onDialogResult(result);
                dismiss();
            };
        }
        return clickListener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private final DialogInterface.OnClickListener okListener = (dialog, which) -> {
        if (adapter == null) {
            dialog.dismiss();
            return;
        }
        HashMap<String, Object> result = new HashMap<>();
        result.put(DialogType.DIALOG_TYPE, dataType);
        result.put(dataType.name(), adapter.getSelectedItems());
        listener.onDialogResult(result);
        dialog.dismiss();
    };

    private final DialogInterface.OnClickListener cancelListener = (dialog, which) -> dialog.cancel();

    public static <E> void addView(final LinearLayout parent, final E data, final Set<E> dataSet) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.layout_box, parent, false);

        TextView textView = viewGroup.findViewById(R.id.textView);
        if (data instanceof Permission) {
            textView.setText(((Permission) data).getLocalizedName().getValue());
        } else if (data instanceof PermissionToSignInMethod) {
            textView.setText(((PermissionToSignInMethod) data).getPermissionId());
        }

        Button deleteButton = viewGroup.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(view -> {
            dataSet.remove(data);
            parent.removeView(viewGroup);
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 0, 10, 0);
        viewGroup.setLayoutParams(params);
        parent.addView(viewGroup);
    }
}
