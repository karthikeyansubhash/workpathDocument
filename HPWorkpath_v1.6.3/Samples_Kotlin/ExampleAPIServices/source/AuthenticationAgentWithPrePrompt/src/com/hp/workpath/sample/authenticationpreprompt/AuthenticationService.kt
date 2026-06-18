// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authenticationpreprompt

import android.util.Log
import com.hp.workpath.api.Result
import com.hp.workpath.api.access.AbstractAuthenticationService
import com.hp.workpath.api.access.AccessService
import com.hp.workpath.api.access.AuthenticationAttributes
import com.hp.workpath.api.access.Principal
import com.hp.workpath.api.access.SignInAction
import com.hp.workpath.api.access.UserOverridesAttributes
import com.hp.workpath.api.access.UserPreferencesAttributes
import com.hp.workpath.api.access.AuthenticationAttributes.WindowsBuilder
import com.hp.workpath.sample.authenticationpreprompt.task.InitializationTask
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutionException

class AuthenticationService : AbstractAuthenticationService() {
    /* Background task for Workpath SDK API initialization */
    private lateinit var mInitializationTask: Job
    private var isInitializedSDK = false
    override fun onCreate() {
        super.onCreate()
        initializedSDK()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mInitializationTask.isInitialized) {
            mInitializationTask.cancel()
        }
    }

    override fun onSignIn(principal: Principal) {
        Log.i(TAG, "Received sign in event: " + principal.username)
    }

    override fun onSignOut() {
        Log.i(TAG, "Received sign out event")
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onPrePrompt() {
        try {
            GlobalScope.launch(Dispatchers.Main) {
                Log.i(TAG, "Received onPrePrompt event")
                if (isInitializedSDK) {
                    val result = Result()
                    val signInAction = SignInAction(SignInAction.Action.SUCCESS, null)
                    AccessService.signIn(this@AuthenticationService, signInAction, windowsData, result)
                    if (result.code != Result.RESULT_OK) {
                        showLogResult("AccessService.signIn", result)
                        val failAction = SignInAction(SignInAction.Action.FAIL, result.cause)
                        AccessService.signIn(this@AuthenticationService, failAction, null, null)
                    } else {
                        showLogResult("AccessService.signIn", result)
                    }
                }
            }
        } catch (t: Throwable) {
            showLogResult("AccessService.signIn ${t.message}")
        }
    }

    @get:Throws(Exception::class)
    private val windowsData: AuthenticationAttributes
        get() {
            val id = "Tester"
            val password = "password"
            val userOverridesAttributes = UserOverridesAttributes.Builder()
                    .addBccAddress(getString(R.string.bcc_address_email_01), getString(R.string.bcc_address_name_01))
                    .addBccAddress(getString(R.string.bcc_address_email_02), getString(R.string.bcc_address_name_02))
                    .addCcAddress(getString(R.string.cc_address_email_01), getString(R.string.cc_address_name_01))
                    .addCcAddress(getString(R.string.cc_address_email_02), getString(R.string.cc_address_name_02))
                    .setFrom(getString(R.string.from_address_email), getString(R.string.from_address_name))
                    .addToAddress(getString(R.string.to_address_email_01), getString(R.string.to_address_name_01))
                    .addToAddress(getString(R.string.to_address_email_02), getString(R.string.to_address_name_02))
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
                    .addUserProperty(getString(R.string.value_user_property_key_01), getString(R.string.value_user_property_value_01))
                    .addUserProperty(getString(R.string.value_user_property_key_02), getString(R.string.value_user_property_value_02))
                    .setUserOverridesAttributes(userOverridesAttributes)
                    .setUserPreferencesAttributes(userPreferencesAttributes)
                    .build()
        }

    @OptIn(DelicateCoroutinesApi::class)
    private fun initializedSDK() {//AccessoryAgentSample 참조
        Log.i(TAG, "Received initializedSDK")
        try {
            if (!isInitializedSDK) {
                mInitializationTask = GlobalScope.launch(Dispatchers.Main) {
                    val initStatus = InitializationTask(applicationContext, null).execute()
                    if (initStatus == InitializationTask.InitStatus.NO_ERROR) {
                        isInitializedSDK = true
                        Log.i(TAG, "Received initializedSDK true")
                    } else {
                        Log.e(TAG, getString(R.string.sdk_support_missing))
                    }
                }
            }
        } catch (ie: InterruptedException) {
            showLogResult("Workpath.getInstance().initialize ${ie.message}")
        } catch (ee: ExecutionException) {
            showLogResult("Workpath.getInstance().initialize ${ee.message}")
        }
    }

    private fun showLogResult(msg: String) {
        showLogResult(msg, null)
    }

    private fun showLogResult(msg: String, result: Result?) {
        val message = if (result != null) "$msg\n${Logger.build(result)}" else msg
        Log.d(TAG, message)
    }

    companion object {
        const val TAG = AuthenticationActivity.TAG
    }
}