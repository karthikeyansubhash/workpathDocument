package com.hp.workpath.sample.eventnotificationsample.task;

import android.content.Context;

import com.hp.workpath.api.access.AccessService;
import com.hp.workpath.sample.eventnotificationsample.R;

public class AccessInitializationTask extends InitializationTask {
    public AccessInitializationTask(Context context, ResultHandler handler) {
        super(context, handler);
    }

    @Override
    public boolean isSupported(Context context) {
        return AccessService.isSupported(context);
    }

    @Override
    public int getExceptionMessage() {
        return R.string.access_service_not_supported;
    }
}
