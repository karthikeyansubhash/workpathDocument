// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath
import com.hp.workpath.api.massstorage.CustomerDataFile
import com.hp.workpath.api.massstorage.MassStorageInfo
import com.hp.workpath.api.massstorage.MassStorageService.AbstractMassStorageChangeObserver
import com.hp.workpath.sample.massstoragesample.FileListAdapter.FileListOnClickListener
import com.hp.workpath.sample.massstoragesample.Logger.showResult
import com.hp.workpath.sample.massstoragesample.databinding.ActivityMainBinding
import com.hp.workpath.sample.massstoragesample.fragments.FileCreateFragment
import com.hp.workpath.sample.massstoragesample.fragments.FileInfoFragment.Companion.newInstance
import com.hp.workpath.sample.massstoragesample.fragments.FileRenameFragment
import com.hp.workpath.sample.massstoragesample.fragments.ISelectedStorageListener
import com.hp.workpath.sample.massstoragesample.fragments.NewFileFragment
import com.hp.workpath.sample.massstoragesample.fragments.StorageListFragment
import com.hp.workpath.sample.massstoragesample.model.FileInfo
import com.hp.workpath.sample.massstoragesample.model.StorageInfo
import com.hp.workpath.sample.massstoragesample.task.DeleteFileTask
import com.hp.workpath.sample.massstoragesample.task.GetStorageListTask
import com.hp.workpath.sample.massstoragesample.task.InitializationTask
import java.util.EnumMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList
import kotlin.collections.set

class MainActivity : AppCompatActivity(), ISelectedStorageListener {

    /**
     * Map [StorageInfo]
     * Store references to summary TextViews to provide information
     */

    private val mSummaries = EnumMap<StorageInfo, TextView>(StorageInfo::class.java)

    private lateinit var mFileListAdapter: FileListAdapter

    private lateinit var mSnackBar: Snackbar
    private lateinit var mAlertDialog: AlertDialog
    private lateinit var mMassStorageChangeObserver: MassStorageChangeObserver

    private lateinit var massStorageInfoList: MutableList<MassStorageInfo>
    private var currentSelected = 0

    private var baseCustomerDataFile: CustomerDataFile? = null

