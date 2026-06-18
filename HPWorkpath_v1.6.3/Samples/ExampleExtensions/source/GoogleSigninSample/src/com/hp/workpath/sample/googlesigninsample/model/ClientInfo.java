// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.googlesigninsample.model;

import com.google.gson.annotations.SerializedName;

public class ClientInfo {

    @SerializedName("client_id")
    String clientId;

    @SerializedName("client_secret")
    String clientSecret;

    @SerializedName("redirect_uri")
    String redirectUri;

    @SerializedName("grant_type")
    String grantType;

    @SerializedName("code_challenge_method")
    String codeChallengeMethod;

    @SerializedName("auth_uri")
    String authUri;

    @SerializedName("token_uri")
    String tokenUri;

    String codeChallenge;

    String codeVerifier;

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getCodeChallengeMethod() {
        return codeChallengeMethod;
    }

    public String getAuthUri() {
        return authUri;
    }

    public String getTokenUri() {
        return tokenUri;
    }

    public String getCodeChallenge() {
        return codeChallenge;
    }

    public void setCodeChallenge(String codeChallenge) {
        this.codeChallenge = codeChallenge;
    }

    public String getCodeVerifier() {
        return codeVerifier;
    }

    public void setCodeVerifier(String codeVerifier) {
        this.codeVerifier = codeVerifier;
    }
}
