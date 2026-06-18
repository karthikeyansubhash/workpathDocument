// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.printsample.task;

import static com.hp.workpath.sample.printsample.MainActivity.BATCH_CLICKED;
import static com.hp.workpath.sample.printsample.MainActivity.PRINT_CLICKED;
import static com.hp.workpath.sample.printsample.MainActivity.batchJobs;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.hp.workpath.api.CapabilitiesExceededException;
import com.hp.workpath.api.printer.NetworkCredentialsAttributes;
import com.hp.workpath.api.printer.PrintAttributes;
import com.hp.workpath.api.printer.PrintAttributesCaps;
import com.hp.workpath.api.printer.PrinterService;
import com.hp.workpath.api.printer.PrintletAttributes;
import com.hp.workpath.sample.printsample.Logger;
import com.hp.workpath.sample.printsample.MainActivity;
import com.hp.workpath.sample.printsample.R;
import com.hp.workpath.sample.printsample.fragments.PrintConfigureFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Async task to request print.
 */
public class RequestPrintTask {

    private static final String TAG = MainActivity.TAG;

    private final WeakReference<MainActivity> mContextRef;

    private final SharedPreferences mPrefs;
    private String mErrorMsg = null;

    private Boolean isBackGround;
    private int ButtonClicked;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public RequestPrintTask(final MainActivity context, int buttonClicked, Boolean backgroundJob) {
        this.mContextRef = new WeakReference<>(context);
        this.mPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        isBackGround = backgroundJob;
        ButtonClicked = buttonClicked;
    }

