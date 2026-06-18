package com.hp.workpath.sample.eventnotificationsample.task

import android.content.Context
import com.hp.workpath.api.access.AccessService
import com.hp.workpath.sample.eventnotificationsample.R

class AccessInitializationTask(context: Context, handler: ResultHandler) :
    InitializationTask(context, handler) {
    override fun isSupported(context: Context?): Boolean {
        return AccessService.isSupported(context?: return false)
    }

    override fun getExceptionMessage(): Int {
        return R.string.access_service_not_supported
    }
}