// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessoryagentsample;

import android.content.Context;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.access.SignInAction;
import com.hp.workpath.api.config.ConfigService;

import org.json.JSONObject;

public class ActionUtil {

    public static SignInAction.Action getAction(Context context) {
        try {
            if (ConfigService.isSupported(context)) {
                Result result = new Result();
                JSONObject config = ConfigService.getDefaultConfig(context, result);
                Logger.showResult(null, "ConfigService.getDefaultConfig", result);
                if (result.getCode() == Result.RESULT_OK) {
                    if (config != null) {
                        String action = config.getString("agent_action");
                        if (SignInAction.Action.CONTINUE.name().equals(action)) {
                            return SignInAction.Action.CONTINUE;
                        } else if (SignInAction.Action.FAIL.name().equals(action)) {
                            return SignInAction.Action.FAIL;
                        } else if (SignInAction.Action.HOME.name().equals(action)) {
                            return SignInAction.Action.HOME;
                        } else if (SignInAction.Action.BACK.name().equals(action)) {
                            return SignInAction.Action.BACK;
                        } else {
                            return SignInAction.Action.SUCCESS;
                        }
                    }
                }
            }
        } catch (Throwable t) {
            Logger.showResult(null, "ConfigService.getDefaultConfig " + t.getMessage());
        }
        return SignInAction.Action.SUCCESS;
    }

    public static void setAction(Context context, String action) throws Throwable {
        if (ConfigService.isSupported(context)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("agent_action", action);
            Result result = ConfigService.setDefaultConfig(context, jsonObject);
            Logger.showResult(null, "ConfigService.setDefaultConfig", result);
        }
    }
}
