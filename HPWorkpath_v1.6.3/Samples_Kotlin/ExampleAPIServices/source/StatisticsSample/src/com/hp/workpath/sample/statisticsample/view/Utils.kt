// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.hp.workpath.sample.statisticsample.MainActivity
import com.hp.workpath.sample.statisticsample.R
import java.io.*

object Utils {

    fun setTitle(viewGroup: ViewGroup, id: Int): ViewGroup {
        (viewGroup.findViewById<View>(R.id.titleTextView) as TextView).setText(id)
        return viewGroup
    }

    fun <T> setSummary(viewGroup: ViewGroup, value: T?) {
        try {
            if (value != null) {
                val valueString: String = if (value is Enum<*>) {
                    (value as Enum<*>).name
                } else if (value is Int ||
                    value is Boolean
                ) {
                    value.toString()
                } else {
                    value as String
                }
                (viewGroup.findViewById<View>(R.id.summaryTextView) as TextView).text = valueString
                return
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        viewGroup.visibility = View.GONE
    }

    fun getLayout(viewGroup: ViewGroup, id: Int): LinearLayout {
        (viewGroup.findViewById<View>(R.id.titleTextView) as TextView).setText(id)
        return viewGroup.findViewById<View>(R.id.layoutChild) as LinearLayout
    }

    private const val ASSET_TEST_PAGES = "test_pages"

    @JvmStatic
    fun copyAssets(context: Context): Boolean {
        val assetManager = context.assets
        val files: Array<String> = try {
            assetManager.list(ASSET_TEST_PAGES) ?: return false
        } catch (e: IOException) {
            Log.e(MainActivity.TAG, "Failed to get asset file list." + e.message)
            return false
        }
        for (filename in files) {
            var `in`: InputStream? = null
            var out: OutputStream? = null
            try {
                `in` = assetManager.open("$ASSET_TEST_PAGES/$filename")
                val outFile = File(context.filesDir, filename)
                out = FileOutputStream(outFile)
                copyFile(`in`, out)
            } catch (e: IOException) {
                Log.e(MainActivity.TAG, "Failed to copy asset file: $filename")
            } finally {
                if (`in` != null) {
                    try {
                        `in`.close()
                    } catch (ignore: IOException) {
                    }
                }
                if (out != null) {
                    try {
                        out.close()
                    } catch (ignore: IOException) {
                    }
                }
            }
        }
        return true
    }

    @Throws(IOException::class)
    private fun copyFile(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while (`in`.read(buffer).also { read = it } != -1) {
            out.write(buffer, 0, read)
        }
    }
}