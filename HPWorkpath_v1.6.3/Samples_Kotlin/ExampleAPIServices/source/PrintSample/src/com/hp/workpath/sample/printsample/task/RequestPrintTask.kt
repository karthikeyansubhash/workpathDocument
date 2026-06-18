// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.printsample.task

import android.content.SharedPreferences
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.coroutineScope
import androidx.preference.PreferenceManager
import com.hp.workpath.api.CapabilitiesExceededException
import com.hp.workpath.api.printer.NetworkCredentialsAttributes
import com.hp.workpath.api.printer.PrintAttributes
import com.hp.workpath.api.printer.PrintAttributesCaps
import com.hp.workpath.api.printer.PrinterService
import com.hp.workpath.api.printer.PrintletAttributes
import com.hp.workpath.sample.printsample.Logger
import com.hp.workpath.sample.printsample.MainActivity
import com.hp.workpath.sample.printsample.MainActivity.Companion.BATCH_CLICKED
import com.hp.workpath.sample.printsample.MainActivity.Companion.PRINT_CLICKED
import com.hp.workpath.sample.printsample.R
import com.hp.workpath.sample.printsample.fragments.PrintConfigureFragment
import kotlinx.coroutines.*
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.log

class RequestPrintTask(context: MainActivity) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private val mPrefs: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    private lateinit var mErrorMsg: String
    private var mThrowable: Throwable? = null
    private var rid: String? = null
    var isBackGround: Boolean = false


    suspend fun execute(buttonClicked: Int, backgroundJob: Boolean) {
        mContextRef.get()?.run {
            try {

                val settingsUi = mPrefs.getBoolean(PrintConfigureFragment.PREF_SHOW_SETTINGS, false)
                Log.i(TAG, "Settings UI:$settingsUi")
                isBackGround = backgroundJob
                var attributes: PrintAttributes? = null

                if (!settingsUi) {
                    val caps: PrintAttributesCaps? = mContextRef.get()?.capabilities
                    if (caps == null) {
                        mContextRef.get()?.run {
                            mErrorMsg = getString(R.string.capabilities_not_loaded)
                            onPostExecute(rid)
                        }
                        return
                    }
                    // Build PrintAttributes based on preferences values
                    val duplex = PrintAttributes.Duplex.valueOf(
                        mPrefs.getString(
                            PrintConfigureFragment.PREF_DUPLEX_MODE,
                            PrintAttributes.Duplex.DEFAULT.name
                        )
                            ?: PrintAttributes.Duplex.DEFAULT.name
                    )
                    Log.i(TAG, "Selected Duplex:" + duplex.name)

                    val cm = PrintAttributes.ColorMode.valueOf(
                        mPrefs.getString(
                            PrintConfigureFragment.PREF_COLOR_MODE,
                            PrintAttributes.ColorMode.DEFAULT.name
                        )
                            ?: PrintAttributes.ColorMode.DEFAULT.name
                    )
                    Log.i(TAG, "Selected Color Mode:" + cm.name)

                    val saf = mPrefs.getString(
                        PrintConfigureFragment.PREF_AUTOFIT,
                        PrintAttributes.AutoFit.DEFAULT.name
                    )
                    val af = saf?.let { PrintAttributes.AutoFit.valueOf(it) }
                    Log.i(TAG, "Selected Auto Fit: $saf")

                    val sm = PrintAttributes.StapleMode.valueOf(
                        mPrefs.getString(
                            PrintConfigureFragment.PREF_STAPLE_MODE,
                            PrintAttributes.StapleMode.DEFAULT.name
                        )
                            ?: PrintAttributes.StapleMode.DEFAULT.name
                    )
                    Log.i(TAG, "Selected Staple Mode:" + sm.name)

                    val collateMode = PrintAttributes.CollateMode.valueOf(
                        mPrefs.getString(
                            PrintConfigureFragment.PREF_COLLATE_MODE,
                            PrintAttributes.CollateMode.DEFAULT.name
                        )
                            ?: PrintAttributes.CollateMode.DEFAULT.name
                    )
                    Log.i(TAG, "Selected Collate Mode:" + collateMode.name)

                    val psrc = PrintAttributes.PaperSource.valueOf(
                        mPrefs.getString(
                            PrintConfigureFragment.PREF_PAPER_SOURCE,
                            PrintAttributes.PaperSource.DEFAULT.name
                        )
                            ?: PrintAttributes.PaperSource.DEFAULT.name
                    )
                    Log.i(TAG, "Selected Paper Source:" + psrc.name)

                    val psz = PrintAttributes.PaperSize.valueOf(
                        mPrefs.getString(
                            PrintConfigureFragment.PREF_PAPER_SIZE,
                            PrintAttributes.PaperSize.DEFAULT.name
                        )
                            ?: PrintAttributes.PaperSize.DEFAULT.name
                    )
                    Log.i(TAG, "Selected Paper Size:" + psz.name)

                    val paperType = PrintAttributes.PaperType.valueOf(
                        mPrefs.getString(
                            PrintConfigureFragment.PREF_PAPER_TYPE,
                            PrintAttributes.PaperType.DEFAULT.name
                        )
                            ?: PrintAttributes.PaperType.DEFAULT.name
                    )
                    Log.i(TAG, "Selected Paper Type:" + paperType.name)

                    val dfmt = PrintAttributes.DocumentFormat.valueOf(
                        mPrefs.getString(
                            PrintConfigureFragment.PREF_DOC_FORMAT,
                            PrintAttributes.DocumentFormat.AUTO.name
                        )
                            ?: PrintAttributes.DocumentFormat.AUTO.name
                    )
                    Log.i(TAG, "Selected Document Format:" + dfmt.name)

                    val copies = Integer.valueOf(
                        mPrefs.getString(PrintConfigureFragment.PREF_COPIES, "1")
                            ?: "1"
                    )
                    Log.i(TAG, "Selected copies: $copies")

                    val jobName = mPrefs.getString(PrintConfigureFragment.PREF_JOB_NAME, null)
                    Log.i(TAG, "Selected jobName: $jobName")

                    val source = PrintAttributes.Source.valueOf(
                        mPrefs.getString(
                            PrintConfigureFragment.PREF_SOURCE,
                            PrintAttributes.Source.STORAGE.name
                        )
                            ?: PrintAttributes.Source.STORAGE.name
                    )
                    Log.i(TAG, "Selected source: $source")

                    val ot = PrintAttributes.Orientation.valueOf(
                        mPrefs.getString(
                            PrintConfigureFragment.PREF_ORIENTATION,
                            PrintAttributes.Orientation.DEFAULT.name
                        )
                            ?: PrintAttributes.Orientation.DEFAULT.name
                    )
                    Log.i(TAG, "Selected Orientation:" + ot.name)

                    val pq = PrintAttributes.PrintQuality.valueOf(
                        mPrefs.getString(
                            PrintConfigureFragment.PREF_PRINT_QUALITY,
                            PrintAttributes.PrintQuality.DEFAULT.name
                        )
                            ?: PrintAttributes.PrintQuality.DEFAULT.name
                    )
                    Log.i(TAG, "Selected Print Quality:" + pq.name)

                    val ob = PrintAttributes.OutputBin.valueOf(
                        mPrefs.getString(
                            PrintConfigureFragment.PREF_OUTPUT_BIN,
                            PrintAttributes.OutputBin.DEFAULT.name
                        )
                            ?: PrintAttributes.OutputBin.DEFAULT.name
                    )
                    Log.i(TAG, "Selected Output Bin:" + ob.name)

                    val startPageRanges = Integer.valueOf(
                        mPrefs.getString(PrintConfigureFragment.PREF_START_PAGE_RANGES, "0")
                            ?: "0"
                    )
                    Log.i(TAG, "Selected Start Page Ranges: $startPageRanges")

                    val endPageRanges = Integer.valueOf(
                        mPrefs.getString(PrintConfigureFragment.PREF_END_PAGE_RANGES, "0")
                            ?: "0"
                    )
                    Log.i(TAG, "Selected End Page Ranges: $endPageRanges")

                    var finishingSet: MutableSet<String> = HashSet()
                    finishingSet.add(PrintAttributes.Finishings.DEFAULT.name)
                    finishingSet =
                        mPrefs.getStringSet(PrintConfigureFragment.PREF_FINISHINGS, finishingSet)
                            ?: finishingSet
                    val fo: MutableList<PrintAttributes.Finishings> = ArrayList()
                    for (finishing in finishingSet) {
                        fo.add(PrintAttributes.Finishings.valueOf(finishing))
                    }
                    Log.i(TAG, "Selected Finishings:$fo")

                    when (source) {
                        PrintAttributes.Source.STORAGE -> {
                            val filePath =
                                mPrefs.getString(PrintConfigureFragment.PREF_FILENAME, "") ?: ""
                            Log.i(TAG, "Selected path: $filePath")
                            attributes =
                                PrintAttributes.PrintFromStorageBuilder(Uri.fromFile(File(filePath)))
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
                                    .build(caps)
                            if (buttonClicked == BATCH_CLICKED) {
                                if (File(filePath).isFile) {
                                    batchJobs.add(attributes)
                                    showSnackBar(File(filePath).name + " Added to batch")
                                } else {
                                    showSnackBar("No File Selected")
                                }
                            }
                        }
                        PrintAttributes.Source.HTTP -> {
                            val fileUri = mPrefs.getString(PrintConfigureFragment.PREF_URI, "")
                            Log.i(TAG, "Selected uri: $fileUri")
                            val fileUriUsername =
                                mPrefs.getString(PrintConfigureFragment.PREF_URI_USERNAME, "")
                                    ?: ""
                            val fileUriPassword =
                                mPrefs.getString(PrintConfigureFragment.PREF_URI_PASSWORD, "")
                                    ?: ""
                            var networkCredentialsAttributes: NetworkCredentialsAttributes? = null
                            if (!TextUtils.isEmpty(fileUriUsername) && !TextUtils.isEmpty(
                                    fileUriPassword
                                )
                            ) {
                                networkCredentialsAttributes =
                                    NetworkCredentialsAttributes.Builder()
                                        .setUserName(fileUriUsername)
                                        .setPassword(fileUriPassword)
                                        .build()
                            }
                            // building with common print attributes set
                            attributes = PrintAttributes.PrintFromHttpBuilder(Uri.parse(fileUri))
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
                                .build(caps)
                            if (buttonClicked == BATCH_CLICKED) {
                                lifecycle.coroutineScope.launch(Dispatchers.IO) {
                                    try {
                                        HttpURLConnection.setFollowRedirects(false);
                                        val con: HttpURLConnection =
                                            URL(fileUri).openConnection() as HttpURLConnection
                                        con.requestMethod = "HEAD";
                                        if (con.responseCode == HttpURLConnection.HTTP_OK) {
                                            Log.d("FILE_EXISTS", "true");
                                            val filePath = Uri.parse(fileUri)
                                            attributes?.let { batchJobs.add(it) }
                                            filePath.path?.let {
                                                showSnackBar(File(it).name + " Added to batch")
                                            }
                                        } else {
                                            showSnackBar("No File Selected")
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace();
                                        showSnackBar("FILE_EXISTS")
                                        Log.d("FILE_EXISTS", "false")
                                    }
                                }
                            }
                        }
                        PrintAttributes.Source.USB -> {
                            val filePath =
                                mPrefs.getString(PrintConfigureFragment.PREF_USB_FILENAME, "") ?: ""
                            Log.i(TAG, "Selected usb file: $filePath")
                            // building with common print attributes set
                            attributes = PrintAttributes.PrintFromUsbBuilder(filePath)
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
                                .build(caps)
                            if (buttonClicked == BATCH_CLICKED) {
                                if (File(filePath).isFile) {
                                    batchJobs.add(attributes)
                                    showSnackBar(File(filePath).name + " Added to batch")
                                } else {
                                    showSnackBar("No File Selected")
                                }
                            }
                        }
                        PrintAttributes.Source.STREAM -> {
                            val filePath =
                                mPrefs.getString(PrintConfigureFragment.PREF_STREAM_FILENAME, "") ?: ""
                            Log.i(TAG, "Selected file for stream: $filePath")
                            // can print from any InputStream, here FileInputStream as example
                            val printStream: InputStream = FileInputStream(File(filePath))
                            // building with common print attributes set
                            attributes = PrintAttributes.PrintFromStreamBuilder(printStream)
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
                                .build(caps)
                        }
                    }
                }
                val taskAttribs = PrintletAttributes.Builder()
                    .setShowSettingsUi(settingsUi)
                    .setBackgroundJob(isBackGround)
                    .build()
                // Submit the job
                if (buttonClicked == PRINT_CLICKED && batchJobs.isEmpty()) {
                    rid = mContextRef.get()
                        ?.let { PrinterService.submit(it, attributes, taskAttribs) }
                    Log.i(TAG, "Job submitted with rid = $rid")
                } else if (buttonClicked == PRINT_CLICKED && !batchJobs.isEmpty()) {
                    rid = mContextRef.get()
                        ?.let { PrinterService.submit(it, batchJobs, taskAttribs) }
                    Log.i(TAG, "Job submitted with rid = $rid")
                    batchJobs.clear()
                }
            } catch (cee: CapabilitiesExceededException) {
                mErrorMsg = "CapabilitiesExceededException"
                mThrowable = cee
            } catch (iae: IllegalArgumentException) {
                mErrorMsg = "IllegalArgumentException"
                mThrowable = iae
            } catch (t: Throwable) {
                mErrorMsg = "Unknown Throwable"
                mThrowable = t
            }
            if (buttonClicked == PRINT_CLICKED) {
                onPostExecute(rid)
            }
        }
    }

    private suspend fun onPostExecute(rid: String?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                if (!TextUtils.isEmpty(rid)) {
                    setRid(rid)
                    Logger.showResult(this, "Job submitted with rid = $rid")
                } else if (mThrowable != null) {
                    Logger.showResult(this, "$mErrorMsg, ${mThrowable?.message}")
                } else {
                    Logger.showResult(this, mErrorMsg)
                }
            }
        }
    }

    companion object {
        private const val TAG = MainActivity.TAG
    }
}