    public void taskExecute() {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        final boolean settingsUi = mPrefs.getBoolean(PrintConfigureFragment.PREF_SHOW_SETTINGS, false);
                        Log.i(TAG, "Settings UI:" + settingsUi);

                        PrintAttributes attributes = null;

                        if (!settingsUi) {
                            final PrintAttributesCaps caps = mContextRef.get().getCapabilities();

                            if (caps == null) {
                                mErrorMsg = mContextRef.get().getString(R.string.capabilities_not_loaded);
                                onPostExecute(null);
                                return;
                            }

                            // Build PrintAttributes based on preferences values
                            final PrintAttributes.Duplex duplex = PrintAttributes.Duplex.valueOf(
                                    mPrefs.getString(PrintConfigureFragment.PREF_DUPLEX_MODE,
                                            PrintAttributes.Duplex.DEFAULT.name()));
                            Log.i(TAG, "Selected Duplex:" + duplex.name());

                            final PrintAttributes.ColorMode cm = PrintAttributes.ColorMode.valueOf(
                                    mPrefs.getString(PrintConfigureFragment.PREF_COLOR_MODE,
                                            PrintAttributes.ColorMode.DEFAULT.name()));
                            Log.i(TAG, "Selected Color Mode:" + cm.name());

                            final String saf = mPrefs.getString(PrintConfigureFragment.PREF_AUTOFIT,
                                    PrintAttributes.AutoFit.DEFAULT.name());
                            final PrintAttributes.AutoFit af = PrintAttributes.AutoFit.valueOf(saf);
                            Log.i(TAG, "Selected Auto Fit: " + saf);

                            final PrintAttributes.StapleMode sm = PrintAttributes.StapleMode.valueOf(
                                    mPrefs.getString(PrintConfigureFragment.PREF_STAPLE_MODE,
                                            PrintAttributes.StapleMode.DEFAULT.name()));
                            Log.i(TAG, "Selected Staple Mode:" + sm.name());

                            final PrintAttributes.CollateMode collateMode = PrintAttributes.CollateMode.valueOf(
                                    mPrefs.getString(PrintConfigureFragment.PREF_COLLATE_MODE,
                                            PrintAttributes.CollateMode.DEFAULT.name()));
                            Log.i(TAG, "Selected Collate Mode:" + collateMode.name());

                            final PrintAttributes.PaperSource psrc = PrintAttributes.PaperSource.valueOf(
                                    mPrefs.getString(PrintConfigureFragment.PREF_PAPER_SOURCE,
                                            PrintAttributes.PaperSource.DEFAULT.name()));
                            Log.i(TAG, "Selected Paper Source:" + psrc.name());

                            final PrintAttributes.PaperSize psz = PrintAttributes.PaperSize.valueOf(
                                    mPrefs.getString(PrintConfigureFragment.PREF_PAPER_SIZE,
                                            PrintAttributes.PaperSize.DEFAULT.name()));
                            Log.i(TAG, "Selected Paper Size:" + psz.name());

                            final PrintAttributes.PaperType paperType = PrintAttributes.PaperType.valueOf(
                                    mPrefs.getString(PrintConfigureFragment.PREF_PAPER_TYPE,
                                            PrintAttributes.PaperType.DEFAULT.name()));
                            Log.i(TAG, "Selected Paper Type:" + paperType.name());

                            final PrintAttributes.DocumentFormat dfmt = PrintAttributes.DocumentFormat.valueOf(
                                    mPrefs.getString(PrintConfigureFragment.PREF_DOC_FORMAT,
                                            PrintAttributes.DocumentFormat.AUTO.name()));
                            Log.i(TAG, "Selected Document Format:" + dfmt.name());

                            final int copies = Integer.valueOf(
                                    mPrefs.getString(PrintConfigureFragment.PREF_COPIES, "1"));
                            Log.i(TAG, "Selected copies: " + copies);

                            String jobName = mPrefs.getString(PrintConfigureFragment.PREF_JOB_NAME, null);
                            Log.i(TAG, "Selected jobName: " + jobName);

                            final PrintAttributes.Source source = PrintAttributes.Source.valueOf(
                                    mPrefs.getString(PrintConfigureFragment.PREF_SOURCE, PrintAttributes.Source.STORAGE.name()));

                            Log.i(TAG, "Selected source: " + source);

                            final PrintAttributes.Orientation ot = PrintAttributes.Orientation.valueOf(
                                    mPrefs.getString(PrintConfigureFragment.PREF_ORIENTATION, PrintAttributes.Orientation.DEFAULT.name()));
                            Log.i(TAG, "Selected Orientation:" + ot.name());

                            final PrintAttributes.PrintQuality pq = PrintAttributes.PrintQuality.valueOf(
                                    mPrefs.getString(PrintConfigureFragment.PREF_PRINT_QUALITY, PrintAttributes.PrintQuality.DEFAULT.name()));
                            Log.i(TAG, "Selected Print Quality:" + pq.name());

                            final PrintAttributes.OutputBin ob = PrintAttributes.OutputBin.valueOf(
                                    mPrefs.getString(PrintConfigureFragment.PREF_OUTPUT_BIN, PrintAttributes.OutputBin.DEFAULT.name()));
                            Log.i(TAG, "Selected Output Bin:" + ob.name());

                            String startPageRangeStr = mPrefs.getString(PrintConfigureFragment.PREF_START_PAGE_RANGES, null);

                            int startPageRanges = 0;
                            if (!TextUtils.isEmpty(startPageRangeStr)) {
                                startPageRanges = Integer.valueOf(startPageRangeStr);
                            }
                            Log.i(TAG, "Selected Start Page Ranges: " + startPageRanges);
                            String endPageRangeStr = mPrefs.getString(PrintConfigureFragment.PREF_END_PAGE_RANGES, null);

                            int endPageRanges = 0;
                            if (!TextUtils.isEmpty(endPageRangeStr)) {
                                endPageRanges = Integer.valueOf(endPageRangeStr);
                            }
                            Log.i(TAG, "Selected End Page Ranges: " + endPageRanges);

                            Set<String> finishingSet = new HashSet<>();
                            finishingSet.add(PrintAttributes.Finishings.DEFAULT.name());
                            finishingSet = mPrefs.getStringSet(PrintConfigureFragment.PREF_FINISHINGS, finishingSet);
                            List<PrintAttributes.Finishings> fo = new ArrayList<>();
                            for (String finishing : finishingSet) {
                                fo.add(PrintAttributes.Finishings.valueOf(finishing));
                            }
                            Log.i(TAG, "Selected Finishings:" + fo.toString());

                            if (source == PrintAttributes.Source.STORAGE) {
                                String filePath = mPrefs.getString(PrintConfigureFragment.PREF_FILENAME, "");
                                Log.i(TAG, "Selected path: " + filePath);

                                attributes =
                                        new PrintAttributes.PrintFromStorageBuilder(Uri.fromFile(new File(filePath)))
                                                .setCollateMode(collateMode)
                                                .setColorMode(cm)
                                                .setDuplex(duplex)
                                                .setAutoFit(af)
                                                .setStapleMode(sm)
                                                .setPaperSource(psrc)
                                                .setPaperSize(psz)
                                                .setPaperType(paperType)
                                                .setDocumentFormat(dfmt)
                                                .setCopies(copies)
                                                .setJobName(jobName)
                                                .setOrientation(ot)
                                                .setPrintQuality(pq)
                                                .setOutputBin(ob)
                                                .setStartPageRanges(startPageRanges)
                                                .setEndPageRanges(endPageRanges)
                                                .setFinishingsList(fo)
                                                .build(caps);
                                if (ButtonClicked == BATCH_CLICKED) {
                                    if (new File(filePath).isFile()) {
                                        batchJobs.add(attributes);
                                        mContextRef.get().showSnackBar(new File(filePath).getName() + " Added to batch");
                                    } else {
                                        mContextRef.get().showSnackBar("No File Selected");
                                    }
                                }
                            } else if (source == PrintAttributes.Source.HTTP) {
                                String fileUri = mPrefs.getString(PrintConfigureFragment.PREF_URI, "");
                                Log.i(TAG, "Selected uri: " + fileUri);

                                String fileUriUsername = mPrefs.getString(PrintConfigureFragment.PREF_URI_USERNAME, "");
                                String fileUriPassword = mPrefs.getString(PrintConfigureFragment.PREF_URI_PASSWORD, "");

                                NetworkCredentialsAttributes networkCredentialsAttributes = null;
                                if (!TextUtils.isEmpty(fileUriUsername) && !TextUtils.isEmpty(fileUriPassword)) {
                                    networkCredentialsAttributes = new NetworkCredentialsAttributes.Builder()
                                            .setUserName(fileUriUsername)
                                            .setPassword(fileUriPassword)
                                            .build();
                                }

                                // building with common print attributes set
                                attributes = new PrintAttributes.PrintFromHttpBuilder(Uri.parse(fileUri))
                                        .setCollateMode(collateMode)
                                        .setColorMode(cm)
                                        .setDuplex(duplex)
                                        .setAutoFit(af)
                                        .setStapleMode(sm)
                                        .setPaperSource(psrc)
                                        .setPaperSize(psz)
                                        .setPaperType(paperType)
                                        .setDocumentFormat(dfmt)
                                        .setCopies(copies)
                                        .setJobName(jobName)
                                        .setOrientation(ot)
                                        .setPrintQuality(pq)
                                        .setOutputBin(ob)
                                        .setStartPageRanges(startPageRanges)
                                        .setEndPageRanges(endPageRanges)
                                        .setFinishingsList(fo)
                                        .setNetworkCredentials(networkCredentialsAttributes)
                                        .build(caps);
                                if (ButtonClicked == BATCH_CLICKED) {
                                    try {
                                        HttpURLConnection.setFollowRedirects(false);
                                        HttpURLConnection con = (HttpURLConnection) new URL(fileUri).openConnection();
                                        con.setRequestMethod("HEAD");
                                        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                            Log.d("FILE_EXISTS", "true");
                                            Uri filePath = Uri.parse(fileUri);
                                            batchJobs.add(attributes);
                                            mContextRef.get().showSnackBar(new File(String.valueOf(filePath)).getName() + " Added to batch");
                                        } else {
                                            mContextRef.get().showSnackBar("No File Selected");
                                        }
                                    } catch (Exception e) {
                                        Logger.showResult(mContextRef.get(), e.getMessage());
                                        mContextRef.get().showSnackBar("FILE_EXISTS");
                                        Log.d("FILE_EXISTS", "false");
                                    }
                                }
                            } else if (source == PrintAttributes.Source.USB) {
                                String filePath = mPrefs.getString(PrintConfigureFragment.PREF_USB_FILENAME, "");

                                Log.i(TAG, "Selected usb file: " + filePath);

                                // building with common print attributes set
                                attributes = new PrintAttributes.PrintFromUsbBuilder(filePath)
                                        .setCollateMode(collateMode)
                                        .setColorMode(cm)
                                        .setDuplex(duplex)
                                        .setAutoFit(af)
                                        .setStapleMode(sm)
                                        .setPaperSource(psrc)
                                        .setPaperSize(psz)
                                        .setDocumentFormat(dfmt)
                                        .setCopies(copies)
                                        .setJobName(jobName)
                                        .setOrientation(ot)
                                        .setPrintQuality(pq)
                                        .setOutputBin(ob)
                                        .setStartPageRanges(startPageRanges)
                                        .setEndPageRanges(endPageRanges)
                                        .setFinishingsList(fo)
                                        .build(caps);
                                if (ButtonClicked == BATCH_CLICKED) {
                                    if (new File(filePath).isFile()) {
                                        batchJobs.add(attributes);
                                        mContextRef.get().showSnackBar(new File(filePath).getName() + " Added to batch");
                                    } else {
                                        mContextRef.get().showSnackBar("No File Selected");
                                    }
                                }
                            } else if (source == PrintAttributes.Source.STREAM) {
                                String filePath = mPrefs.getString(PrintConfigureFragment.PREF_STREAM_FILENAME, "");

                                Log.i(TAG, "Selected file for stream: " + filePath);

                                // can print from any InputStream, here FileInputStream as example
                                InputStream printStream = new FileInputStream(new File(filePath));

                                // building with common print attributes set
                                attributes = new PrintAttributes.PrintFromStreamBuilder(printStream)
                                        .setCollateMode(collateMode)
                                        .setColorMode(cm)
                                        .setDuplex(duplex)
                                        .setAutoFit(af)
                                        .setStapleMode(sm)
                                        .setPaperSource(psrc)
                                        .setPaperSize(psz)
                                        .setDocumentFormat(dfmt)
                                        .setCopies(copies)
                                        .setJobName(jobName)
                                        .setOrientation(ot)
                                        .setPrintQuality(pq)
                                        .setOutputBin(ob)
                                        .setStartPageRanges(startPageRanges)
                                        .setEndPageRanges(endPageRanges)
                                        .setFinishingsList(fo)
                                        .build(caps);
                            }

                /*if (batchJobs.isEmpty() || (source == PrintAttributes.Source.STREAM)) {
                    isBackGround = false;
                }*/
                        }

