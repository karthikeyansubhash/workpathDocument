// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.suppliessample.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.supplies.supplyinfo.Supply;
import com.hp.workpath.sample.suppliessample.Logger;
import com.hp.workpath.sample.suppliessample.R;
import com.hp.workpath.sample.suppliessample.SupplyActivity;
import com.hp.workpath.sample.suppliessample.task.SuppliesTask;
import com.hp.workpath.sample.suppliessample.view.SupplyListAdapter;

import java.util.List;

public class SupplyListFragment extends Fragment implements View.OnClickListener {

    private TextView mTotalTextView;
    private TextView mNoSupplyTextView;
    private Button mGetSupplyListButton;
    private RecyclerView mListView;
    private SupplyListAdapter mListAdapter;
    private ProgressBar mProgressBar;

    List<Supply> mSupplies;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supply_list, container, false);
        mTotalTextView = view.findViewById(R.id.totalTextView);
        mNoSupplyTextView = view.findViewById(R.id.noSupplyTextView);
        mGetSupplyListButton = view.findViewById(R.id.getSupplyListButton);
        mGetSupplyListButton.setOnClickListener(this);
        mListView = view.findViewById(R.id.supplyListView);
        mListAdapter = new SupplyListAdapter(listOnClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mListView.setLayoutManager(layoutManager);
        mListView.setAdapter(mListAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL);
        mListView.addItemDecoration(dividerItemDecoration);
        mProgressBar = view.findViewById(R.id.progressBar);
        getSupplyList();
        return view;
    }

    private void getSupplyList() {
        mNoSupplyTextView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mListAdapter.clear();
        new SuppliesTask(requireContext(), supplyInterface).taskExecute();
    }

    View.OnClickListener listOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int itemPosition = mListView.getChildLayoutPosition(v);
            startSupplyActivity(itemPosition + 1);
        }
    };

    private void startSupplyActivity(int index) {
        if (index > 0 && index <= mSupplies.size()) {
            Intent intent = new Intent(getContext(), SupplyActivity.class);
            intent.putExtra(SupplyActivity.INDEX, index);
            startActivity(intent);
        } else {
            if (getContext() != null) {
                Logger.showResult(getActivity(), getString(R.string.over_range));
            }
        }
    }

    ResponseInterface supplyInterface = new ResponseInterface() {
        @Override
        public void success(List<Supply> supplies) {
            mProgressBar.setVisibility(View.GONE);
            mSupplies = supplies;

            if (mSupplies != null && mSupplies.size() > 0) {
                mTotalTextView.setText(String.valueOf(mSupplies.size()));
                mListAdapter.setItem(mSupplies);
            } else {
                mNoSupplyTextView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void failure(String msg, Result result) {
            mProgressBar.setVisibility(View.GONE);
            Logger.showResult(getActivity(), msg, result);
        }
    };

    @Override
    public void onClick(View v) {
        if (v == mGetSupplyListButton) {
            getSupplyList();
        }
    }
}
