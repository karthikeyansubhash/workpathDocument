// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.printinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.hp.workpath.api.statistics.jobinfo.print.PrintAgentInfo
import com.hp.workpath.api.statistics.jobinfo.print.PrintAgentInfo.*
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.view.Utils.setSummary
import com.hp.workpath.sample.statisticsample.view.Utils.setTitle

class PrintAgentInfoView(var inflater: LayoutInflater, var rootView: LinearLayout) {
    private lateinit var layoutAgentId: ViewGroup
    private lateinit var layoutMarkerColor: ViewGroup
    private lateinit var layoutProductNumber: ViewGroup
    private lateinit var layoutSerialNumber: ViewGroup
    private lateinit var layoutConsumableContentType: ViewGroup
    private lateinit var layoutConsumableType: ViewGroup
    private lateinit var layoutDescription: ViewGroup
    private lateinit var layoutAgentUsedUnit: ViewGroup
    private lateinit var layoutAmount: ViewGroup
    private lateinit var layoutAgentCapacityUnit: ViewGroup
    private lateinit var layoutMaxCapacity: ViewGroup
    private lateinit var layoutDate: ViewGroup
    private lateinit var layoutName: ViewGroup

    fun setPrintAgentInfo(printAgentInfos: Array<PrintAgentInfo?>?) {
        rootView.removeAllViews()
        if (printAgentInfos != null) {
            for (index in printAgentInfos.indices) {
                rootView.addView(setPrintAgentInfoInternal(index, printAgentInfos[index]))
            }
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setPrintAgentInfoInternal(index: Int?, printAgentInfo: PrintAgentInfo?): View {
        val view = inflater.inflate(R.layout.layout_print_agent_info, rootView, false)
        initViewPrintAgentInfo(view)
        if (index != null) {
            if (index % 2 == 0) {
                view.setBackgroundColor(
                    ContextCompat.getColor(
                        view.context,
                        R.color.option_background_color
                    )
                )
            }
        }
        if (printAgentInfo != null) {
            setSummary(layoutAgentId, printAgentInfo.agentId)
            setAgentUsed(printAgentInfo.agentUsed)
            setAgentCapacity(printAgentInfo.capacity)
            setAgentManufacturer(printAgentInfo.manufacturer)
            setSummary(layoutDescription, printAgentInfo.description)
            setSummary(layoutMarkerColor, printAgentInfo.markerColor)
            setSummary(layoutProductNumber, printAgentInfo.productNumber)
            setSummary(layoutSerialNumber, printAgentInfo.serialNumber)
            setSummary(layoutConsumableContentType, printAgentInfo.consumableContentType)
            setSummary(layoutConsumableType, printAgentInfo.consumableType)
        }
        return view
    }

    private fun setAgentUsed(agentUsed: AgentUsed?) {
        if (agentUsed != null) {
            setSummary(layoutAgentUsedUnit, agentUsed.unit)
            setSummary(layoutAmount, agentUsed.amount)
        } else {
            val parent = layoutAgentUsedUnit.parent.parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setAgentCapacity(agentCapacity: Capacity?) {
        if (agentCapacity != null) {
            setSummary(layoutAgentCapacityUnit, agentCapacity.unit)
            setSummary(layoutMaxCapacity, agentCapacity.maxCapacity)
        } else {
            val parent = layoutAgentCapacityUnit.parent.parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    fun setAgentManufacturer(agentManufacturer: AgentManufacturer?) {
        if (agentManufacturer != null) {
            setSummary(layoutDate, agentManufacturer.date)
            setSummary(layoutName, agentManufacturer.name)
        } else {
            val parent = layoutDate.parent.parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun initViewPrintAgentInfo(view: View) {
        layoutAgentId = setTitle(view.findViewById(R.id.layoutAgentId), R.string.agentId)
        layoutMarkerColor =
            setTitle(view.findViewById(R.id.layoutMarkerColor), R.string.markerColor)
        layoutProductNumber =
            setTitle(view.findViewById(R.id.layoutProductNumber), R.string.productNumber)
        layoutSerialNumber =
            setTitle(view.findViewById(R.id.layoutSerialNumber), R.string.serialNumber)
        layoutConsumableContentType = setTitle(
            view.findViewById(R.id.layoutConsumableContentType),
            R.string.consumableContentType
        )
        layoutConsumableType =
            setTitle(view.findViewById(R.id.layoutConsumableType), R.string.consumableType)
        layoutDescription =
            setTitle(view.findViewById(R.id.layoutDescription), R.string.description)
        (view.findViewById<View>(R.id.titleAgentUsedTextView) as TextView).setText(R.string.agentUsed)
        (view.findViewById<View>(R.id.titleCapacityTextView) as TextView).setText(R.string.capacity)
        (view.findViewById<View>(R.id.titleManufacturerTextView) as TextView).setText(R.string.manufacturer)

        layoutAgentUsedUnit = setTitle(view.findViewById(R.id.layoutAgentUsedUnit), R.string.unit)
        layoutAmount = setTitle(view.findViewById(R.id.layoutAmount), R.string.amount)
        layoutAgentCapacityUnit =
            setTitle(view.findViewById(R.id.layoutAgentCapacityUnit), R.string.unit)
        layoutMaxCapacity =
            setTitle(view.findViewById(R.id.layoutMaxCapacity), R.string.maxCapacity)
        layoutDate = setTitle(view.findViewById(R.id.layoutDate), R.string.date)
        layoutName = setTitle(view.findViewById(R.id.layoutName), R.string.name)
    }
}