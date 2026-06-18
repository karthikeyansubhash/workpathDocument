// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.statisticsample.view.extendeduserinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.hp.workpath.api.statistics.jobinfo.userinfo.AuthenticatedUserInfo
import com.hp.workpath.api.statistics.jobinfo.userinfo.ExtendedUserInfo
import com.hp.workpath.sample.statisticsample.R
import com.hp.workpath.sample.statisticsample.view.Utils.getLayout
import com.hp.workpath.sample.statisticsample.view.Utils.setSummary
import com.hp.workpath.sample.statisticsample.view.Utils.setTitle
import com.hp.workpath.sample.statisticsample.view.common.KeyValueView

class ExtendedUserInfoView(inflater: LayoutInflater, var rootView: LinearLayout) {
    var view: View = inflater.inflate(R.layout.layout_extended_user_info, rootView, false)
    private lateinit var keyValueView: KeyValueView

    private lateinit var layoutAuthenticationType: ViewGroup
    private lateinit var layoutDisplayName: ViewGroup
    private lateinit var layoutEmailAddress: ViewGroup
    private lateinit var layoutExchangeMailboxUri: ViewGroup
    private lateinit var layoutFullyQualifiedUserName: ViewGroup
    private lateinit var layoutHomeFolderPath: ViewGroup
    private lateinit var layoutLdapBindUser: ViewGroup
    private lateinit var layoutNdsContext: ViewGroup
    private lateinit var layoutNdsTreeName: ViewGroup
    private lateinit var layoutsAMAccountName: ViewGroup
    private lateinit var layoutSidString: ViewGroup
    private lateinit var layoutUserDomain: ViewGroup
    private lateinit var layoutUserName: ViewGroup
    private lateinit var layoutUserPrincipalName: ViewGroup
    private lateinit var layoutKeyValuePairs: LinearLayout

    private lateinit var layoutAuthenticationAgentId: ViewGroup
    private lateinit var layoutAuthenticationAgentName: ViewGroup
    private lateinit var layoutAuthorizationAgentId: ViewGroup
    private lateinit var layoutAuthorizationAgentName: ViewGroup

    fun setExtendedUserInfo(extendedUserInfo: ExtendedUserInfo?) {
        rootView.removeAllViews()
        if (extendedUserInfo != null) {
            setAuthenticationUserInfo(extendedUserInfo.authenticatedUserInfo)
            setSummary(layoutAuthenticationAgentId, extendedUserInfo.authenticationAgentId)
            setSummary(layoutAuthenticationAgentName, extendedUserInfo.authenticationAgentName)
            setSummary(layoutAuthorizationAgentId, extendedUserInfo.authorizationAgentId)
            setSummary(layoutAuthorizationAgentName, extendedUserInfo.authorizationAgentName)
            rootView.addView(view)
        } else {
            val parent = (rootView as ViewGroup).parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun setAuthenticationUserInfo(info: AuthenticatedUserInfo?) {
        if (info != null) {
            setSummary(layoutAuthenticationType, info.authenticationType)
            setSummary(layoutDisplayName, info.displayName)
            setSummary(layoutEmailAddress, info.emailAddress)
            setSummary(layoutExchangeMailboxUri, info.exchangeMailboxUri)
            setSummary(layoutFullyQualifiedUserName, info.fullyQualifiedUserName)
            setSummary(layoutHomeFolderPath, info.homeFolderPath)
            setSummary(layoutLdapBindUser, info.ldapBindUser)
            setSummary(layoutNdsContext, info.ndsContext)
            setSummary(layoutNdsTreeName, info.ndsTreeName)
            setSummary(layoutsAMAccountName, info.getsAMAccountName())
            setSummary(layoutSidString, info.sidString)
            setSummary(layoutUserDomain, info.userDomain)
            setSummary(layoutUserName, info.userName)
            setSummary(layoutUserPrincipalName, info.userPrincipalName)
            keyValueView.setKeyValue(info.keyValuePairs)
        } else {
            val parent = layoutAuthenticationType.parent.parent as LinearLayout
            parent.visibility = View.GONE
        }
    }

    private fun initViewExtendedUserInfo() {
        (view.findViewById<View>(R.id.titleAuthenticatedUserInfoTextView) as TextView).setText(R.string.authenticatedUserInfo)
        layoutAuthenticationType =
            setTitle(view.findViewById(R.id.layoutAuthenticationType), R.string.authenticationType)
        layoutDisplayName =
            setTitle(view.findViewById(R.id.layoutDisplayName), R.string.displayName)
        layoutEmailAddress =
            setTitle(view.findViewById(R.id.layoutEmailAddress), R.string.emailAddress)
        layoutExchangeMailboxUri =
            setTitle(view.findViewById(R.id.layoutExchangeMailboxUri), R.string.exchangeMailboxUri)
        layoutFullyQualifiedUserName = setTitle(
            view.findViewById(R.id.layoutFullyQualifiedUserName),
            R.string.fullyQualifiedUserName
        )
        layoutHomeFolderPath =
            setTitle(view.findViewById(R.id.layoutHomeFolderPath), R.string.homeFolderPath)
        layoutLdapBindUser =
            setTitle(view.findViewById(R.id.layoutLdapBindUser), R.string.ldapBindUser)
        layoutNdsContext = setTitle(view.findViewById(R.id.layoutNdsContext), R.string.ndsContext)
        layoutNdsTreeName =
            setTitle(view.findViewById(R.id.layoutNdsTreeName), R.string.ndsTreeName)
        layoutsAMAccountName =
            setTitle(view.findViewById(R.id.layoutsAMAccountName), R.string.sAMAccountName)
        layoutSidString = setTitle(view.findViewById(R.id.layoutSidString), R.string.sidString)
        layoutUserDomain = setTitle(view.findViewById(R.id.layoutUserDomain), R.string.userDomain)
        layoutUserName = setTitle(view.findViewById(R.id.layoutUserName), R.string.userName)
        layoutUserPrincipalName =
            setTitle(view.findViewById(R.id.layoutUserPrincipalName), R.string.userPrincipalName)
        layoutKeyValuePairs =
            getLayout(view.findViewById(R.id.layoutKeyValuePairs), R.string.keyValuePairs)

        layoutAuthenticationAgentId = setTitle(
            view.findViewById(R.id.layoutAuthenticationAgentId),
            R.string.authenticationAgentId
        )
        layoutAuthenticationAgentName = setTitle(
            view.findViewById(R.id.layoutAuthenticationAgentName),
            R.string.authenticationAgentName
        )
        layoutAuthorizationAgentId = setTitle(
            view.findViewById(R.id.layoutAuthorizationAgentId),
            R.string.authorizationAgentId
        )
        layoutAuthorizationAgentName = setTitle(
            view.findViewById(R.id.layoutAuthorizationAgentName),
            R.string.authorizationAgentName
        )
    }

    private fun initViewClass(inflater: LayoutInflater) {
        keyValueView = KeyValueView(inflater, layoutKeyValuePairs)
    }

    init {
        initViewExtendedUserInfo()
        initViewClass(inflater)
    }
}