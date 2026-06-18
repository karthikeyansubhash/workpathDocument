// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.googlesigninsample.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class AccountInfo {
    @SerializedName("access_token")
    var accessToken: String? = null

    @SerializedName("expires_in")
    var expiresIn = 0

    @SerializedName("id_token")
    var idToken: String? = null

    @SerializedName("refresh_token")
    var refreshToken: String? = null

    @SerializedName("scope")
    var scope: String? = null

    @SerializedName("token_type")
    var tokenType: String? = null
    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
}