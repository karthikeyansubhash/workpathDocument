// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.deviceusagesample.view.deviceusage;

import static com.hp.workpath.sample.deviceusagesample.MainActivity.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hp.workpath.api.deviceusage.printer.PrinterInfo;
import com.hp.workpath.sample.deviceusagesample.R;
import com.hp.workpath.sample.deviceusagesample.view.Utils;
import com.hp.workpath.sample.deviceusagesample.view.deviceusage.printer.A4EquivalentByJobCategoryView;
import com.hp.workpath.sample.deviceusagesample.view.deviceusage.printer.ByColorModeView;
import com.hp.workpath.sample.deviceusagesample.view.deviceusage.printer.ByJobCategoryAndMediaSizeView;
import com.hp.workpath.sample.deviceusagesample.view.deviceusage.printer.ByJobCategoryView;
import com.hp.workpath.sample.deviceusagesample.view.deviceusage.printer.CopyByMediaSizeView;
import com.hp.workpath.sample.deviceusagesample.view.deviceusage.printer.FaxByMediaSizeView;
import com.hp.workpath.sample.deviceusagesample.view.deviceusage.printer.PlexView;
import com.hp.workpath.sample.deviceusagesample.view.deviceusage.printer.PrintByMediaSizeView;
import com.hp.workpath.sample.deviceusagesample.view.deviceusage.printer.PlexByMediaSizeView;

public class PrinterInfoView {

    LinearLayout rootView;
    View view;

    PlexView plexView;
    ByJobCategoryView byJobCategoryView;
    ByJobCategoryAndMediaSizeView byJobCategoryAndMediaSizeView;
    ByColorModeView byColorModeView;
    A4EquivalentByJobCategoryView a4EquivalentByJobCategoryView;
    PrintByMediaSizeView printByMediaSizeView;
    CopyByMediaSizeView copyByMediaSizeView;
    FaxByMediaSizeView faxByMediaSizeView;
    PlexByMediaSizeView plexByMediaSizeView;

    ViewGroup layoutSheets;
    ViewGroup layoutEngineCycles;

    LinearLayout layoutPlex;
    LinearLayout layoutByJobCategory;
    LinearLayout layoutByJobCategoryAndMediaSize;
    LinearLayout layoutByColorMode;
    LinearLayout layoutA4EquivalentByJobCategory;
    LinearLayout layoutPrintByMediaSize;
    LinearLayout layoutCopyByMediaSize;
    LinearLayout layoutFaxByMediaSize;
    LinearLayout layoutPlexByMediaSize;


    public PrinterInfoView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.view = inflater.inflate(R.layout.layout_printer_info, rootView, false);

