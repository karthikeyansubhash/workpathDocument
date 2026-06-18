// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hp.workpath.api.deviceusage.printer.PrinterInfo;
import com.hp.workpath.sample.deviceusagesample.R;
import com.hp.workpath.sample.deviceusagesample.view.JobCategoryAndMediaSizeAdapter;

import java.lang.reflect.Type;
import java.util.List;

import static com.hp.workpath.sample.deviceusagesample.JobCategoryAndMediaSizeActivity.DATA;

public class JobCategoryAndMediaSizeFragment extends Fragment {

    private JobCategoryAndMediaSizeAdapter mListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_category_and_media_size, container, false);
        RecyclerView mListView = view.findViewById(R.id.jobCategoryAndMediaSizeView);
        mListAdapter = new JobCategoryAndMediaSizeAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mListView.setLayoutManager(layoutManager);
        mListView.setAdapter(mListAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL);
        mListView.addItemDecoration(dividerItemDecoration);

        String data = getArguments().getString(DATA);
        showJobCategoryAndMediaSize(data);
        return view;
    }

    private void showJobCategoryAndMediaSize(String data) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<PrinterInfo.ByJobCategoryAndMediaSize>>() {
        }.getType();
        List<PrinterInfo.ByJobCategoryAndMediaSize> list = gson.fromJson(data, listType);
        mListAdapter.setItem(list);
    }
}
