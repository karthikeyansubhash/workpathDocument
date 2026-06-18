// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accesssample.task

import android.view.View
import com.hp.workpath.api.Result
import com.hp.workpath.api.Workpath
import com.hp.workpath.api.access.AccessService
import com.hp.workpath.api.access.Principal
import com.hp.workpath.sample.accesssample.Logger
import com.hp.workpath.sample.accesssample.MainActivity
import com.hp.workpath.sample.accesssample.R
import com.hp.workpath.sample.accesssample.model.UserDetails
import com.hp.workpath.sample.accesssample.model.UserDetails.DOMAIN
import com.hp.workpath.sample.accesssample.model.UserDetails.FULLY_QUALIFIED_NAME
import com.hp.workpath.sample.accesssample.model.UserDetails.VERSION
import com.hp.workpath.sample.accesssample.model.UserDetails.IS_ADMIN
import com.hp.workpath.sample.accesssample.model.UserDetails.IS_AUTHENTICATED
import com.hp.workpath.sample.accesssample.model.UserDetails.IS_DEVICE_USER
import com.hp.workpath.sample.accesssample.model.UserDetails.IS_GUEST_USER
import com.hp.workpath.sample.accesssample.model.UserDetails.IS_HP_CLOUD_USER
import com.hp.workpath.sample.accesssample.model.UserDetails.IS_SERVICE_USER
import com.hp.workpath.sample.accesssample.model.UserDetails.IS_SMART_CARD_USER
import com.hp.workpath.sample.accesssample.model.UserDetails.PASSWORD
import com.hp.workpath.sample.accesssample.model.UserDetails.PRINCIPAL_ID
import com.hp.workpath.sample.accesssample.model.UserDetails.PROVIDER
import com.hp.workpath.sample.accesssample.model.UserDetails.PROVIDER_UUID
import com.hp.workpath.sample.accesssample.model.UserDetails.SIMPLE_AUTHORITIES
import com.hp.workpath.sample.accesssample.model.UserDetails.USER_EMAIL
import com.hp.workpath.sample.accesssample.model.UserDetails.USER_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.util.EnumMap


class UserDetailsReaderTask(context: MainActivity) {

    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private var mThrowable: Throwable? = null
    private val result: Result = Result()

    suspend fun execute() {
        val values: MutableMap<UserDetails, String?> = EnumMap(UserDetails::class.java)
        var currentUser: Principal? = null
        try {
            mContextRef.get()?.run {
                values[VERSION] = resources.getString(R.string.na)
                values[FULLY_QUALIFIED_NAME] = resources.getString(R.string.na)
                values[PRINCIPAL_ID] = resources.getString(R.string.na)
                values[DOMAIN] = resources.getString(R.string.na)
                values[PROVIDER] = resources.getString(R.string.na)
                values[USER_NAME] = resources.getString(R.string.na)
                values[PASSWORD] = resources.getString(R.string.na)
                values[USER_EMAIL] = resources.getString(R.string.na)
                values[IS_AUTHENTICATED] = resources.getString(R.string.na)
                values[SIMPLE_AUTHORITIES] = resources.getString(R.string.na)
                values[IS_HP_CLOUD_USER] = resources.getString(R.string.na)
                values[VERSION] = resources.getString(R.string.version,
                        Workpath.getInstance().versionName,
                        Workpath.getInstance().versionCode)

                // get user principal information using AccessService API
                currentUser = AccessService.getCurrentPrincipal(this.applicationContext, result)

                // first check whether Result is fine or not
                if (result.code == Result.RESULT_OK) {
                    currentUser?.run {
                        values[FULLY_QUALIFIED_NAME] = fullyQualifiedName
                        values[PRINCIPAL_ID] = principalId
                        values[DOMAIN] = domain
                        values[PROVIDER] = provider
                        values[USER_NAME] = username
                        values[PASSWORD] = password
                        values[USER_EMAIL] = userEmail
                        values[IS_ADMIN] = isAdmin.toString()
                        values[IS_AUTHENTICATED] = isAuthenticated.toString()
                        values[IS_HP_CLOUD_USER] = isHPCloudUser.toString()
                        values[IS_GUEST_USER] = isGuestUser.toString()
                        values[IS_DEVICE_USER] = isDeviceUser.toString()
                        values[IS_SERVICE_USER] = isServiceUser.toString()
                        values[IS_SMART_CARD_USER] = isSmartCardUser.toString()
                        values[PROVIDER_UUID] = providerUUID?.toString()
                        values[SIMPLE_AUTHORITIES] = simpleAuthorities?.toString()
                    }
                }
            }
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(values, currentUser)
    }

    private suspend fun onPostExecute(userResult: Map<UserDetails, String?>, currentUser: Principal?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                showProgress(View.GONE)
                if (result.code == Result.RESULT_OK &&
                        userResult.isNotEmpty() && currentUser != null) {
                    Logger.showResult(this, "AccessService.getCurrentPrincipal " + Logger.build(currentUser))
                    handleUpdate(userResult, currentUser)
                } else if (mThrowable != null) {
                    Logger.showResult(this, "AccessService.getCurrentPrincipal ${mThrowable?.message}")
                } else {
                    Logger.showResult(this, Logger.build(result))
                }
            }
        }
    }
}