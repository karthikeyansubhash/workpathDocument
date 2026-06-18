// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessoryservicesample

import android.content.Context
import com.hp.workpath.api.Result
import com.hp.workpath.api.access.SignInAction
import com.hp.workpath.api.config.ConfigService
import org.json.JSONObject

object ActionUtil {
    @JvmStatic
    fun getAction(context: Context): SignInAction.Action {
        try {
            if (ConfigService.isSupported(context)) {
                val result = Result()
                val config = ConfigService.getDefaultConfig(context, result)
                Logger.showResult(null, "ConfigService.getDefaultConfig", result)
                if (result.code == Result.RESULT_OK) {
                    if (config != null) {
                        val action = config.getString("agent_action")
                        return when {
                            SignInAction.Action.CONTINUE.name == action -> {
                                SignInAction.Action.CONTINUE
                            }
                            SignInAction.Action.FAIL.name == action -> {
                                SignInAction.Action.FAIL
                            }
                            SignInAction.Action.HOME.name == action -> {
                                SignInAction.Action.HOME
                            }
                            SignInAction.Action.BACK.name == action -> {
                                SignInAction.Action.BACK
                            }
                            else -> {
                                SignInAction.Action.SUCCESS
                            }
                        }
                    }
                }
            }
        } catch (t: Throwable) {
            Logger.showResult(null, "ConfigService.getDefaultConfig " + t.message)
        }
        return SignInAction.Action.SUCCESS
    }

    @JvmStatic
    @Throws(Throwable::class)
    fun setAction(context: Context, action: String?) {
        if (ConfigService.isSupported(context)) {
            val jsonObject = JSONObject()
            jsonObject.put("agent_action", action)
            val result = ConfigService.setDefaultConfig(context, jsonObject)
            Logger.showResult(null, "ConfigService.setDefaultConfig(): ", result)
        }
    }
}