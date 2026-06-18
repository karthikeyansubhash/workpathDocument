// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.copysample.task;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.preference.PreferenceManager;

import com.hp.workpath.api.CapabilitiesExceededException;
import com.hp.workpath.api.copier.CopierService;
import com.hp.workpath.api.copier.CopyAttributesCaps;
import com.hp.workpath.api.copier.JobCredentialsAttributes;
import com.hp.workpath.api.copier.StoredJobAttributes;
import com.hp.workpath.sample.copysample.Logger;
import com.hp.workpath.sample.copysample.MainActivity;
import com.hp.workpath.sample.copysample.R;
import com.hp.workpath.sample.copysample.fragments.CopyConfigureFragment;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReleaseStoredTask {

    private final WeakReference<MainActivity> mContextRef;
    private JobCredentialsAttributes jobCredentials;

    private final SharedPreferences mPrefs;
    private Throwable mThrowable;
    private String mErrorMsg = null;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    public ReleaseStoredTask(final MainActivity context, JobCredentialsAttributes jobCredentials) {
        this.mContextRef = new WeakReference<>(context);
        this.jobCredentials = jobCredentials;
        this.mPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public void taskExecute(final String... params) {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    MainActivity activity = mContextRef.get();

                    try {
                        String storedJobId = params[0];

                        // Obtain Caps to build copy Attributes
                        final CopyAttributesCaps caps = activity.getCapabilities();

                        if (caps == null) {
                            mErrorMsg = activity.getString(R.string.capabilities_not_loaded);
                            onPostExecute(null);
                            return;
                        }

                        final int copies = Integer.valueOf(
                                mPrefs.getString(CopyConfigureFragment.PREF_COPIES, "1"));

                        StoredJobAttributes storedJobAttributes = new StoredJobAttributes.StoredJobBuilder(storedJobId)
                                .setCopies(copies)
                                .setJobCredentials(jobCredentials)
                                .build(caps);

                        // Release the job
                        final String rid = CopierService.releaseStoredJob(activity, storedJobAttributes);
                        onPostExecute(rid);
                    } catch (CapabilitiesExceededException cee) {
                        mErrorMsg = "CapabilitiesExceededException";
                        mThrowable = cee;
                        executor.shutdown();
                        onPostExecute(null);
                    } catch (IllegalArgumentException iae) {
                        mErrorMsg = "IllegalArgumentException";
                        mThrowable = iae;
                        executor.shutdown();
                        onPostExecute(null);
                    } catch (Throwable t) {
                        mErrorMsg = "Unknown exception";
                        mThrowable = t;
                        executor.shutdown();
                        onPostExecute(null);
                    }
                }
            });
        } catch (Exception e) {
            mThrowable = e;
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
                    Logger.showResult(mContextRef.get(), "Release StoredJob submitted with rid = " + rid);
                } else if (mThrowable != null) {
                    Logger.showResult(mContextRef.get(), mErrorMsg + " " + mThrowable);
                } else if (mErrorMsg != null) {
                    Logger.showResult(mContextRef.get(), mErrorMsg);
                }
            }
        });
    }
} 