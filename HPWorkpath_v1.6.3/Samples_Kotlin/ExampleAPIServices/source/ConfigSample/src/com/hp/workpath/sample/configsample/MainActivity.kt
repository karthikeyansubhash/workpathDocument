// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.configsample

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.security.keystore.KeyGenParameterSpec
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath
import com.hp.workpath.api.config.ConfigService.AbstractConfigChangeObserver
import com.hp.workpath.api.printer.PrintAttributes
import com.hp.workpath.api.printer.PrintAttributesCaps
import com.hp.workpath.sample.configsample.Logger.showResult
import com.hp.workpath.sample.configsample.databinding.ActivityMainBinding
import com.hp.workpath.sample.configsample.model.SimplePrintOption
import com.hp.workpath.sample.configsample.task.ConfigReaderTask
import com.hp.workpath.sample.configsample.task.ConfigUpdateTask
import com.hp.workpath.sample.configsample.task.InitializationTask
import com.hp.workpath.sample.configsample.task.LoadPrintCapabilitiesTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.security.GeneralSecurityException


/**
 * Main activity for Config Sample.
 */
class MainActivity : AppCompatActivity() {
    /* Background task for Workpath SDK API initialization */
    private lateinit var mConfigChangeObserver: ConfigChangeObserver

    private lateinit var mOptionListAdapter: OptionListAdapter
    private lateinit var mAlertDialog: AlertDialog
    private lateinit var mSnackBar: Snackbar
    private lateinit var configUpdateJob: Job
    private lateinit var mBindingActivityMain: ActivityMainBinding

    private var caps: PrintAttributesCaps? = null
    private val SCREEN_4_3_INCH = "Screen_4.3_Inch"
    private val SECRET_KEY = "secret"
    private val SECRET_FILE = "sensitive_data.txt"
    private var isSecretValueUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBindingActivityMain = ActivityMainBinding.inflate(layoutInflater)
        val tag = mBindingActivityMain.layout?.tag
        if (SCREEN_4_3_INCH.equals(tag)) {
            setSupportActionBar(mBindingActivityMain.toolbar)
        }
        setContentView(mBindingActivityMain.root)

