// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.printsample.filebrowser

import android.content.Context
import android.util.Log
import com.hp.workpath.sample.printsample.MainActivity
import java.io.File
import java.io.FileFilter
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Locale
import kotlin.Comparator

object FileUtils {
    const val HIDDEN_PREFIX = "."
    const val PATH = "path"
    private const val ASSET_TEST_PAGES = "test_pages"
    var COMPARATOR = Comparator<File> { f1, f2 ->
        f1.name.lowercase().compareTo(
                f2.name.lowercase())
    }
    var FILE_FILTER = FileFilter { file -> file.isFile && accept(file.name) }
    var DIR_FILTER = FileFilter { file ->
        val fileName = file.name
        file.isDirectory && !fileName.startsWith(HIDDEN_PREFIX)
    }

    private fun accept(name: String): Boolean {
        val fileNameLower = name.lowercase(Locale.US)
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
                        || fileNameLower.endsWith(".prn"))
    }

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