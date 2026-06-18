// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.suppliessample.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hp.workpath.api.Result;
import com.hp.workpath.api.supplies.supplyinfo.Supply;
import com.hp.workpath.sample.suppliessample.Logger;
import com.hp.workpath.sample.suppliessample.R;
import com.hp.workpath.sample.suppliessample.SupplyActivity;
import com.hp.workpath.sample.suppliessample.task.SuppliesTask;

import java.util.ArrayList;
import java.util.List;

public class SupplyFragment extends Fragment {

    TextView mSuppliesRawDataTextView;
    TextView mTitleTextView;
    ProgressBar mProgressBar;
    RecyclerView supplyRecycler;

    int mIndex = 0;

    ArrayList supplylist = new ArrayList();
    ArrayList supplyName = new ArrayList();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supplies, container, false);
        supplyRecycler = view.findViewById(R.id.supply_recycler);
        mSuppliesRawDataTextView = view.findViewById(R.id.suppliesRawDataTextView);
        mSuppliesRawDataTextView.setMovementMethod(new ScrollingMovementMethod());
        mTitleTextView = view.findViewById(R.id.titleTextView);
        mProgressBar = view.findViewById(R.id.progressBar);

        mIndex = getArguments().getInt(SupplyActivity.INDEX);
        getSupplies();
        return view;
    }

    private void setRawData(Supply supply) {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        mSuppliesRawDataTextView.setText(gson.toJson(supply));
    }

    public void getSupplies() {
        mProgressBar.setVisibility(View.VISIBLE);
        new SuppliesTask(requireContext(), supplyInterface).taskExecute();
    }

    private void setSupplies(Supply supply) {
        mTitleTextView.setText(getString(R.string.supplyDetails));
        setRawData(supply);
        setSupply(supply);
    }

    ResponseInterface supplyInterface = new ResponseInterface() {
        @Override
        public void success(List<Supply> supplies) {
            mProgressBar.setVisibility(View.GONE);
            if (supplies != null && supplies.size() > 0) {
                setSupplies(supplies.get(mIndex - 1));
            } else {
                mSuppliesRawDataTextView.setText("There is no supply information");
            }
        }

        @Override
        public void failure(String msg, Result result) {
            mProgressBar.setVisibility(View.GONE);
            Logger.showResult(getActivity(), msg, result);
        }
    };

    private void setSupply(Supply supply) {
        supplylist.add(supply.getConsumableTypeEnum());
        supplylist.add(supply.getDescription());
        supplylist.add(supply.getMarkerColor());
        supplylist.add(supply.getApproxPercentRemaining());
        supplylist.add(supply.getMakeAndModel());
        supplylist.add(supply.getProductNumber());
        supplylist.add(supply.getSerialNumber());
        supplylist.add(supply.getCapacity().getMaxCapacity());
        supplylist.add(supply.getCapacity().getUnit());

        supplyName.add(getString(R.string.consumableTypeEnum));
        supplyName.add(getString(R.string.description));
        supplyName.add(getString(R.string.markerColor));
        supplyName.add(getString(R.string.approxPercentRemaining));
        supplyName.add(getString(R.string.makeAndModel));
        supplyName.add(getString(R.string.productNumber));
        supplyName.add(getString(R.string.serialNumber));
        supplyName.add(getString(R.string.maxCapacity));
        supplyName.add(getString(R.string.unit));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        supplyRecycler.setLayoutManager(layoutManager);
        supplyRecycler.setAdapter(new SupplyRecyclerAdapter(getActivity(), supplylist, supplyName));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
        );
        supplyRecycler.addItemDecoration(dividerItemDecoration);
    }
}

class SupplyRecyclerAdapter extends RecyclerView.Adapter<SupplyRecyclerAdapter.ViewHolder> {
    Activity activity = new FragmentActivity();
    ArrayList supplylist;
    ArrayList supplyName;

    public SupplyRecyclerAdapter(FragmentActivity activity, ArrayList supplylist, ArrayList supplyName) {
        this.activity = activity;
        this.supplylist = supplylist;
        this.supplyName = supplyName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_supply_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.supplyNameDetail.setText(supplyName.get(position).toString());
        if (supplylist.get(position)!=null) {
            holder.supplyValueDetail.setText(supplylist.get(position).toString());
        }
    }

    @Override
    public int getItemCount() {
        return supplylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView supplyNameDetail;
        public TextView supplyValueDetail;

        ViewHolder(View view) {
            super(view);
            supplyNameDetail = view.findViewById(R.id.supply_name);
            supplyValueDetail = view.findViewById(R.id.supply_value);
        }
    }
}



