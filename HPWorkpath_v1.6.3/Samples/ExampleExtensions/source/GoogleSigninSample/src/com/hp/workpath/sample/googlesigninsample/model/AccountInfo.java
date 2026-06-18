// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.googlesigninsample.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class AccountInfo {

    @SerializedName("access_token")
    String accessToken;

    @SerializedName("expires_in")
    int expiresIn;

    @SerializedName("id_token")
    String idToken;

    @SerializedName("refresh_token")
    String refreshToken;

    @SerializedName("scope")
    String scope;

    @SerializedName("token_type")
    String tokenType;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public String getIdToken() {
        return idToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getScope() {
        return scope;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
