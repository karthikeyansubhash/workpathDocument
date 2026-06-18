package com.hp.workpath.sample.deviceusagesample.view.deviceusage.printer;

import static com.hp.workpath.sample.deviceusagesample.JobCategoryAndMediaSizeActivity.DATA;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.hp.workpath.api.deviceusage.printer.PrinterInfo;
import com.hp.workpath.sample.deviceusagesample.R;
import com.hp.workpath.sample.deviceusagesample.FaxByMediaSizeActivity;

public class FaxByMediaSizeView {
    LinearLayout rootView;

    LayoutInflater inflater;

    Button faxByMediaSizeButton;

    public FaxByMediaSizeView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.inflater = inflater;
    }

    public void setFaxByMediaSize(PrinterInfo.FaxByMediaSize[] faxByMediaSize) {
        rootView.removeAllViews();
        if (faxByMediaSize != null) {
            rootView.addView(setFaxByMediaSizeInternal(faxByMediaSize));
        } else {
            LinearLayout parent = (LinearLayout) ((ViewGroup) rootView).getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private View setFaxByMediaSizeInternal(final PrinterInfo.FaxByMediaSize[] categories) {
        View view = inflater.inflate(R.layout.layout_button, rootView, false);
        initViewFaxByMediaSize(view);
        if (categories != null) {
            faxByMediaSizeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    Intent intent = new Intent(v.getContext(), FaxByMediaSizeActivity.class);
                    intent.putExtra(DATA, gson.toJson(categories));
                    v.getContext().startActivity(intent);
                }
            });
        }
        return view;
    }

    private void initViewFaxByMediaSize(View view) {
        faxByMediaSizeButton = view.findViewById(R.id.detailButton);
    }
}
