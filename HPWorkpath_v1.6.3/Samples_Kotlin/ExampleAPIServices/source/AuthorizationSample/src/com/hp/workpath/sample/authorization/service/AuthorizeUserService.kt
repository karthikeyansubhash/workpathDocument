// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization.service

import android.util.Log
import com.hp.workpath.api.Workpath
import com.hp.workpath.api.authorization.AbstractAuthorizationService
import com.hp.workpath.api.authorization.UserAuthorizationData
import com.hp.workpath.api.authorization.UserAuthorizationResult
import com.hp.workpath.api.authorization.UserOverrides
import com.hp.workpath.sample.authorization.MainActivity
import com.hp.workpath.sample.authorization.R
import com.hp.workpath.sample.authorization.task.GetPermissionsTask
import java.util.concurrent.Executors

class AuthorizeUserService : AbstractAuthorizationService() {
    override fun authorizeUser(userAuthorizationData: UserAuthorizationData?): UserAuthorizationResult? {
        Log.i(MainActivity.TAG, "authorizeUser: ${userAuthorizationData.toString()}")

        return try {
            val userOverrides = UserOverrides.Builder()
                .addBccAddress(
                    getString(R.string.bcc_address_email_01) ?: "",
                    getString(R.string.bcc_address_name_01) ?: ""
                )
                .addBccAddress(
                    getString(R.string.bcc_address_email_02) ?: "",
                    getString(R.string.bcc_address_name_02) ?: ""
                )
                .addCcAddress(
                    getString(R.string.cc_address_email_01) ?: "",
                    getString(R.string.cc_address_name_01) ?: ""
                )
                .addCcAddress(
                    getString(R.string.cc_address_email_02) ?: "",
                    getString(R.string.cc_address_name_02) ?: ""
                )
                .setFrom(
                    getString(R.string.from_address_email) ?: "",
                    getString(R.string.from_address_name) ?: ""
                )
                .addToAddress(
                    getString(R.string.to_address_email_01) ?: "",
                    getString(R.string.to_address_name_01) ?: ""
                )
                .addToAddress(
                    getString(R.string.to_address_email_02) ?: "",
                    getString(R.string.to_address_name_02) ?: ""
                )
                .setMessage(getString(R.string.email_message) ?: "")
                .setSubject(getString(R.string.email_subject) ?: "")
                .setFaxBillingCode(getString(R.string.fax_billing_code) ?: "")
                .setFaxCompanyName(getString(R.string.fax_company_name) ?: "")
                .build()

            Workpath.getInstance().initialize(applicationContext)
            val executorService = Executors.newSingleThreadExecutor()
            val future = executorService.submit(GetPermissionsTask(applicationContext))
            val permissions = future.get()

            Log.i(MainActivity.TAG, "authorizeUser: permissions=$permissions")

            UserAuthorizationResult.Builder()
                .setAuthorizedUserOverrides(userOverrides)
                .setUserPermissionSet(GetPermissionsTask.getPermissionSetFromPermissions(permissions))
                .build()
        } catch (e: Exception) {
            Log.e(MainActivity.TAG, "authorizeUser: ${e.message}")
            null
        }
    }
}