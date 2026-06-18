// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessoryagentsample.task

import android.view.View
import com.hp.workpath.api.Result
import com.hp.workpath.api.access.AccessService
import com.hp.workpath.sample.accessoryagentsample.Logger
import com.hp.workpath.sample.accessoryagentsample.MainActivity
import com.hp.workpath.sample.accessoryagentsample.R
import com.hp.workpath.sample.accessoryagentsample.model.UserDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class UserDetailsReaderTask(context: MainActivity) {
    private val mContextRef: WeakReference<MainActivity> = WeakReference(context)
    private var mThrowable: Throwable? = null
    private val result: Result = Result()

    suspend fun execute() {
        val values: MutableMap<UserDetails, String> = HashMap()
        try {
            mContextRef.get()?.run {
                val res = resources
                values[UserDetails.FULLY_QUALIFIED_NAME] = res.getString(R.string.na)
                values[UserDetails.PRINCIPAL_ID] = res.getString(R.string.na)
                values[UserDetails.DOMAIN] = res.getString(R.string.na)
                values[UserDetails.PROVIDER] = res.getString(R.string.na)
                values[UserDetails.USER_NAME] = res.getString(R.string.na)
                values[UserDetails.IS_AUTHENTICATED] = res.getString(R.string.na)

                // get user principal information using AccessService API
                val currentUser = AccessService.getCurrentPrincipal(applicationContext, result)

                // first check whether Result is fine or not
                if (result.code == Result.RESULT_OK && currentUser != null) {
                    values[UserDetails.FULLY_QUALIFIED_NAME] = currentUser.fullyQualifiedName
                    values[UserDetails.PRINCIPAL_ID] = currentUser.principalId
                    values[UserDetails.DOMAIN] = currentUser.domain
                    values[UserDetails.PROVIDER] = currentUser.provider
                    values[UserDetails.USER_NAME] = currentUser.username
                    values[UserDetails.IS_AUTHENTICATED] = currentUser.isAuthenticated.toString()
                }
            }
        } catch (t: Throwable) {
            mThrowable = t
        }
        onPostExecute(values)
    }

    private suspend fun onPostExecute(userResult: Map<UserDetails, String>?) {
        withContext(Dispatchers.Main) {
            mContextRef.get()?.run {
                showProgress(View.GONE)
                if (userResult != null && result.code == Result.RESULT_OK) {
                    handleUpdate(userResult)
                } else if (mThrowable != null) {
                    Logger.showResult(this, "AccessService.getCurrentPrincipal ${mThrowable?.message}")
                } else {
                    Logger.showResult(this, "AccessService.getCurrentPrincipal", result)
                }
            }
        }
    }
}