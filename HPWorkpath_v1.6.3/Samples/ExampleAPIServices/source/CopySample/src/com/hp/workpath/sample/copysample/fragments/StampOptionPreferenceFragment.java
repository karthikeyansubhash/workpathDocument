// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.hp.workpath.api.copier.CopyAttributes;
import com.hp.workpath.api.copier.StampFormat;
import com.hp.workpath.api.copier.StampOption;
import com.hp.workpath.api.copier.StampPolicyType;
import com.hp.workpath.api.copier.StampType;
import com.hp.workpath.sample.copysample.MainActivity;
import com.hp.workpath.sample.copysample.R;

public class StampOptionPreferenceFragment extends PreferenceDialogFragmentCompat {

    private Spinner mStampPositionSpinner;
    private Spinner mStampTypeSpinner;
    private Spinner mStampPolicyTypeSpinner;
    private EditText mStampTextEditText;
    private Spinner mStampFormatFontSpinner;;
    private Spinner mStampFormatTextSizeSpinner;
    private Spinner mStampFormatTextColorSpinner;
    private Spinner mStampFormatWhiteBackgroundSpinner;
    private EditText mStampFormatStatingPageEditText;

    public static StampOptionPreferenceFragment newInstance(String key) {
        final StampOptionPreferenceFragment fragment = new StampOptionPreferenceFragment();
        final Bundle bundle = new Bundle();
        bundle.putString(ARG_KEY, key);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onBindDialogView(@NonNull View view) {
        super.onBindDialogView(view);
        try{

            mStampPositionSpinner = view.findViewById(R.id.stampPositionSpinner);
            mStampTypeSpinner = view.findViewById(R.id.stampTypeSpinner);
            mStampPolicyTypeSpinner = view.findViewById(R.id.stampPolicyTypeSpinner);
            mStampTextEditText = view.findViewById(R.id.stampTextEditText);
            mStampFormatFontSpinner = view.findViewById(R.id.stampFormatFontSpinner);
            mStampFormatTextSizeSpinner = view.findViewById(R.id.stampFormatTextSizeSpinner);
            mStampFormatTextColorSpinner = view.findViewById(R.id.stampFormatTextColorSpinner);
            mStampFormatWhiteBackgroundSpinner = view.findViewById(R.id.stampFormatWhiteBackgroundSpinner);
            mStampFormatStatingPageEditText = view.findViewById(R.id.stampFormatStartingPageEditText);

            MainActivity mainActivity = (MainActivity)getActivity();
            if(mainActivity.getCapabilities() != null){
                setStampCapabilities();
            }
        }catch(Exception e){

        }

    }

    private void setStampCapabilities(){
        ArrayAdapter stampPositionAdapter = new ArrayAdapter(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, getStampPreference().getmStampPositionList());
        mStampPositionSpinner.setAdapter(stampPositionAdapter);

        mStampPositionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CopyAttributes.StampPosition stampPosition = CopyAttributes.StampPosition.valueOf(mStampPositionSpinner.getItemAtPosition(position).toString());
                if(stampPosition != null){
                    ArrayAdapter stampTypeAdapter = new ArrayAdapter(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, getStampPreference().getmStampTypeListMap().get(stampPosition));
                    ArrayAdapter stampPolicyTypeAdapter = new ArrayAdapter(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, getStampPreference().getmStampPolicyTypeListMap().get(stampPosition));
                    ArrayAdapter stampFontAdapter = new ArrayAdapter(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, getStampPreference().getmStampFontListMap().get(stampPosition));
                    ArrayAdapter stampTextSizeAdapter = new ArrayAdapter(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, getStampPreference().getmStampTextSizeListMap().get(stampPosition));
                    ArrayAdapter stampTextColorAdapter = new ArrayAdapter(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, getStampPreference().getmStampTextColorListMap().get(stampPosition));
                    ArrayAdapter stampWhiteBackgroundAdapter = new ArrayAdapter(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, getStampPreference().getmStampWhiteBackGroundListMap().get(stampPosition));
                    mStampTypeSpinner.setAdapter(stampTypeAdapter);
                    mStampPolicyTypeSpinner.setAdapter(stampPolicyTypeAdapter);
                    mStampFormatFontSpinner.setAdapter(stampFontAdapter);
                    mStampFormatTextSizeSpinner.setAdapter(stampTextSizeAdapter);
                    mStampFormatTextColorSpinner.setAdapter(stampTextColorAdapter);
                    mStampFormatWhiteBackgroundSpinner.setAdapter(stampWhiteBackgroundAdapter);

                    StampOption stampOption = getStampPreference().getmStampOptionMap().get(stampPosition);
                    if(stampOption != null){
                        mStampTypeSpinner.setSelection(stampTypeAdapter.getPosition(stampOption.type));
                        mStampPolicyTypeSpinner.setSelection(stampPolicyTypeAdapter.getPosition((stampOption.policyType)));
                        mStampFormatFontSpinner.setSelection(stampFontAdapter.getPosition(stampOption.format.font));
                        mStampFormatTextSizeSpinner.setSelection(stampTextSizeAdapter.getPosition(stampOption.format.textSize));
                        mStampFormatTextColorSpinner.setSelection(stampTextColorAdapter.getPosition(stampOption.format.textColor));
                        mStampFormatWhiteBackgroundSpinner.setSelection(stampWhiteBackgroundAdapter.getPosition(stampOption.format.whiteBackground));
                        mStampFormatStatingPageEditText.setText(String.valueOf(stampOption.format.startingPage));
                        mStampTextEditText.setText(stampOption.text);

                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
    }


    // get the NumberPickerPreference instance
    private StampOptionDialogPreference getStampPreference() {
        return (StampOptionDialogPreference) getPreference();
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            if(mStampPositionSpinner.getSelectedItem() == null) {
                return;
            }
            CopyAttributes.StampPosition stampPosition = CopyAttributes.StampPosition.valueOf(String.valueOf(mStampPositionSpinner.getSelectedItem()));

            String font = String.valueOf(mStampFormatFontSpinner.getSelectedItem());
            int textSize = Integer.parseInt(String.valueOf(mStampFormatTextSizeSpinner.getSelectedItem()));
            String textColor = String.valueOf(mStampFormatTextColorSpinner.getSelectedItem());
            boolean whiteBackground = Boolean.parseBoolean(String.valueOf(mStampFormatWhiteBackgroundSpinner.getSelectedItem()));
            int startingPage = 1;
            try{
                startingPage = Integer.parseInt(String.valueOf(mStampFormatStatingPageEditText.getText()));
            }catch(Exception e){}

            StampType stampType = StampType.valueOf(String.valueOf(mStampTypeSpinner.getSelectedItem()));
            StampPolicyType stampPolicyType = StampPolicyType.NONE;
            try{
                stampPolicyType = StampPolicyType.valueOf(String.valueOf(mStampPolicyTypeSpinner.getSelectedItem()));
            }catch (Exception e){}

            String text = String.valueOf(mStampTextEditText.getText());

            StampFormat stampFormat = new StampFormat(font,textSize,textColor,whiteBackground,startingPage);

            StampOption stampOption = new StampOption(stampFormat,stampPolicyType, text, stampType);
            getStampPreference().getmStampOptionMap().put(stampPosition, stampOption);

            getStampPreference().saveStampOptionMap();
        }
    }

}