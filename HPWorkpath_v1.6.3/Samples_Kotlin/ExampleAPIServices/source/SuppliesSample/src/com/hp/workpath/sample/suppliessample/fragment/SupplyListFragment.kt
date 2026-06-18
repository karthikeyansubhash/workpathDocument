// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.suppliessample.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hp.workpath.api.Result
import com.hp.workpath.api.supplies.supplyinfo.Supply
import com.hp.workpath.sample.suppliessample.Logger
import com.hp.workpath.sample.suppliessample.R
import com.hp.workpath.sample.suppliessample.SupplyActivity
import com.hp.workpath.sample.suppliessample.databinding.FragmentSupplyListBinding
import com.hp.workpath.sample.suppliessample.task.SuppliesTask
import com.hp.workpath.sample.suppliessample.view.SupplyListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SupplyListFragment : Fragment(), View.OnClickListener {

    private lateinit var mListAdapter: SupplyListAdapter
    lateinit var mSupplies: List<Supply>
    private var mBindingFragment: FragmentSupplyListBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val mBindingFragmentSupplyList get() = mBindingFragment!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBindingFragment = FragmentSupplyListBinding.inflate(inflater, container, false)
        val view = mBindingFragmentSupplyList.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBindingFragmentSupplyList.getSupplyListButton.setOnClickListener(this)
        mListAdapter = SupplyListAdapter(listOnClickListener)
        val layoutManager = LinearLayoutManager(context)
        mBindingFragmentSupplyList.supplyListView.layoutManager = layoutManager
        mBindingFragmentSupplyList.supplyListView.adapter = mListAdapter
        val dividerItemDecoration = DividerItemDecoration(
            context,
            DividerItemDecoration.VERTICAL
        )
        mBindingFragmentSupplyList.supplyListView.addItemDecoration(dividerItemDecoration)
        getSupplyList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBindingFragment = null
    }

    private fun getSupplyList() {
        mBindingFragmentSupplyList.noSupplyTextView.visibility = View.GONE
        mBindingFragmentSupplyList.progressBar.visibility = View.VISIBLE
        mListAdapter.clear()
        lifecycleScope.launch(Dispatchers.Default) {
            SuppliesTask(requireContext(), supplyInterface).execute()
        }
    }

    private var listOnClickListener = View.OnClickListener { v ->
        val itemPosition = mBindingFragmentSupplyList.supplyListView.getChildLayoutPosition(v)
        startSupplyActivity(itemPosition + 1)
    }

    private fun startSupplyActivity(index: Int) {
        if (index > 0 && index <= mSupplies.size) {
            val intent = Intent(context, SupplyActivity::class.java)
            intent.putExtra(SupplyActivity.INDEX, index)
            startActivity(intent)
        } else {
            Logger.showResult(requireActivity(), getString(R.string.over_range))
        }
    }

    private var supplyInterface: ResponseInterface = object : ResponseInterface {
        override fun success(supplies: List<Supply>?) {
            mBindingFragmentSupplyList.progressBar.visibility = View.GONE
            mSupplies = supplies as List<Supply>
            if (mSupplies.isNotEmpty()) {
                mBindingFragmentSupplyList.totalTextView.text = mSupplies.size.toString()
                mListAdapter.setItem(mSupplies)
            } else {
                mBindingFragmentSupplyList.noSupplyTextView.visibility = View.VISIBLE
            }
        }

        override fun failed(msg: String?, result: Result?) {
            mBindingFragmentSupplyList.progressBar.visibility = View.GONE
            Logger.showResult(activity, msg, result)
        }
    }

    override fun onClick(v: View) {
        if (v === mBindingFragmentSupplyList.getSupplyListButton) {
            getSupplyList()
        }
    }
}