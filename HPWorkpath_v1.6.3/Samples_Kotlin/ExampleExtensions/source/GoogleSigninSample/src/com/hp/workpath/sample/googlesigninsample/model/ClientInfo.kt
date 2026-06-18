// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.googlesigninsample.model

import com.google.gson.annotations.SerializedName

class ClientInfo {
    @SerializedName("client_id")
    var clientId: String? = null

    @SerializedName("client_secret")
    var clientSecret: String? = null

    @SerializedName("redirect_uri")
    var redirectUri: String? = null

    @SerializedName("grant_type")
    var grantType: String? = null

    @SerializedName("code_challenge_method")
    var codeChallengeMethod: String? = null

    @SerializedName("auth_uri")
    var authUri: String? = null

    @SerializedName("token_uri")
    var tokenUri: String? = null
    var codeChallenge: String? = null
    var codeVerifier: String? = null
}