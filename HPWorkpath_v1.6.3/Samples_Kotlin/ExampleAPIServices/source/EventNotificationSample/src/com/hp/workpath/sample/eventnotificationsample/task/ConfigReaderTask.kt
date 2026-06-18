package com.hp.workpath.sample.eventnotificationsample.task

import android.content.Context
import com.hp.workpath.api.Result
import com.hp.workpath.api.config.ConfigService
import com.hp.workpath.sample.eventnotificationsample.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

class ConfigReaderTask(context: Context, resultHandler: ResultHandler) {
    private var mContextRef: WeakReference<Context> = WeakReference(context)

    private var mResultHandlerRef: WeakReference<ResultHandler> = WeakReference(resultHandler)

    private val mResult: Result = Result()

    suspend fun taskExecute() {
        mContextRef.get()?.run {
            try {
                onPostExecute(ConfigService.getDefaultConfig(this, mResult))
            } catch (t: Throwable) {
                Logger.showResult(null, "ConfigService.getDefaultConfig is failed:" + t.message)
            }
        }
    }

    private suspend fun onPostExecute(jsonObject: JSONObject?) {
        withContext(Dispatchers.Main) {
            if (jsonObject != null && mResult.code == Result.RESULT_OK) {
                try {
                    mResultHandlerRef.get()?.handleUpdate(jsonObject.toString(4))
                } catch (e: JSONException) {
                    mResultHandlerRef.get()?.handleException(e)
                }
            }
        }
    }
}