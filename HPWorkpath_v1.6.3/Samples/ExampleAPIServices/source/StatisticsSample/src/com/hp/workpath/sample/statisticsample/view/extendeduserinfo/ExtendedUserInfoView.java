// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.extendeduserinfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hp.workpath.api.statistics.jobinfo.userinfo.AuthenticatedUserInfo;
import com.hp.workpath.api.statistics.jobinfo.userinfo.ExtendedUserInfo;
import com.hp.workpath.sample.statisticsample.R;
import com.hp.workpath.sample.statisticsample.view.Utils;
import com.hp.workpath.sample.statisticsample.view.common.KeyValueView;

public class ExtendedUserInfoView {

    LinearLayout rootView;
    View view;
    KeyValueView keyValueView;

    ViewGroup layoutAuthenticationType;
    ViewGroup layoutDisplayName;
    ViewGroup layoutEmailAddress;
    ViewGroup layoutExchangeMailboxUri;
    ViewGroup layoutFullyQualifiedUserName;
    ViewGroup layoutHomeFolderPath;
    ViewGroup layoutLdapBindUser;
    ViewGroup layoutNdsContext;
    ViewGroup layoutNdsTreeName;
    ViewGroup layoutsAMAccountName;
    ViewGroup layoutSidString;
    ViewGroup layoutUserDomain;
    ViewGroup layoutUserName;
    ViewGroup layoutUserPrincipalName;
    LinearLayout layoutKeyValuePairs;

    ViewGroup layoutAuthenticationAgentId;
    ViewGroup layoutAuthenticationAgentName;
    ViewGroup layoutAuthorizationAgentId;
    ViewGroup layoutAuthorizationAgentName;

    public ExtendedUserInfoView(LayoutInflater inflater, LinearLayout rootView) {
        this.rootView = rootView;
        this.view = inflater.inflate(R.layout.layout_extended_user_info, rootView, false);
        initViewExtendedUserInfo();
        initViewClass(inflater);
    }

    public void setExtendedUserInfo(ExtendedUserInfo extendedUserInfo) {
        rootView.removeAllViews();
        if (extendedUserInfo != null) {
            setAuthenticationUserInfo(extendedUserInfo.getAuthenticatedUserInfo());
            Utils.setSummary(layoutAuthenticationAgentId, extendedUserInfo.getAuthenticationAgentId());
            Utils.setSummary(layoutAuthenticationAgentName, extendedUserInfo.getAuthenticationAgentName());
            Utils.setSummary(layoutAuthorizationAgentId, extendedUserInfo.getAuthorizationAgentId());
            Utils.setSummary(layoutAuthorizationAgentName, extendedUserInfo.getAuthorizationAgentName());
            rootView.addView(view);
        } else {
            LinearLayout parent = (LinearLayout) rootView.getParent();
            parent.setVisibility(View.GONE);
        }
    }

    public void setAuthenticationUserInfo(AuthenticatedUserInfo info) {
        if (info != null) {
            Utils.setSummary(layoutAuthenticationType, info.getAuthenticationType());
            Utils.setSummary(layoutDisplayName, info.getDisplayName());
            Utils.setSummary(layoutEmailAddress, info.getEmailAddress());
            Utils.setSummary(layoutExchangeMailboxUri, info.getExchangeMailboxUri());
            Utils.setSummary(layoutFullyQualifiedUserName, info.getFullyQualifiedUserName());
            Utils.setSummary(layoutHomeFolderPath, info.getHomeFolderPath());
            Utils.setSummary(layoutLdapBindUser, info.getLdapBindUser());
            Utils.setSummary(layoutNdsContext, info.getNdsContext());
            Utils.setSummary(layoutNdsTreeName, info.getNdsTreeName());
            Utils.setSummary(layoutsAMAccountName, info.getsAMAccountName());
            Utils.setSummary(layoutSidString, info.getSidString());
            Utils.setSummary(layoutUserDomain, info.getUserDomain());
            Utils.setSummary(layoutUserName, info.getUserName());
            Utils.setSummary(layoutUserPrincipalName, info.getUserPrincipalName());
            keyValueView.setKeyValue(info.getKeyValuePairs());
        } else {
            LinearLayout parent = (LinearLayout) layoutAuthenticationType.getParent().getParent();
            parent.setVisibility(View.GONE);
        }
    }

    private void initViewExtendedUserInfo() {
        ((TextView) view.findViewById(R.id.titleAuthenticatedUserInfoTextView)).setText(R.string.authenticatedUserInfo);
        layoutAuthenticationType = Utils.setTitle(view.findViewById(R.id.layoutAuthenticationType), R.string.authenticationType);
        layoutDisplayName = Utils.setTitle(view.findViewById(R.id.layoutDisplayName), R.string.displayName);
        layoutEmailAddress = Utils.setTitle(view.findViewById(R.id.layoutEmailAddress), R.string.emailAddress);
        layoutExchangeMailboxUri = Utils.setTitle(view.findViewById(R.id.layoutExchangeMailboxUri), R.string.exchangeMailboxUri);
        layoutFullyQualifiedUserName = Utils.setTitle(view.findViewById(R.id.layoutFullyQualifiedUserName), R.string.fullyQualifiedUserName);
        layoutHomeFolderPath = Utils.setTitle(view.findViewById(R.id.layoutHomeFolderPath), R.string.homeFolderPath);
        layoutLdapBindUser = Utils.setTitle(view.findViewById(R.id.layoutLdapBindUser), R.string.ldapBindUser);
        layoutNdsContext = Utils.setTitle(view.findViewById(R.id.layoutNdsContext), R.string.ndsContext);
        layoutNdsTreeName = Utils.setTitle(view.findViewById(R.id.layoutNdsTreeName), R.string.ndsTreeName);
        layoutsAMAccountName = Utils.setTitle(view.findViewById(R.id.layoutsAMAccountName), R.string.sAMAccountName);
        layoutSidString = Utils.setTitle(view.findViewById(R.id.layoutSidString), R.string.sidString);
        layoutUserDomain = Utils.setTitle(view.findViewById(R.id.layoutUserDomain), R.string.userDomain);
        layoutUserName = Utils.setTitle(view.findViewById(R.id.layoutUserName), R.string.userName);
        layoutUserPrincipalName = Utils.setTitle(view.findViewById(R.id.layoutUserPrincipalName), R.string.userPrincipalName);
        layoutKeyValuePairs = Utils.getLayout(view.findViewById(R.id.layoutKeyValuePairs), R.string.keyValuePairs);

        layoutAuthenticationAgentId = Utils.setTitle(view.findViewById(R.id.layoutAuthenticationAgentId), R.string.authenticationAgentId);
        layoutAuthenticationAgentName = Utils.setTitle(view.findViewById(R.id.layoutAuthenticationAgentName), R.string.authenticationAgentName);
        layoutAuthorizationAgentId = Utils.setTitle(view.findViewById(R.id.layoutAuthorizationAgentId), R.string.authorizationAgentId);
        layoutAuthorizationAgentName = Utils.setTitle(view.findViewById(R.id.layoutAuthorizationAgentName), R.string.authorizationAgentName);
    }

    private void initViewClass(LayoutInflater inflater) {
        keyValueView = new KeyValueView(inflater, layoutKeyValuePairs);
    }
}
