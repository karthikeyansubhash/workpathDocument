// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.printinfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hp.workpath.api.statistics.jobinfo.print.PrintAgentInfo;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.view.Utils;

public class PrintAgentInfoView {

    LinearLayout rootView;
    LayoutInflater inflater;

    ViewGroup layoutAgentId;
    ViewGroup layoutMarkerColor;
    ViewGroup layoutProductNumber;
    ViewGroup layoutSerialNumber;
    ViewGroup layoutConsumableContentType;
    ViewGroup layoutConsumableType;
    ViewGroup layoutDescription;
    ViewGroup layoutAgentUsedUnit;
    ViewGroup layoutAmount;
    ViewGroup layoutAgentCapacityUnit;
    ViewGroup layoutMaxCapacity;
    ViewGroup layoutDate;
    ViewGroup layoutName;


    public PrintAgentInfoView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.inflater = inflater;
    }

    public void setPrintAgentInfo(PrintAgentInfo[] printAgentInfos) {
        rootView.removeAllViews();
        if (printAgentInfos != null) {
            for (int index = 0; index < printAgentInfos.length; index++) {
                rootView.addView(setPrintAgentInfoInternal(index, printAgentInfos[index]));
            }
        } else {
            LinearLayout parent = (LinearLayout) ((ViewGroup) rootView).getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private View setPrintAgentInfoInternal(int index, PrintAgentInfo printAgentInfo) {
        View view = inflater.inflate(R.layout.layout_print_agent_info, rootView, false);
        initViewPrintAgentInfo(view);
        if (index % 2 == 0) {
            view.setBackgroundColor(view.getResources().getColor(R.color.option_background_color));
        }
        if (printAgentInfo != null) {
            Utils.setSummary(layoutAgentId, printAgentInfo.getAgentId());
            setAgentUsed(printAgentInfo.getAgentUsed());
            setAgentCapacity(printAgentInfo.getCapacity());
            setAgentManufacturer(printAgentInfo.getManufacturer());
            Utils.setSummary(layoutDescription, printAgentInfo.getDescription());
            Utils.setSummary(layoutMarkerColor, printAgentInfo.getMarkerColor());
            Utils.setSummary(layoutProductNumber, printAgentInfo.getProductNumber());
            Utils.setSummary(layoutSerialNumber, printAgentInfo.getSerialNumber());
            Utils.setSummary(layoutConsumableContentType, printAgentInfo.getConsumableContentType());
            Utils.setSummary(layoutConsumableType, printAgentInfo.getConsumableType());
        }
        return view;
    }

    public void setAgentUsed(PrintAgentInfo.AgentUsed agentUsed) {
        if (agentUsed != null) {
            Utils.setSummary(layoutAgentUsedUnit, agentUsed.getUnit());
            Utils.setSummary(layoutAmount, agentUsed.getAmount());
        } else {
            LinearLayout parent = (LinearLayout) layoutAgentUsedUnit.getParent().getParent();
            parent.setVisibility(View.GONE);
        }
    }

    public void setAgentCapacity(PrintAgentInfo.Capacity agentCapacity) {
        if (agentCapacity != null) {
            Utils.setSummary(layoutAgentCapacityUnit, agentCapacity.getUnit());
            Utils.setSummary(layoutMaxCapacity, agentCapacity.getMaxCapacity());
        } else {
            LinearLayout parent = (LinearLayout) layoutAgentCapacityUnit.getParent().getParent();
            parent.setVisibility(View.GONE);
        }
    }

    public void setAgentManufacturer(PrintAgentInfo.AgentManufacturer agentManufacturer) {
        if (agentManufacturer != null) {
            Utils.setSummary(layoutDate, agentManufacturer.getDate());
            Utils.setSummary(layoutName, agentManufacturer.getName());
        } else {
            LinearLayout parent = (LinearLayout) layoutDate.getParent().getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private void initViewPrintAgentInfo(View view) {
        layoutAgentId = Utils.setTitle(view.findViewById(R.id.layoutAgentId), R.string.agentId);
        layoutMarkerColor = Utils.setTitle(view.findViewById(R.id.layoutMarkerColor), R.string.markerColor);
        layoutProductNumber = Utils.setTitle(view.findViewById(R.id.layoutProductNumber), R.string.productNumber);
        layoutSerialNumber = Utils.setTitle(view.findViewById(R.id.layoutSerialNumber), R.string.serialNumber);
        layoutConsumableContentType = Utils.setTitle(view.findViewById(R.id.layoutConsumableContentType), R.string.consumableContentType);
        layoutConsumableType = Utils.setTitle(view.findViewById(R.id.layoutConsumableType), R.string.consumableType);
        layoutDescription  = Utils.setTitle(view.findViewById(R.id.layoutDescription), R.string.description);
        ((TextView) view.findViewById(R.id.titleAgentUsedTextView)).setText(R.string.agentUsed);
        ((TextView) view.findViewById(R.id.titleCapacityTextView)).setText(R.string.capacity);
        ((TextView) view.findViewById(R.id.titleManufacturerTextView)).setText(R.string.manufacturer);

        layoutAgentUsedUnit = Utils.setTitle(view.findViewById(R.id.layoutAgentUsedUnit), R.string.unit);
        layoutAmount = Utils.setTitle(view.findViewById(R.id.layoutAmount), R.string.amount);
        layoutAgentCapacityUnit = Utils.setTitle(view.findViewById(R.id.layoutAgentCapacityUnit), R.string.unit);
        layoutMaxCapacity = Utils.setTitle(view.findViewById(R.id.layoutMaxCapacity), R.string.maxCapacity);
        layoutDate = Utils.setTitle(view.findViewById(R.id.layoutDate), R.string.date);
        layoutName = Utils.setTitle(view.findViewById(R.id.layoutName), R.string.name);
    }

}
