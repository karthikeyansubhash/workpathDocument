// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authentication

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hp.workpath.api.Result
import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath
import com.hp.workpath.api.access.*
import com.hp.workpath.api.access.AuthenticationAttributes.WindowsBuilder
import com.hp.workpath.sample.authentication.databinding.ActivitySignInBinding
import com.hp.workpath.sample.authentication.databinding.LayoutManualZoneBinding
import com.hp.workpath.sample.authentication.task.InitializationTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthenticationActivity : AppCompatActivity() {

    private var isInitializedSDK = false
    private lateinit var mAlertDialog: AlertDialog

    private val SCREEN_4_3_INCH = "Screen_4.3_Inch"
    private lateinit var mBindingActivitySignIn: ActivitySignInBinding
    private lateinit var mBindingLayoutManualZone: LayoutManualZoneBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBindingActivitySignIn = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(mBindingActivitySignIn.root)
        mBindingLayoutManualZone = LayoutManualZoneBinding.bind(mBindingActivitySignIn.root)
        initView()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.Default) {
            InitializationTask(this@AuthenticationActivity).execute()
        }
    }

    override fun onPause() {
        super.onPause()
        if (this::mAlertDialog.isInitialized) {
            mAlertDialog.dismiss()
        }
    }

    private fun initView() {
        mBindingActivitySignIn.signInButton.setOnClickListener(buttonClickListener)
        mBindingActivitySignIn.failButton.setOnClickListener(buttonClickListener)
        mBindingLayoutManualZone.runButton.setOnClickListener(manualClickListener)
        mBindingActivitySignIn.homeButton.setOnClickListener(buttonClickListener)
        mBindingActivitySignIn.backButton.setOnClickListener(buttonClickListener)
        mBindingLayoutManualZone.generateButton.setOnClickListener(
            manualClickListener
        )
        setVersion()
    }

    private fun setVersion() {
        if (!SCREEN_4_3_INCH.equals(mBindingActivitySignIn.container.tag)) {
            try {
                val sdkInfo = Workpath.getInstance()
                val pInfo = packageManager.getPackageInfo(packageName, 0)
                mBindingActivitySignIn.versionTextView!!.text = getString(R.string.version, pInfo.versionName, pInfo.longVersionCode.toInt(), sdkInfo.versionName, sdkInfo.versionCode)
            } catch (t: Throwable) {
                handleException(t)
            }
        }
    }

    private var manualClickListener = View.OnClickListener { v ->
        if (TextUtils.isEmpty(mBindingActivitySignIn.idEditText.text.toString())) {
            mBindingActivitySignIn.idEditText.setText("Tester")
        }
        try {
            if (v === mBindingLayoutManualZone.runButton) {
                if (TextUtils.isEmpty(mBindingLayoutManualZone.resultEditText.text.toString())) {
                    Toast.makeText(
                        this@AuthenticationActivity,
                        "Please fill user information for sign in.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val data =
                        mBindingLayoutManualZone.resultEditText.text.toString()
                    val generatedData = Gson().fromJson(data, AuthenticationAttributes::class.java)
                    val result = Result()
                    val signInAction = SignInAction(SignInAction.Action.SUCCESS, null)
                    AccessService.signIn(
                        this@AuthenticationActivity,
                        signInAction,
                        generatedData,
                        result
                    )
                    Logger.showResult(this, "AccessService.signIn", result)
                }
            } else if (v === mBindingLayoutManualZone.generateButton) {
                val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
                mBindingLayoutManualZone.resultEditText.setText(
                    gson.toJson(
                        windowsData
                    )
                )
            }
        } catch (t: Throwable) {
            Logger.showResult(this, "AccessService.signIn ${t.message}")
        }
    }

    private var buttonClickListener = View.OnClickListener { v ->
        if (TextUtils.isEmpty(mBindingActivitySignIn.idEditText.text.toString())) {
            mBindingActivitySignIn.idEditText.setText("Tester")
        }
        try {
            val result = Result()
            when (v) {
                mBindingActivitySignIn.signInButton -> {
                    val signInAction = SignInAction(SignInAction.Action.SUCCESS, null)
                    AccessService.signIn(
                        this@AuthenticationActivity,
                        signInAction,
                        windowsData,
                        result
                    )
                }
                mBindingActivitySignIn.failButton -> {
                    val failureMessage = "This is failure message from AuthenticationAgent Sample."
                    val signInAction = SignInAction(SignInAction.Action.FAIL, failureMessage)
                    AccessService.signIn(this@AuthenticationActivity, signInAction, null, result)
                }
                mBindingActivitySignIn.homeButton -> {
                    val signInAction = SignInAction(SignInAction.Action.HOME, null)
                    AccessService.signIn(this@AuthenticationActivity, signInAction, null, result)
                }
                mBindingActivitySignIn.backButton -> {
                    val signInAction = SignInAction(SignInAction.Action.BACK, null)
                    AccessService.signIn(this@AuthenticationActivity, signInAction, null, result)
                }
            }
            if (result.code != Result.RESULT_OK) {
                Logger.showResult(this, "AccessService.signIn", result)
            }
        } catch (t: Throwable) {
            Logger.showResult(this, "AccessService.signIn ${t.message}")
        }
    }

    @get:Throws(Exception::class)
    private val windowsData: AuthenticationAttributes
        get() {
            val id = mBindingActivitySignIn.idEditText.text.toString()
            val password = mBindingActivitySignIn.passwordEditText.text.toString()
            val userOverridesAttributes = UserOverridesAttributes.Builder()
                .addBccAddress(
                    getString(R.string.bcc_address_email_01),
                    getString(R.string.bcc_address_name_01)
                )
                .addBccAddress(
                    getString(R.string.bcc_address_email_02),
                    getString(R.string.bcc_address_name_02)
                )
                .addCcAddress(
                    getString(R.string.cc_address_email_01),
                    getString(R.string.cc_address_name_01)
                )
                .addCcAddress(
                    getString(R.string.cc_address_email_02),
                    getString(R.string.cc_address_name_02)
                )
                .setFrom(
                    getString(R.string.from_address_email),
                    getString(R.string.from_address_name)
                )
                .addToAddress(
                    getString(R.string.to_address_email_01),
                    getString(R.string.to_address_name_01)
                )
                .addToAddress(
                    getString(R.string.to_address_email_02),
                    getString(R.string.to_address_name_02)
                )
                .setMessage(getString(R.string.email_message))
                .setSubject(getString(R.string.email_subject))
                .setFaxBillingCode(getString(R.string.fax_billing_code))
                .setFaxCompanyName(getString(R.string.fax_company_name))
                .build()
            val userPreferencesAttributes = UserPreferencesAttributes.Builder()
                .setAutoLaunchAppAccessPointId(getString(R.string.app_access_point_id))
                .setLanguageCode(getString(R.string.language_code))
                .build()
            return WindowsBuilder()
                .setFullyQualifiedName(getString(R.string.value_fully_qualified_name, id))
                .setDisplayName(id)
                .setPassword(password)
                .setUserDomain(getString(R.string.value_domain))
                .setUserEmail(getString(R.string.value_user_email, id))
                .setUserName(id)
                .setUserPrincipalName(id)
                .setHomeFolderPath(getString(R.string.value_home_folder_path))
                .addUserProperty(
                    getString(R.string.value_user_property_key_01),
                    getString(R.string.value_user_property_value_01)
                )
                .addUserProperty(
                    getString(R.string.value_user_property_key_02),
                    getString(R.string.value_user_property_value_02)
                )
                .setUserOverridesAttributes(userOverridesAttributes)
                .setUserPreferencesAttributes(userPreferencesAttributes)
                .build()
        }

    fun handleComplete() {
        isInitializedSDK = true
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
                SsdkUnsupportedException.LIBRARY_NOT_INSTALLED, SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED -> getString(
                    R.string.sdk_support_missing
                )
                else -> getString(R.string.unknown_error)
            }
        } else {
            t?.message?.run {
                errorMsg = this
            }
        }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (SCREEN_4_3_INCH.equals(mBindingActivitySignIn.container.tag)) {
            menuInflater.inflate(R.menu.version, menu)
            val versionMenu = menu?.findItem(R.id.menuVersion)
            try {
                val sdkInfo = Workpath.getInstance()
                val pInfo = packageManager.getPackageInfo(packageName, 0)
                versionMenu?.title = getString(
                    R.string.version,
                    pInfo.versionName,
                    pInfo.longVersionCode.toInt(),
                    sdkInfo.versionName,
                    sdkInfo.versionCode
                )
            } catch (t: Throwable) {
                handleException(t)
            }
        }
        return true
    }

    companion object {
        const val TAG = "[SAMPLE]" + "AuthAgent"
    }
}