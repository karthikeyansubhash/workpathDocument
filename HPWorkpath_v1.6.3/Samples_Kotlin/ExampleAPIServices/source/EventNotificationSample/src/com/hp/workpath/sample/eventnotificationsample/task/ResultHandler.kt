package com.hp.workpath.sample.eventnotificationsample.task

interface ResultHandler {
    fun handleComplete()
    fun handleException(t: Throwable?)
    fun handleUpdate(updateData: String?)
}