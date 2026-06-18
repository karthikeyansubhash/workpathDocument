// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization.task

import android.content.Context
import com.hp.workpath.api.Result
import com.hp.workpath.api.authorization.AuthorizationService
import com.hp.workpath.api.authorization.Permission
import com.hp.workpath.sample.authorization.exception.ResultException
import java.lang.ref.WeakReference
import java.util.concurrent.Callable

class GetPermissionsTask(context: Context) : Callable<ArrayList<Permission>?> {

    private val contextRef: WeakReference<Context> = WeakReference(context)

    @Throws(Exception::class)
    override fun call(): ArrayList<Permission>? {
        val result = Result()
        val permissions = contextRef.get()?.let { ctx ->
            AuthorizationService.getPermissions(ctx, result)
        }
        if (result.code != Result.RESULT_OK) {
            throw ResultException(result)
        }
        return permissions
    }

    companion object {
        fun getPermissionsFromPermissionSet(
            context: Context,
            permissionSet: List<String>
        ): java.util.ArrayList<Permission>? {
            val result = Result()
            val permissions = context.let { ctx ->
                AuthorizationService.getPermissions(ctx, result)
            }
            return if (permissions != null && permissionSet.isNotEmpty()) {
                permissions.filter { permissionSet.contains(it.id) }
                    .toCollection(java.util.ArrayList())
            } else {
                return null
            }
        }

        fun getPermissionSetFromPermissions(permissions: ArrayList<Permission>?): List<String> {
            val permissionSet = mutableListOf<String>()
            permissions?.forEach { permission ->
                permissionSet.add(permission.id)
            }
            return permissionSet
        }
    }
}