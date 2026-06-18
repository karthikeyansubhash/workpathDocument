package com.hp.workpath.sample.printsample.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.DialogFragment;

import com.hp.workpath.api.printer.PrintAttributes;
import com.hp.workpath.api.printer.PrintAttributesReader;
import com.hp.workpath.sample.printsample.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckboxListDialogFragment extends DialogFragment {
    private TextView emptyTextView;
    private LinearLayoutCompat checkboxContainer;
    private BatchJobListInterface mListener;

    private ArrayList<PrintAttributes> printAttributes = new ArrayList();
    private Map<Integer, Boolean> mPrintObjectdMap = new HashMap();
    private static String PRINT_ATTRIBUTE_LIST = "print_attributes";
    private View dialogView;

    public interface BatchJobListInterface {
        void onRemovePrintAttributeFromList(Map<Integer, Boolean> mPrintObjectdMap);

        void showJobSelectionError();
    }

    public static CheckboxListDialogFragment newInstance(ArrayList<PrintAttributes> accessoryInfoList) {
        CheckboxListDialogFragment f = new CheckboxListDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(PRINT_ATTRIBUTE_LIST, accessoryInfoList);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (BatchJobListInterface) getActivity();
        } catch (ClassCastException e) {
            Toast.makeText(getActivity(), getActivity().getClass().getName()
                    + " must implement BatchJobListInterface", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_NEGATIVE);
            positiveButton.setOnClickListener(view -> {
                boolean atleastOneSelected = false;

                for (Map.Entry<Integer, Boolean> entry : mPrintObjectdMap.entrySet()) {
                    Boolean value = entry.getValue();
                    if (value) {
                        atleastOneSelected = true;
                        break;
                    }
                }

                if (atleastOneSelected) {
                    mListener.onRemovePrintAttributeFromList(mPrintObjectdMap);
                    dismiss();
                } else {
                    mListener.showJobSelectionError();
                }
            });
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_checkbox_list, null);
        emptyTextView = dialogView.findViewById(R.id.emptyTextView);
        checkboxContainer = dialogView.findViewById(R.id.checkbox_container);
        printAttributes = requireArguments().getParcelableArrayList(PRINT_ATTRIBUTE_LIST);

        for (int index = 0; index < printAttributes.size(); index++) {
            mPrintObjectdMap.put(index, false);
        }
        String title = requireActivity().getString(R.string.batch_job_list);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setView(dialogView)
                .setPositiveButton(requireActivity().getString(R.string.done), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .setNegativeButton(requireActivity().getString(R.string.remove), null)
                .setCancelable(false);

        return dialogBuilder.create();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewElements();
    }

    private void findViewElements() {
        for (int i = 0; i < printAttributes.size(); i++) {
            int index = i;
            AppCompatCheckBox checkbox = new AppCompatCheckBox(requireContext());
            LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 5, 0, 5);
            checkbox.setLayoutParams(params);
            checkbox.setText(new PrintAttributesReader(printAttributes.get(i)).getUri().getLastPathSegment());
            checkbox.setTag(index);
            checkbox.setOnCheckedChangeListener((compoundButton, isChecked) -> mPrintObjectdMap.put(index, isChecked));
            checkboxContainer.addView(checkbox);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return dialogView;
    }
}
