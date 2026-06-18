// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.googlesigninsample.task

import com.hp.workpath.sample.googlesigninsample.model.AccountInfo

interface TaskInterface {
    fun onFailure(t: Throwable?)
    fun refreshedToken(token: String)
    fun revokedToken()
    fun receivedToken(accountInfo: AccountInfo)
}