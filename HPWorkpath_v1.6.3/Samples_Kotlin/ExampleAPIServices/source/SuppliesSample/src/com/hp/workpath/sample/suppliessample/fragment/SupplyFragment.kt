// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.suppliessample.fragment

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.hp.workpath.api.Result
import com.hp.workpath.api.supplies.supplyinfo.Supply
import com.hp.workpath.sample.suppliessample.Logger
import com.hp.workpath.sample.suppliessample.R
import com.hp.workpath.sample.suppliessample.SupplyActivity
import com.hp.workpath.sample.suppliessample.databinding.FragmentSuppliesBinding
import com.hp.workpath.sample.suppliessample.task.SuppliesTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SupplyFragment : Fragment() {

    private var mBindingFragment: FragmentSuppliesBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val mBindingFragmentSupplies get() = mBindingFragment!!
    val supplylist = arrayListOf<String>()
    val supplyName = arrayListOf<String>()
    var mIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBindingFragment = FragmentSuppliesBinding.inflate(inflater, container, false)
        val view = mBindingFragmentSupplies.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBindingFragmentSupplies.suppliesRawDataTextView.movementMethod = ScrollingMovementMethod()
        mIndex = arguments?.getInt(SupplyActivity.INDEX) ?: 0
        getSupplies()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBindingFragment = null
    }

    private fun setRawData(supply: Supply) {
        val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
        mBindingFragmentSupplies.suppliesRawDataTextView.text = gson.toJson(supply)
    }

    fun getSupplies() {
        mBindingFragmentSupplies.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.Default) {
            SuppliesTask(requireContext(), supplyInterface).execute()
        }
    }

    private fun setSupplies(supply: Supply) {
        mBindingFragmentSupplies.titleTextView.text = getString(R.string.supplyDetails)
        setRawData(supply)
        supplylist.add(supply.consumableTypeEnum)
        supplylist.add(supply.description)
        supplylist.add(supply.markerColor)
        supplylist.add(supply.approxPercentRemaining)
        supplylist.add(supply.makeAndModel)
        supplylist.add(supply.productNumber)
        supplylist.add(supply.serialNumber)
        supplylist.add(supply.capacity.maxCapacity.toString())
        supplylist.add(supply.capacity.unit)

        supplyName.add(getString(R.string.consumableTypeEnum))
        supplyName.add(getString(R.string.description))
        supplyName.add(getString(R.string.markerColor))
        supplyName.add(getString(R.string.approxPercentRemaining))
        supplyName.add(getString(R.string.makeAndModel))
        supplyName.add(getString(R.string.productNumber))
        supplyName.add(getString(R.string.serialNumber))
        supplyName.add(getString(R.string.maxCapacity))
        supplyName.add(getString(R.string.unit))

        val layoutManager = LinearLayoutManager(context)
        mBindingFragmentSupplies.supplyRecycler.layoutManager = layoutManager
        mBindingFragmentSupplies.supplyRecycler.adapter =
            SupplyRecyclerAdapter(activity, supplylist, supplyName)
        val dividerItemDecoration = DividerItemDecoration(
            context,
            DividerItemDecoration.VERTICAL
        )
        mBindingFragmentSupplies.supplyRecycler.addItemDecoration(dividerItemDecoration)


    }

    private var supplyInterface: ResponseInterface = object : ResponseInterface {
        override fun success(supplies: List<Supply>?) {
            mBindingFragmentSupplies.progressBar.visibility = View.GONE
            if (supplies != null && supplies.isNotEmpty()) {
                setSupplies(supplies[mIndex - 1])
            } else {
                mBindingFragmentSupplies.suppliesRawDataTextView.text =
                    "There is no supply information"
            }
        }

        override fun failed(msg: String?, result: Result?) {
            mBindingFragmentSupplies.progressBar.visibility = View.GONE
            Logger.showResult(activity, msg, result)
        }

    }

}

class SupplyRecyclerAdapter(
    val activity: FragmentActivity?,
    val supplylist: ArrayList<String>,
    val supplyName: ArrayList<String>
) : RecyclerView.Adapter<SupplyRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SupplyRecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_supply_details, parent, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: SupplyRecyclerAdapter.ViewHolder, position: Int) {
        holder.supplyNameDetail.text = supplyName[position]
        holder.supplyValueDetail.text = supplylist[position]
    }


    override fun getItemCount(): Int {
        return supplylist.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var supplyNameDetail: TextView = view.findViewById(R.id.supply_name)
        var supplyValueDetail: TextView = view.findViewById(R.id.supply_value)
    }

}