                        final PrintletAttributes taskAttribs = new PrintletAttributes.Builder()
                                .setShowSettingsUi(settingsUi)
                                .setBackgroundJob(isBackGround)
                                .build();

                        // Submit the job
                        if (ButtonClicked == PRINT_CLICKED && batchJobs.isEmpty()) {
                            Log.d(TAG, "single job doInBackground: " + isBackGround);
                            onPostExecute(PrinterService.submit(mContextRef.get(), attributes, taskAttribs));
                            return;
                        } else if (ButtonClicked == PRINT_CLICKED && !batchJobs.isEmpty()) {
                            Log.d(TAG, "batch job doInBackground: " + isBackGround);
                            String submit = PrinterService.submit(mContextRef.get(), batchJobs, taskAttribs);
                            batchJobs.clear();
                            onPostExecute(submit);
                        }
                    } catch (CapabilitiesExceededException cee) {
                        mErrorMsg = "CapabilitiesExceededException";
                        Logger.showResult(null, mErrorMsg + " " + cee.getMessage());
                        executor.shutdown();
                        onPostExecute(null);
                    } catch (IllegalArgumentException iae) {
                        mErrorMsg = "IllegalArgumentException";
                        Logger.showResult(null, mErrorMsg + " " + iae.getMessage());
                        executor.shutdown();
                        onPostExecute(null);
                    } catch (Throwable t) {
                        mErrorMsg = "Unknown exception";
                        Logger.showResult(null, mErrorMsg + " " + t.getMessage());
                        executor.shutdown();
                        onPostExecute(null);
                    }
                }
            });
        } catch (Exception e) {
            mErrorMsg = "Unknown exception";
            Logger.showResult(null, mErrorMsg + " " + e.getMessage());
            onPostExecute(null);
            executor.shutdown();
        }
    }

    private void onPostExecute(final String rid) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(rid)) {
                    mContextRef.get().setRid(rid);
                    Logger.showResult(mContextRef.get(), "Job submitted with rid = " + rid);
                } else if (mErrorMsg != null) {
                    Logger.showResult(mContextRef.get(), mErrorMsg);
                }
            }
        });
    }
}


