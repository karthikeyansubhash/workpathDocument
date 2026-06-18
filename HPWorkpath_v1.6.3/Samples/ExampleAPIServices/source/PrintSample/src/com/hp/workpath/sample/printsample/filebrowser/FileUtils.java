// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.printsample.filebrowser;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.hp.workpath.sample.printsample.MainActivity;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.Locale;

public class FileUtils {
    private FileUtils() {
    }

    private static final String TAG = MainActivity.TAG + "/U";
    public static final String HIDDEN_PREFIX = ".";
    public static final String PATH = "path";
    private static final String ASSET_TEST_PAGES = "test_pages";

    public static Comparator<File> COMPARATOR = new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
            return f1.getName().toLowerCase().compareTo(
                    f2.getName().toLowerCase());
        }
    };

    public static FileFilter FILE_FILTER = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return file.isFile() && FileUtils.accept(file.getName());
        }
    };

    public static FileFilter DIR_FILTER = new FileFilter() {
        @Override
        public boolean accept(File file) {
            final String fileName = file.getName();
            return file.isDirectory() && !fileName.startsWith(HIDDEN_PREFIX);
        }
    };

    public static boolean accept(final String name) {
        final String fileNameLower = name.toLowerCase(Locale.US);
        return !name.startsWith(HIDDEN_PREFIX) &&
                (fileNameLower.endsWith(".pdf")
                        || fileNameLower.endsWith(".jpg")
                        || fileNameLower.endsWith(".jpeg")
                        || fileNameLower.endsWith(".jpe")
                        || fileNameLower.endsWith(".jfif")
                        || fileNameLower.endsWith(".tiff")
                        || fileNameLower.endsWith(".tif")
                        || fileNameLower.endsWith(".ps")
                        || fileNameLower.endsWith(".txt")
                        || fileNameLower.endsWith(".pcl")
                        || fileNameLower.endsWith(".prn"));
    }

    public static boolean copyAssets(Context context) {
        AssetManager assetManager = context.getAssets();
        String[] files;
        try {
            files = assetManager.list(ASSET_TEST_PAGES);
        } catch (IOException e) {
            Log.e(TAG, "Failed to get asset file list." + e.getMessage());
            return false;
        }
        if (files != null)
            for (String filename : files) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open(ASSET_TEST_PAGES + "/" + filename);
                    File outFile = new File(context.getFilesDir(), filename);
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                } catch (IOException e) {
                    Log.e(TAG, "Failed to copy asset file: " + filename);
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
