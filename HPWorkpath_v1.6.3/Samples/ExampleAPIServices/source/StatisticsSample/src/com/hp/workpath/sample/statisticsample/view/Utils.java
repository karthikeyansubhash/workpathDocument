// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hp.workpath.sample.statisticsample.MainActivity;
import com.hp.workpath.sample.statisticsample.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {

    private static final String ASSET_TEST_PAGES = "test_pages";

    public static ViewGroup setTitle(ViewGroup viewGroup, int id) {
        ((TextView) viewGroup.findViewById(R.id.titleTextView)).setText(id);
        return viewGroup;
    }

    public static <T> void setSummary(ViewGroup viewGroup, T value) {
        try {
            if (value != null) {
                String valueString;
                if (value instanceof Enum) {
                    valueString = ((Enum) value).name();
                } else if (value instanceof Integer ||
                        value instanceof Boolean) {
                    valueString = String.valueOf(value);
                } else {
                    valueString = (String) value;
                }
                ((TextView) viewGroup.findViewById(R.id.summaryTextView)).setText(valueString);
                return;
            }
        } catch (Throwable ignore) { }
        viewGroup.setVisibility(View.GONE);
    }

    public static LinearLayout getLayout(ViewGroup viewGroup, int id) {
        ((TextView) viewGroup.findViewById(R.id.titleTextView)).setText(id);
        return ((LinearLayout) viewGroup.findViewById(R.id.layoutChild));
    }

    public static boolean copyAssets(Context context) {
        AssetManager assetManager = context.getAssets();
        String[] files;
        try {
            files = assetManager.list(ASSET_TEST_PAGES);
        } catch (IOException e) {
            Log.e(MainActivity.TAG, "Failed to get asset file list." + e.getMessage());
            return false;
        }
        if (files != null)
            for (String filename : files) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open(ASSET_TEST_PAGES + "/" + filename);
                    File outFile = new File(context.getFilesDir(), filename);
                    if (!outFile.exists()) {
                        out = new FileOutputStream(outFile);
                        copyFile(in, out);
                    }
                } catch (IOException e) {
                    Log.e(MainActivity.TAG, "Failed to copy asset file: " + filename);
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ignore) {
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException ignore) {
                        }
                    }
                }
            }
        return true;
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
