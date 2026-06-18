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
import com.hp.workpath.sample.deviceusagesample.PlexByMediaSizeActivity;
import com.hp.workpath.sample.deviceusagesample.PrintByMediaSizeActivity;
import com.hp.workpath.sample.deviceusagesample.R;

public class PlexByMediaSizeView {
    LinearLayout rootView;

    LayoutInflater inflater;

    Button plexByMediaSizeButton;

    public PlexByMediaSizeView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.inflater = inflater;
    }

    public void setPlexByMediaSize(PrinterInfo.PlexByMediaSize[] plexByMediaSize) {
        rootView.removeAllViews();
        if (plexByMediaSize != null) {
            rootView.addView(setPlexByMediaSizeInternal(plexByMediaSize));
        } else {
            LinearLayout parent = (LinearLayout) ((ViewGroup) rootView).getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private View setPlexByMediaSizeInternal(final PrinterInfo.PlexByMediaSize[] categories) {
        View view = inflater.inflate(R.layout.layout_button, rootView, false);
        initViewPlexByMediaSize(view);
        if (categories != null) {
            plexByMediaSizeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    Intent intent = new Intent(v.getContext(), PlexByMediaSizeActivity.class);
                    intent.putExtra(DATA, gson.toJson(categories));
                    v.getContext().startActivity(intent);
                }
            });
        }
        return view;
    }

    private void initViewPlexByMediaSize(View view) {
        plexByMediaSizeButton = view.findViewById(R.id.detailButton);
    }
}
