// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.suppliessample.view;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hp.workpath.api.supplies.supplyinfo.Supply;
import com.hp.workpath.sample.suppliessample.R;

import java.util.ArrayList;
import java.util.List;

public class SupplyListAdapter extends RecyclerView.Adapter<SupplyListAdapter.ViewHolder> {

    List<Supply> mSupplyList = new ArrayList<>();
    View.OnClickListener mListOnClickListener;

    public SupplyListAdapter(View.OnClickListener listener) {
        mListOnClickListener = listener;
    }

    public Supply getItem(int position) {
        return mSupplyList.get(position);
    }

    public void setItem(List<Supply> supplyList) {
        mSupplyList = supplyList;
        notifyDataSetChanged();
    }

    public void clear() {
        int size = mSupplyList.size();
        if (size > 0) {
            mSupplyList.clear();
            notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mSupplyList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_supply_list, parent, false);
        view.setOnClickListener(mListOnClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Supply supplyData = getItem(position);
        holder.sequenceTextView.setText(String.valueOf(position + 1));
        holder.consumableTypeTextView.setText(supplyData.getConsumableTypeEnum());
        holder.approxPercentTextView.setText(supplyData.getApproxPercentRemaining());
        holder.productNumberTextView.setText(supplyData.getProductNumber());
        holder.descriptionTextView.setText(supplyData.getDescription());
        setDescriptionBackground(holder.descriptionTextView, supplyData.getDescription());
    }

    private void setDescriptionBackground(TextView textView, String description) {
        if (!TextUtils.isEmpty(description)) {
            if (description.contains("Yellow")) {
                textView.setBackgroundResource(R.color.yellow);
            } else if (description.contains("Magenta")) {
                textView.setBackgroundResource(R.color.magenta);
            } else if (description.contains("Cyan")) {
                textView.setBackgroundResource(R.color.cyan);
            } else if (description.contains("Black")) {
                textView.setBackgroundResource(R.color.black);
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView sequenceTextView;
        public TextView consumableTypeTextView;
        public TextView consumableStateTextView;
        public TextView approxPercentTextView;
        public TextView productNumberTextView;
        public TextView descriptionTextView;

        ViewHolder(View view) {
            super(view);
            sequenceTextView = view.findViewById(R.id.sequenceTextView);
            consumableTypeTextView = view.findViewById(R.id.consumableTypeTextView);
            approxPercentTextView = view.findViewById(R.id.approxPercentTextView);
            productNumberTextView = view.findViewById(R.id.productNumberTextView);
            descriptionTextView = view.findViewById(R.id.descriptionTextView);
        }
    }
}
