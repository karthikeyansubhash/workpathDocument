// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample

import android.content.Intent
import android.os.Bundle
import android.widget.HorizontalScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.hp.workpath.sample.emailsample.databinding.ActivityFileBrowserBinding
import com.hp.workpath.sample.emailsample.filebrowser.FileListFragment
import com.hp.workpath.sample.emailsample.filebrowser.FileListFragment.Companion.newInstance
import com.hp.workpath.sample.emailsample.filebrowser.FileUtils.PATH
import java.io.File

class FileBrowserActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener, FileListFragment.Callbacks {

    private var mPath: String? = null
    private lateinit var mBindingActivityFileBrowser: ActivityFileBrowserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBindingActivityFileBrowser = ActivityFileBrowserBinding.inflate(layoutInflater)
        setContentView(mBindingActivityFileBrowser.root)

        supportFragmentManager.addOnBackStackChangedListener(this)
        mPath = filesDir.path
        addFragment(mPath)
        setNavigationPath(mPath)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onBackStackChanged() {
        val count = supportFragmentManager.backStackEntryCount
        mPath = if (count > 0) {
            val fragment = supportFragmentManager.getBackStackEntryAt(count - 1)
            fragment.name
        } else {
            filesDir.path
        }
        setNavigationPath(mPath)
        invalidateOptionsMenu()
    }

    private fun addFragment(path: String?) {
        val fragment = newInstance(path)
        supportFragmentManager.beginTransaction()
                .add(R.id.mainFragmentContainer, fragment).commit()
    }

    private fun replaceFragment(file: File) {
        mPath = file.absolutePath
        val fragment = newInstance(mPath)
        supportFragmentManager.beginTransaction()
                .replace(R.id.mainFragmentContainer, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(mPath).commit()
    }

    private fun finishWithResult(file: File?) {
        if (file != null) {
            setResult(RESULT_OK, Intent().putExtra(PATH, file.absolutePath))
        } else {
            setResult(RESULT_CANCELED)
        }
        finish()
    }

    override fun onFileSelected(file: File?) {
        if (file != null) {
            if (file.isDirectory) {
                setNavigationPath(file.absolutePath)
                replaceFragment(file)
            } else {
                finishWithResult(file)
            }
        } else {
            Toast.makeText(this@FileBrowserActivity, R.string.error_select_file,
                    Toast.LENGTH_SHORT).show()
        }
    }

    private fun setNavigationPath(absolutePath: String?) {
        mBindingActivityFileBrowser.pathTextView.text = absolutePath
        mBindingActivityFileBrowser.horizontalScrollView.post { mBindingActivityFileBrowser.horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT) }
    }

    override fun onBackPressed() {
        val fm = supportFragmentManager
        if (fm.backStackEntryCount > 0) {
            fm.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}