        initViewPrinterInfo();
        initViewClass(inflater);
    }

    public void setPrinterInfo(PrinterInfo printerInfo) {
        rootView.removeAllViews();
        if (printerInfo != null) {
            PrinterInfo.A4EquivalentByJobCategory[] a4EquivalentByJobCategory = printerInfo.getA4EquivalentByJobCategory();
            for (int index = 0; index < a4EquivalentByJobCategory.length; index++) {
                Log.d(TAG,"a4EquivalentByJobCategory JobCategory : " + a4EquivalentByJobCategory[index].getJobCategory());
                Log.d(TAG,"a4EquivalentByJobCategory ColorDeciImpressions : " + a4EquivalentByJobCategory[index].getColorDeciImpressions());
                Log.d(TAG,"a4EquivalentByJobCategory MonoDeciImpressions : " + a4EquivalentByJobCategory[index].getMonoDeciImpressions());
                Log.d(TAG,"a4EquivalentByJobCategory TotalDeciImpressions : " + a4EquivalentByJobCategory[index].getTotalDeciImpressions());
            }


            PrinterInfo.PrintByMediaSize[] printByMediaSize = printerInfo.getPrintByMediaSize();
            for (int index = 0; index < printByMediaSize.length; index++) {
                Log.d(TAG,"printByMediaSize JobCategory : " + printByMediaSize[index].getMediaSize());
                Log.d(TAG,"printByMediaSize ColorImpressions : " + printByMediaSize[index].getColorImpressions());
                Log.d(TAG,"printByMediaSize MonoImpressions : " + printByMediaSize[index].getMonoImpressions());
                Log.d(TAG,"printByMediaSize TotalImpressions : " + printByMediaSize[index].getTotalImpressions());
            }

            PrinterInfo.CopyByMediaSize[] copyByMediaSize = printerInfo.getCopyByMediaSize();
            for (int index = 0; index < copyByMediaSize.length; index++) {
                Log.d(TAG,"copyByMediaSize JobCategory : " + copyByMediaSize[index].getMediaSize());
                Log.d(TAG,"copyByMediaSize ColorImpressions : " + copyByMediaSize[index].getColorImpressions());
                Log.d(TAG,"copyByMediaSize MonoImpressions : " + copyByMediaSize[index].getMonoImpressions());
                Log.d(TAG,"copyByMediaSize TotalImpressions : " + copyByMediaSize[index].getTotalImpressions());
            }

            PrinterInfo.FaxByMediaSize[] faxByMediaSize = printerInfo.getFaxByMediaSize();
            for (int index = 0; index < faxByMediaSize.length; index++) {
                Log.d(TAG,"faxByMediaSize JobCategory : " + faxByMediaSize[index].getMediaSize());
                Log.d(TAG,"faxByMediaSize ColorImpressions : " + faxByMediaSize[index].getColorImpressions());
                Log.d(TAG,"faxByMediaSize MonoImpressions : " + faxByMediaSize[index].getMonoImpressions());
                Log.d(TAG,"faxByMediaSize TotalImpressions : " + faxByMediaSize[index].getTotalImpressions());
            }

            PrinterInfo.PlexByMediaSize[] plexByMediaSize = printerInfo.getPlexByMediaSize();
            for (int index = 0; index < plexByMediaSize.length; index++) {
                Log.d(TAG,"plexByMediaSize JobCategory : " + plexByMediaSize[index].getMediaSize());
                Log.d(TAG,"plexByMediaSize SimplexSheets : " + plexByMediaSize[index].getSimplexSheets());
                Log.d(TAG,"plexByMediaSize DuplexSheets : " + plexByMediaSize[index].getDuplexSheets());
                Log.d(TAG,"plexByMediaSize TotalSheets : " + plexByMediaSize[index].getTotalSheets());
            }

            Utils.setSummary(layoutSheets, printerInfo.getSheets());
            Utils.setSummary(layoutEngineCycles, printerInfo.getEngineCycles());
            plexView.setPlex(printerInfo.getByPrintPlex());
            byJobCategoryView.setByJobCategory(printerInfo.getByJobCategory());
            byJobCategoryAndMediaSizeView.setByJobCategoryAndMediaSize(printerInfo.getByJobCategoryAndMediaSize());
            byColorModeView.setByColorMode(printerInfo.getByColorMode());
            a4EquivalentByJobCategoryView.setA4EquivalentByJobCategory(printerInfo.getA4EquivalentByJobCategory());
            printByMediaSizeView.setPrintByMediaSize(printerInfo.getPrintByMediaSize());
            copyByMediaSizeView.setCopyByMediaSize(printerInfo.getCopyByMediaSize());
            faxByMediaSizeView.setFaxByMediaSize(printerInfo.getFaxByMediaSize());
            plexByMediaSizeView.setPlexByMediaSize(printerInfo.getPlexByMediaSize());
            rootView.addView(view);
        } else {
            LinearLayout parent = (LinearLayout) ((ViewGroup) rootView).getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private void initViewPrinterInfo() {
        layoutSheets = Utils.setTitle(view.findViewById(R.id.layoutSheets), R.string.sheets);
        layoutEngineCycles = Utils.setTitle(view.findViewById(R.id.layoutEngineCycles), R.string.engineCycles);
        layoutPlex = Utils.getLayout(view.findViewById(R.id.layoutByPrintPlex), R.string.byPrintPlex);
        layoutByJobCategory = Utils.getLayout(view.findViewById(R.id.layoutByJobCategory), R.string.byJobCategory);
        layoutByJobCategoryAndMediaSize = Utils.getLayout(view.findViewById(R.id.layoutByJobCategoryAndMediaSize), R.string.byJobCategoryAndMediaSize);
        layoutByColorMode = Utils.getLayout(view.findViewById(R.id.layoutByColorMode), R.string.byColorMode);
        layoutA4EquivalentByJobCategory= Utils.getLayout(view.findViewById(R.id.layoutA4EquivalentByJobCategory), R.string.a4EquivalentByJobCategory);
        layoutPrintByMediaSize = Utils.getLayout(view.findViewById(R.id.layoutPrintByMediaSize), R.string.printByMediaSize);
        layoutCopyByMediaSize = Utils.getLayout(view.findViewById(R.id.layoutCopyByMediaSize),R.string.copyByMediaSize);
        layoutFaxByMediaSize = Utils.getLayout(view.findViewById(R.id.layoutFaxByMediaSize),R.string.faxByMediaSize);
        layoutPlexByMediaSize = Utils.getLayout(view.findViewById(R.id.layoutPlexByMediaSiz), R.string.plexByMediaSize);
    }

    private void initViewClass(LayoutInflater inflater) {
        plexView = new PlexView(inflater, layoutPlex);
        byJobCategoryView = new ByJobCategoryView(inflater, layoutByJobCategory);
        byJobCategoryAndMediaSizeView = new ByJobCategoryAndMediaSizeView(inflater, layoutByJobCategoryAndMediaSize);
        byColorModeView = new ByColorModeView(inflater, layoutByColorMode);
        a4EquivalentByJobCategoryView = new A4EquivalentByJobCategoryView(inflater, layoutA4EquivalentByJobCategory);
        printByMediaSizeView = new PrintByMediaSizeView(inflater, layoutPrintByMediaSize);
        copyByMediaSizeView = new CopyByMediaSizeView(inflater, layoutCopyByMediaSize);
        faxByMediaSizeView = new FaxByMediaSizeView(inflater, layoutFaxByMediaSize);
        plexByMediaSizeView = new PlexByMediaSizeView(inflater, layoutPlexByMediaSize);
    }
}
