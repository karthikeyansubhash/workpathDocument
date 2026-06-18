package com.hp.workpath.sample.eventnotificationsample.task

import android.content.Context
import com.hp.workpath.api.config.ConfigService
import com.hp.workpath.sample.eventnotificationsample.R

class ConfigInitializationTask(context: Context, handler: ResultHandler)
    : InitializationTask(context, handler) {
    override fun isSupported(context: Context?): Boolean {
        return ConfigService.isSupported(context?: return false)
    }

    override fun getExceptionMessage(): Int {
        return R.string.config_service_not_supported
    }
}