    private lateinit var mBindingActivityMain: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBindingActivityMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBindingActivityMain.root)
        // find the text and button
        findViewElements()

        // add click listener to call the MFP
        addListener()

        mMassStorageChangeObserver = MassStorageChangeObserver(Handler(Looper.getMainLooper()))
    }

    override fun onResume() {
        super.onResume()
        mBindingActivityMain.container.isEnabled = false

        // register MassStorageChangeObserver to receive mass storage change callback
        mMassStorageChangeObserver.register(applicationContext)

        lifecycleScope.launch(Dispatchers.Default) {
            InitializationTask(this@MainActivity).execute()
        }
    }

    override fun onPause() {
        super.onPause()

        // unregister MassStorageChangeObserver
        mMassStorageChangeObserver.unregister(applicationContext)

        if (this::mAlertDialog.isInitialized) {
            mAlertDialog.dismiss()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.version, menu)
        val versionMenu = menu.findItem(R.id.menuVersion)
        try {
            val sdkInfo = Workpath.getInstance()
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            versionMenu.title = getString(R.string.version, pInfo.versionName, pInfo.longVersionCode.toInt(), sdkInfo.versionName, sdkInfo.versionCode)
        } catch (t: Throwable) {
            handleException(t)
        }
        return true
    }

    private fun findViewElements() {
        (findViewById<View>(R.id.headerStorageInformation)
                .findViewById<View>(R.id.headerTextView) as TextView).text = getString(R.string.storage_information)
        for (item in StorageInfo.values()) {
            val itemLayout = findViewById<ViewGroup>(item.itemId)
            (itemLayout.findViewById<View>(R.id.titleTextView) as TextView).setText(item.titleId)
            mSummaries[item] = itemLayout.findViewById<View>(R.id.summaryTextView) as TextView
        }

        mFileListAdapter = FileListAdapter(fileListOnClickListener)
        mBindingActivityMain.fileListView.layoutManager = LinearLayoutManager(this)
        mBindingActivityMain.fileListView.adapter = mFileListAdapter
    }

    private var fileListOnClickListener: FileListOnClickListener = object : FileListOnClickListener {
        override fun onDeleteButton(enable: Boolean) {
            mBindingActivityMain.deleteButton.isEnabled = enable
        }

        override fun onRenameButton(enable: Boolean) {
            mBindingActivityMain.renameButton.isEnabled = enable
        }

        override fun onItemClick(view: View) {
            val holder = mBindingActivityMain.fileListView.getChildViewHolder(view)
            val position = holder.adapterPosition

            val selectedItem = mFileListAdapter.getItem(position).file
            if (selectedItem.isRoot || selectedItem.isDirectory) {
                // move to selected folder
                displayFileList(selectedItem)
            } else {
                val fileInfoFragment = newInstance(selectedItem)
                fileInfoFragment.show(supportFragmentManager, "dialog")
            }
        }
    }

    private fun addListener() {
        mBindingActivityMain.newFileButton.setOnClickListener {
            if (massStorageInfoList.isNotEmpty()) {
                val newFileFragment = NewFileFragment.newInstance(massStorageInfoList[currentSelected], baseCustomerDataFile)
                newFileFragment.show(supportFragmentManager, "dialog")
            } else {
                if (applicationContext != null) {
                    Logger.showResult(this@MainActivity, getString(R.string.not_loaded_storage_list))
                }
            }
        }
        mBindingActivityMain.createButton.setOnClickListener {
            if (massStorageInfoList.isNotEmpty()) {
                val fileCreateFragment = FileCreateFragment.newInstance(massStorageInfoList[currentSelected], baseCustomerDataFile)
                fileCreateFragment.show(supportFragmentManager, "dialog")
            } else {
                if (applicationContext != null) {
                    Logger.showResult(this@MainActivity, getString(R.string.not_loaded_storage_list))
                }
            }
        }
        mBindingActivityMain.renameButton.setOnClickListener(View.OnClickListener {
            if (massStorageInfoList.isNotEmpty()) {
                val checkedList: BooleanArray? = mFileListAdapter.getCheckedItemArray()
                checkedList?.let {
                    for (x in it.indices) {
                        if (it[x]) {
                            val fileRenameFragment = FileRenameFragment.newInstance(massStorageInfoList[currentSelected], mFileListAdapter.getItem(x).file)
                            fileRenameFragment.show(supportFragmentManager, "dialog")
                            return@OnClickListener
                        }
                    }
                }
            } else {
                if (applicationContext != null) {
                    Logger.showResult(this@MainActivity, getString(R.string.not_loaded_storage_list))
                }
            }
        })
        mBindingActivityMain.deleteButton.setOnClickListener {
            if (massStorageInfoList.isNotEmpty()) {
                var selectedItemList: MutableList<CustomerDataFile> = ArrayList()
                val checkedItemArray = mFileListAdapter.getCheckedItemArray()
                checkedItemArray?.let {
                    for (x in it.indices) {
                        if (it[x]) {
                            selectedItemList.add(mFileListAdapter.getItem(x).file)
                        }
                    }
                }
                showProgress(View.VISIBLE)
                lifecycleScope.launch(Dispatchers.Default) {
                    baseCustomerDataFile?.let {
                        DeleteFileTask(this@MainActivity, selectedItemList, it).execute()
                    }
                }
            } else {
                if (applicationContext != null) {
                    Logger.showResult(this@MainActivity, getString(R.string.not_loaded_storage_list))
                }
            }
        }
        mBindingActivityMain.childStorageList.selectButton.setOnClickListener {
            getStorageList()
        }
        mBindingActivityMain.refreshButton.setOnClickListener { displayFileList(baseCustomerDataFile) }
    }

    fun handleComplete() {
        if (applicationContext != null) {
            showResult(this@MainActivity, getString(R.string.initialized))
        }
    }

    private fun getStorageList() {
        showProgress(View.VISIBLE)
        mBindingActivityMain.container.isEnabled = true
        lifecycleScope.launch(Dispatchers.Default) {
            GetStorageListTask(this@MainActivity).execute()
        }
    }

    fun showProgress(visibility: Int) {
        mBindingActivityMain.progressBar.visibility = visibility
    }

    fun loadStorage(massStorageInfo: MassStorageInfo) {
        displayStorageInfo(massStorageInfo)
        displayFileList(CustomerDataFile(this, massStorageInfo, "/"))
    }

    fun loadStorageList(massStorageInfoList: MutableList<MassStorageInfo>) {
        this.massStorageInfoList = massStorageInfoList
        if (massStorageInfoList.isNotEmpty()) {
            val storageDialog = StorageListFragment.newInstance(massStorageInfoList)
            storageDialog.show(supportFragmentManager, "dialog")
        }
    }

    fun displayFileList(customerDataFile: CustomerDataFile?) {
        if (::mFileListAdapter.isInitialized) {
            mFileListAdapter.clear()
        }

        if (customerDataFile != null) {
            baseCustomerDataFile = customerDataFile
            val path = customerDataFile.path

            Log.i(TAG, "CustomerDataFile: " + Logger.build(customerDataFile))
            val listFiles = customerDataFile.listFiles()
            val parentCustomerDataFile = customerDataFile.parentFile

            val fileInfoList: MutableList<FileInfo> = ArrayList()
            if (parentCustomerDataFile != null) {
                val parentFileInfo: FileInfo = createRootFolder(parentCustomerDataFile)
                fileInfoList.add(parentFileInfo)
            }

            for (cdf in listFiles) {
                fileInfoList.add(FileInfo(cdf!!))
            }
            if (fileInfoList.size == 0) {
                mBindingActivityMain.noFileTextView.visibility = View.VISIBLE
            } else {
                mBindingActivityMain.noFileTextView.visibility = View.GONE
                mFileListAdapter.setListItems(fileInfoList)
            }

            mBindingActivityMain.pathTextView.text = path
            mBindingActivityMain.createButton.isEnabled = true
            mBindingActivityMain.newFileButton.isEnabled = true
            mBindingActivityMain.renameButton.isEnabled = false
            mBindingActivityMain.deleteButton.isEnabled = false
        } else {
            Logger.showResult(this, "CustomerData is null")
        }
    }

    private fun createRootFolder(file: CustomerDataFile): FileInfo {
        val fileInfo = FileInfo(file)
        fileInfo.fileName = FileListAdapter.BACK
        return fileInfo
    }

    override fun selectedStorage(index: Int) {
        currentSelected = index
        loadStorage(massStorageInfoList[index])
    }

    private fun displayStorageInfo(massStorageInfo: MassStorageInfo?) {
        for (item in mSummaries.keys) {
            when (item.itemId) {
                R.id.childStorageList, R.id.childName -> mSummaries[item]?.text = massStorageInfo?.run { name }
                        ?: ""
                R.id.childExternalFileDirectory -> mSummaries[item]?.text = massStorageInfo?.run { externalFileDirectory }
                        ?: ""
                R.id.childFreeSpace -> mSummaries[item]?.text = massStorageInfo?.run { freeSpace.toString() }
                        ?: ""
                R.id.childProtocol -> mSummaries[item]?.text = massStorageInfo?.run { protocol.toString() }
                        ?: ""
                R.id.childTotalSpace -> mSummaries[item]?.text = massStorageInfo?.run { totalSpace.toString() }
                        ?: ""
                R.id.childType -> mSummaries[item]?.text = massStorageInfo?.run { type.toString() }
                        ?: ""
                R.id.childMounted -> mSummaries[item]?.text = massStorageInfo?.run { isMounted.toString() }
                        ?: ""
                R.id.childVolumeName -> mSummaries[item]?.text = massStorageInfo?.run { volumeName }
                        ?: ""
            }
        }
    }

    /**
     * Exception in could be because of following reasons
     *
     *  1. Library is not installed
     *  2. Library update is needed
     *  3. Version issue, unsupported
     *
     */
    fun handleException(t: Throwable?) {
        var errorMsg = ""
        if (t is SsdkUnsupportedException) {
            errorMsg = when (t.type) {
                SsdkUnsupportedException.LIBRARY_NOT_INSTALLED, SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED -> getString(R.string.sdk_support_missing)
                else -> getString(R.string.unknown_error)
            }
        } else {
            t?.message?.run {
                errorMsg = this
            }
        }
        Log.e(TAG, errorMsg)
        mAlertDialog = AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(errorMsg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                .show()
    }

    /**
     * Receives notification when MassStorage is attached or detached.
     */
    private inner class MassStorageChangeObserver(handler: Handler?) : AbstractMassStorageChangeObserver(handler) {
        override fun onChange() {
            Log.d(TAG, "Received onChange()")
            showSnackBar(getString(R.string.onchange_event_message))
            cleanAllViews()
        }
    }

    private fun cleanAllViews() {
        displayStorageInfo(null)
        massStorageInfoList.clear()
        baseCustomerDataFile = null
        mFileListAdapter.clear()
        mBindingActivityMain.noFileTextView.visibility = View.VISIBLE
    }

    fun enableButton(enable: Boolean) {
        mBindingActivityMain.createButton.isEnabled = enable
        mBindingActivityMain.newFileButton.isEnabled = enable
    }

    private fun showSnackBar(text: String) {
        runOnUiThread {
            if (!::mSnackBar.isInitialized) {
                mSnackBar = Snackbar.make(mBindingActivityMain.container, "", Snackbar.LENGTH_INDEFINITE)
                val snackBarView = mSnackBar.view
                val tv = snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                tv?.maxLines = 3
            }
            mSnackBar.run {
                setText(text)
                setActionTextColor(ContextCompat.getColor(context, R.color.snackbar_button_color))
                setAction(getString(R.string.ok)) { mSnackBar.dismiss() }
                show()
            }
        }
    }

    companion object {
        const val TAG = "[SAMPLE]" + "MassStorage"
    }
}