// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.view.deviceusage.printer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hp.workpath.api.deviceusage.Plex;
import com.hp.workpath.sample.deviceusagesample.R;
import com.hp.workpath.sample.deviceusagesample.view.Utils;

public class PlexView {

    LinearLayout rootView;
    LayoutInflater inflater;

    ViewGroup layoutPlex;
    ViewGroup layoutSheets;

    public PlexView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.inflater = inflater;
    }

    public void setPlex(Plex[] plexs) {
        rootView.removeAllViews();
        if (plexs != null) {
            for (int index = 0; index < plexs.length; index++) {
                rootView.addView(setPlexInternal(index, plexs[index]));
            }
        } else {
            LinearLayout parent = (LinearLayout) ((ViewGroup) rootView).getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private View setPlexInternal(int index, Plex plex) {
        View view = inflater.inflate(R.layout.layout_plex, rootView, false);
        initViewPlex(view);
        if (index % 2 == 0) {
            view.setBackgroundColor(view.getResources().getColor(R.color.option_background_color));
        }
        if (plex != null) {
            Utils.setSummary(layoutPlex, plex.getPlex());
            Utils.setSummary(layoutSheets, plex.getSheets());
        }
        return view;
    }

    private void initViewPlex(View view) {
        layoutPlex = Utils.setTitle(view.findViewById(R.id.layoutPlex), R.string.plex);
        layoutSheets = Utils.setTitle(view.findViewById(R.id.layoutSheets), R.string.sheets);
    }
}