        // find the text and button
        findViewElements()
        addListener()
        mConfigChangeObserver = ConfigChangeObserver(Handler(Looper.getMainLooper()))
    }

    override fun onResume() {
        super.onResume()

        // register ConfigChangeObserver to observe the event when application's config is changed
        mConfigChangeObserver.register(applicationContext)
        lifecycleScope.launch (Dispatchers.Default) {
            InitializationTask(this@MainActivity).execute()
        }
    }

    override fun onPause() {
        super.onPause()

        // unregister ConfigChangeObserver
        mConfigChangeObserver.unregister(applicationContext)
        if (this::mAlertDialog.isInitialized) {
            mAlertDialog.dismiss()
        }
        if (this::mSnackBar.isInitialized) {
            mSnackBar.dismiss()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.version, menu)
        val versionMenu = menu.findItem(R.id.menuVersion)
        try {
            val sdkInfo = Workpath.getInstance()
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            versionMenu.title = getString(R.string.version, pInfo.versionName, pInfo.longVersionCode.toInt(), sdkInfo.versionName, sdkInfo.versionCode)
        } catch (e: Exception) {
            handleException(e)
        }
        return true
    }

    private fun findViewElements() {
        mOptionListAdapter = OptionListAdapter(this)
        mBindingActivityMain.printOptionListView.adapter = mOptionListAdapter
    }

    private fun addListener() {
        mBindingActivityMain.updateConfigButton.setOnClickListener { handleSetValue() }
        mBindingActivityMain.getConfigButton.setOnClickListener {
            lifecycleScope.launch (Dispatchers.Default) {
                ConfigReaderTask(this@MainActivity, false).execute()
            }
        }
        mBindingActivityMain.configEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                mBindingActivityMain.updateConfigButton.isEnabled = !TextUtils.isEmpty(s)
            }
        })
    }

    private fun setPrintAttributes(option: SimplePrintOption?) {
        val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
        if (option != null) {
            val defaultJson = gson.toJson(option)
            mBindingActivityMain.printOptionTextView.text = defaultJson
            mOptionListAdapter.setItem(option)
            mOptionListAdapter.notifyDataSetChanged()
        }
    }

    fun setPrintCapabilities(caps: PrintAttributesCaps?) {
        this.caps = caps
    }

    /**
     * Handles value set operation.
     * It can do some checks and launch AsyncTask to set value in ConfigService
     */
    private fun handleSetValue() {
        if (mBindingActivityMain.configEditText.isEnabled) {
            if (::configUpdateJob.isInitialized && configUpdateJob.isActive) {
                configUpdateJob.cancel()
            }

            // disable editing while updating
            mBindingActivityMain.configEditText.isEnabled = false

            // start updating task with new configuration value
            configUpdateJob = lifecycleScope.launch (Dispatchers.Default) {
                ConfigUpdateTask(this@MainActivity, mBindingActivityMain.configEditText.text.toString()).execute()
            }
            showProgressBar(View.VISIBLE)
        }
    }

    fun showProgressBar(visibility: Int) {
        mBindingActivityMain.progressBar.visibility = visibility
    }

    fun handleComplete() {
        mBindingActivityMain.updateConfigButton.isEnabled = true
        lifecycleScope.launch (Dispatchers.Default) {
            LoadPrintCapabilitiesTask(this@MainActivity).execute()
            ConfigReaderTask(this@MainActivity, true).execute()
        }
    }

    fun setConfigComplete() {
        // enable editing
        mBindingActivityMain.configEditText.isEnabled = true
    }

    fun getConfigComplete(configJsonObject: JSONObject) {
        isSecretValueUpdate = isExistSecretComponent(configJsonObject)

        if (!isSecretValueUpdate) {
            val jsonObject = JsonParser().parse(configJsonObject.toString()) as JsonObject
            val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

            // enable editing
            mBindingActivityMain.configEditText.setText(gson.toJson(jsonObject))
        }

        val secretValue = loadSecretValue()
        mBindingActivityMain.secretEditText.setText(secretValue)
        if (TextUtils.isEmpty(secretValue)) {
            mBindingActivityMain.secretLayout.visibility = View.GONE
        } else {
            mBindingActivityMain.secretLayout.visibility = View.VISIBLE
        }
    }

    fun updatePrintOption(configJsonObject: JSONObject) {
        val simplePrintOption = SimplePrintOption()
        try {
            val paperSize = configJsonObject.getString("paperSize")
            if (!TextUtils.isEmpty(paperSize)) {
                val psz = PrintAttributes.PaperSize.valueOf(paperSize.uppercase())
                caps?.let {
                    if (it.paperSizeList.contains(psz)) {
                        simplePrintOption.paperSize = psz.name
                    }
                }
            }
        } catch (t: Throwable) {
            if (applicationContext != null) {
                Logger.showResult(this, getString(R.string.not_supported, "paperSize") + " " + t.message)
            }
        }
        try {
            val colorMode = configJsonObject.getString("colorMode")
            if (!TextUtils.isEmpty(colorMode)) {
                val color = PrintAttributes.ColorMode.valueOf(colorMode.uppercase())
                caps?.let {
                    if (it.colorModeList.contains(color)) {
                        simplePrintOption.colorMode = color.name
                    }
                }
            }
        } catch (t: Throwable) {
            if (applicationContext != null) {
                Logger.showResult(this, getString(R.string.not_supported, "colorMode") + " " + t.message)
            }
        }
        try {
            val copies = configJsonObject.getInt("copies")
            caps?.let {
                if (copies > 0 && copies <= it.maxCopies) {
                    simplePrintOption.copies = copies
                } else {
                    if (applicationContext != null) {
                        Logger.showResult(this, getString(R.string.not_supported, "copies: $copies"))
                    }
                }
            }
        } catch (e: JSONException) {
            if (applicationContext != null) {
                Logger.showResult(this, getString(R.string.not_supported, "copies") + " " + e.message)
            }
        }
        try {
            mBindingActivityMain.urlEditText.setText(configJsonObject.getString("url"))
        } catch (e: JSONException) {
            if (applicationContext != null) {
                Logger.showResult(this, getString(R.string.not_supported, "url") + " " + e.message)
            }
        }
        setPrintAttributes(simplePrintOption)
    }

    private fun isExistSecretComponent(jsonObject: JSONObject) : Boolean {
        /**
         * This source code is one of example that how configuration can be used various way.
         */
        if (jsonObject.has(SECRET_KEY)) {
            try {
                val secretValue = jsonObject.getString(SECRET_KEY)
                jsonObject.remove(SECRET_KEY)

                configUpdateJob = lifecycleScope.launch (Dispatchers.Default) {
                    ConfigUpdateTask(this@MainActivity, jsonObject.toString()).execute()
                }
                saveSecretValue(secretValue)
                Logger.showResult(this, getString(R.string.secret_value))
                return true
            } catch (e: JSONException) {
                if (applicationContext != null) {
                    Logger.showResult(this, getString(R.string.not_supported, SECRET_KEY) + " " + e.message)
                }
            }
        }
        return false
    }

    private fun saveSecretValue(secretValue: String) {
        try {
            val keyGenParameterSpec: KeyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
            val mainKeyAlias: String = MasterKeys.getOrCreate(keyGenParameterSpec)
            val secretFile = File(filesDir, SECRET_FILE)
            if (secretFile.exists()) {
                secretFile.delete()
            }

            val encryptedFile = EncryptedFile.Builder(
                secretFile,
                this@MainActivity,
                mainKeyAlias,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            val fileContent: ByteArray = secretValue.toByteArray(StandardCharsets.UTF_8)
            encryptedFile.openFileOutput().apply {
                write(fileContent)
                flush()
                close()
            }
        } catch (e: GeneralSecurityException) {
            if (applicationContext != null) {
                showResult(this@MainActivity, e.message)
            }
        } catch (e: IOException) {
            if (applicationContext != null) {
                showResult(this@MainActivity, e.message)
            }
        }
    }

    private fun loadSecretValue(): String? {
        try {
            val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
            val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

            val file = File(filesDir, SECRET_FILE)

            if (!file.exists())
                return null

            val encryptedFile = EncryptedFile.Builder(
                file,
                this@MainActivity,
                mainKeyAlias,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            val inputStream: InputStream = encryptedFile.openFileInput()
            val byteArrayOutputStream = ByteArrayOutputStream()
            var nextByte: Int = inputStream.read()
            while (nextByte != -1) {
                byteArrayOutputStream.write(nextByte)
                nextByte = inputStream.read()
            }
            return byteArrayOutputStream.toString()
        } catch (e: GeneralSecurityException) {
            if (applicationContext != null) {
                showResult(this@MainActivity, e.message)
            }
        } catch (e: IOException) {
            if (applicationContext != null) {
                showResult(this@MainActivity, e.message)
            }
        }
        return null
    }

    /**
     * Receives notification about config update.
     */
    private inner class ConfigChangeObserver(handler: Handler?) : AbstractConfigChangeObserver(handler) {
        override fun onChange(updatedData: JSONObject) {
            if (!isSecretValueUpdate) {
                showSnackBar("ConfigService onChange()")
                mAlertDialog = AlertDialog.Builder(this@MainActivity)
                    .setTitle(getString(R.string.config_observer))
                    .setMessage(getString(R.string.config_change))
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        getConfigComplete(updatedData)
                        updatePrintOption(updatedData)
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        getConfigComplete(updatedData)
                        dialog.dismiss()
                    }
                    .show()
            } else {
                isSecretValueUpdate = false
                getConfigComplete(updatedData)
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
        showProgressBar(View.GONE)
        mBindingActivityMain.updateConfigButton.isEnabled = false

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

    fun showSnackBar(text: String) {
        runOnUiThread {
            if (!::mSnackBar.isInitialized) {
                mSnackBar = Snackbar.make(mBindingActivityMain.container, "", Snackbar.LENGTH_INDEFINITE)
                val snackBarView = mSnackBar.view
                val tv = snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                tv?.maxLines = 3
            }
            mSnackBar.run {
                setText(text)
                setActionTextColor(ContextCompat.getColor(view.context, R.color.snackbar_button_color))
                setAction(getString(R.string.ok)) { mSnackBar.dismiss() }
                show()
            }
        }
    }

    companion object {
        const val TAG = "[SAMPLE]" + "Config"
    }
}