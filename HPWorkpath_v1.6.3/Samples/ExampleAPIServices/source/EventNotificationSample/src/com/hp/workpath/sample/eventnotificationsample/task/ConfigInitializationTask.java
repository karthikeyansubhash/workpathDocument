package com.hp.workpath.sample.eventnotificationsample.task;

import android.content.Context;

import com.hp.workpath.api.config.ConfigService;
import com.hp.workpath.sample.eventnotificationsample.R;

public class ConfigInitializationTask extends InitializationTask {
    public ConfigInitializationTask(Context context, ResultHandler handler) {
        super(context, handler);
    }

    @Override
    public boolean isSupported(Context context) {
        return ConfigService.isSupported(context);
    }

    @Override
    public int getExceptionMessage() {
        return R.string.config_service_not_supported;
    }
}
