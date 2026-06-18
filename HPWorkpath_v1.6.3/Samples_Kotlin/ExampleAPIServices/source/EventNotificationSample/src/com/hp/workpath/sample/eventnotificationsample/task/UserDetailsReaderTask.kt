package com.hp.workpath.sample.eventnotificationsample.task

import android.content.Context
import com.hp.workpath.api.Result
import com.hp.workpath.api.access.AccessService
import com.hp.workpath.api.access.Principal
import com.hp.workpath.sample.eventnotificationsample.Logger
import com.hp.workpath.sample.eventnotificationsample.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class UserDetailsReaderTask(context: Context, resultHandler: ResultHandler) {
    companion object {
        val TAG = "[SAMPLE]" + "UserDetailsReaderTask"
    }
    private var mContextRef: WeakReference<Context> = WeakReference(context)

    private var mResultHandlerRef: WeakReference<ResultHandler> = WeakReference(resultHandler)

    private val result: Result = Result()

    private var currentUser: Principal? = null

    suspend fun taskExecute() {
        mContextRef.get()?.run {
            try {
                // get user principal information using AccessService API
                currentUser = AccessService.getCurrentPrincipal(this, result)

                // first check whether Result is fine or not
                if (result.code == Result.RESULT_OK && currentUser != null) {
                    onPostExecute()
                }
            } catch (t: Throwable) {
                onPostExecute()
            }
        }
    }

    private suspend fun onPostExecute() {
        withContext(Dispatchers.Main) {
            val activity = mContextRef.get() as? MainActivity

            if (result.code == Result.RESULT_OK && currentUser != null) {
                Logger.showResult(activity, "AccessService.getCurrentPrincipal " + Logger.build(currentUser))
                mResultHandlerRef.get()?.handleUpdate(currentUser.toString())
            } else {
                Logger.showResult(activity, Logger.build(result))
            }
        }
    }

}