// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.googlesigninsample

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.DriveScopes
import com.google.gson.Gson
import com.hp.workpath.sample.googlesigninsample.databinding.ActivityMainBinding
import com.hp.workpath.sample.googlesigninsample.model.AccountInfo
import com.hp.workpath.sample.googlesigninsample.model.ClientInfo
import com.hp.workpath.sample.googlesigninsample.task.NetworkStatusTask
import com.hp.workpath.sample.googlesigninsample.task.RefreshTokenTask
import com.hp.workpath.sample.googlesigninsample.task.RequestTokenTask
import com.hp.workpath.sample.googlesigninsample.task.RevokeTokenTask
import com.hp.workpath.sample.googlesigninsample.task.TaskInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Arrays

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val CLIENT_INFO_FILE = "client_secrets.json"
    private lateinit var mAccountInfo: AccountInfo
    private lateinit var mClientInfo: ClientInfo

    private var isNetworkAvailable: Boolean = false
    private var mCodeVerifier: String? = null
    private var mCodeChallenge: String? = null
    private lateinit var mBindingMainActivity: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBindingMainActivity = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBindingMainActivity.root)
        generateCodeVerifier()
        initView()
    }

    private fun initView() {
        mBindingMainActivity.loadButton.setOnClickListener(this)
        mBindingMainActivity.revokeTokenButton.setOnClickListener(this)
        mBindingMainActivity.checkButton.setOnClickListener(this)
        mBindingMainActivity.refreshTokenButton.setOnClickListener(this)
        val webSettings = mBindingMainActivity.webView.getSettings()
        webSettings.javaScriptEnabled = true
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; rv:70.0) Gecko/20100101 Firefox/70.0")
        mBindingMainActivity.webView.webViewClient = WebViewClient()
    }

    private fun enableButtons() {
        mBindingMainActivity.revokeTokenButton.isEnabled = true
        mBindingMainActivity.checkButton.isEnabled = true
        mBindingMainActivity.refreshTokenButton.isEnabled = true
    }

    override fun onResume() {
        super.onResume()
        try {
            mClientInfo = readClientInfo()
        } catch (t: Throwable) {
            if (!TextUtils.isEmpty(t.message)) {
                Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
            }
        }
        lifecycleScope.launch(Dispatchers.IO) {
            NetworkStatusTask(this@MainActivity).execute()
        }
    }

    fun setNetworkAvailable(isAvailable: Boolean) {
        this.isNetworkAvailable = isAvailable
    }

    override fun onClick(v: View) {
        if (v === mBindingMainActivity.loadButton) {
            if (!isNetworkAvailable) {
                Toast.makeText(this, "Please connect network", Toast.LENGTH_SHORT).show()
            } else if (!isValidClientInfo(mClientInfo)) {
                Toast.makeText(
                    this,
                    "Please check client information in /assets/$CLIENT_INFO_FILE",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                startSignIn()
            }
        } else if (v === mBindingMainActivity.revokeTokenButton) {
            mAccountInfo.accessToken?.let {
                lifecycleScope.launch(Dispatchers.IO) {
                    RevokeTokenTask(it, taskInterface).execute()
                }
            }
        } else if (v === mBindingMainActivity.checkButton) {
            val intent = Intent(this, FileListActivity::class.java)
            intent.putExtra(FileListActivity.Companion.INTENT_CREDENTIAL, mAccountInfo.toString())
            startActivity(intent)
        } else if (v === mBindingMainActivity.refreshTokenButton) {
            mAccountInfo.refreshToken?.let {
                lifecycleScope.launch(Dispatchers.IO) {
                    RefreshTokenTask(mClientInfo, it, taskInterface).execute()
                }
            }
        }
    }

    private fun startSignIn() {
        try {
            val requestUrl = requestUrl
            if (!TextUtils.isEmpty(requestUrl)) {
                mBindingMainActivity.webView.loadUrl(requestUrl)
                mBindingMainActivity.webView.visibility = View.VISIBLE
            }
        } catch (t: Throwable) {
            Log.e(TAG, "RequestUrl", t)
        }
    }

    private inner class WebViewClient : android.webkit.WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            mClientInfo.redirectUri?.let {
                if (request?.url.toString().startsWith(it)) {
                    view?.visibility = View.GONE
                    val uri = Uri.parse(request?.url.toString())
                    val code = uri.getQueryParameter("code")
                    if (code != null && !TextUtils.isEmpty(code)) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            RequestTokenTask(mClientInfo, code, taskInterface).execute()
                        }
                    }
                }
            }
            return super.shouldOverrideUrlLoading(view, request)
        }
    }

    var taskInterface: TaskInterface = object : TaskInterface {
        override fun onFailure(t: Throwable?) {
            runOnUiThread {
                if (t != null && !TextUtils.isEmpty(t.message)) {
                    Log.e(TAG, "onFailure: " + t.message)
                    Toast.makeText(this@MainActivity, "onFailure: " + t.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        override fun refreshedToken(token: String) {
            Log.i(TAG, "refreshedToken: $token")
            runOnUiThread {
                Toast.makeText(this@MainActivity, "refreshedToken: $token", Toast.LENGTH_SHORT)
                    .show()
                mBindingMainActivity.tokenTextView.text = token
                mAccountInfo.accessToken = token
            }
        }

        override fun revokedToken() {
            Log.i(TAG, "token was revoked")
            runOnUiThread {
                Toast.makeText(this@MainActivity, "token was revoked", Toast.LENGTH_SHORT).show()
            }
        }

        override fun receivedToken(accountInfo: AccountInfo) {
            Log.i(TAG, "receivedToken: " + accountInfo.accessToken)
            runOnUiThread {
                mAccountInfo = accountInfo
                mBindingMainActivity.tokenTextView.text = accountInfo.accessToken
                enableButtons()
            }
        }
    }

    @get:Throws(Throwable::class)
    val requestUrl: String
        get() {
            val flow = baseUrl
            if (!TextUtils.isEmpty(mClientInfo.redirectUri)) {
                var requestUrl = flow.newAuthorizationUrl()
                requestUrl = requestUrl.setRedirectUri(mClientInfo.redirectUri)
                requestUrl["code_challenge_method"] = mClientInfo.codeChallengeMethod
                requestUrl["code_challenge"] = mClientInfo.codeChallenge
                return requestUrl.build()
            }
            throw Throwable("redirection url is empty")
        }

    @get:Throws(IOException::class)
    private val baseUrl: GoogleAuthorizationCodeFlow
        get() {
            val httpTransport: HttpTransport = NetHttpTransport()
            val jsonFactory: JsonFactory = JacksonFactory()
            return GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory,
                mClientInfo.clientId, null,
                Arrays.asList(DriveScopes.DRIVE, "https://www.googleapis.com/auth/userinfo.email")
            )
                .setAccessType("offline").setApprovalPrompt("force").build()
        }

    @Throws(Throwable::class)
    private fun readClientInfo(): ClientInfo {
        val inputStream = assets.open(CLIENT_INFO_FILE)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val json = String(buffer, Charset.forName("UTF-8"))
        val clientInfo = Gson().fromJson(json, ClientInfo::class.java)
        if (!isValidClientInfo(clientInfo)) {
            throw Throwable("Please check client information in /assets/$CLIENT_INFO_FILE")
        }
        clientInfo.codeChallenge = mCodeChallenge
        clientInfo.codeVerifier = mCodeVerifier
        return clientInfo
    }

    private fun isValidClientInfo(clientInfo: ClientInfo?): Boolean {
        if (clientInfo == null) return false
        if (TextUtils.isEmpty(clientInfo.clientId) ||
            getString(R.string.json_client_id).equals(clientInfo.clientId, ignoreCase = true)
        ) {
            return false
        }
        return if (TextUtils.isEmpty(clientInfo.redirectUri) ||
            getString(R.string.json_redirect_uri).equals(clientInfo.redirectUri, ignoreCase = true)
        ) {
            false
        } else true
    }

    private fun generateCodeVerifier() {
        try {
            val len = 50
            val ch = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray()
            val c = CharArray(len)
            val random = SecureRandom()
            for (i in 0 until len) {
                c[i] = ch[random.nextInt(ch.size)]
            }
            mCodeVerifier = String(c)
            Log.e(TAG, "generateCodeVerifier: $mCodeVerifier")
            val body = MessageDigest.getInstance("SHA-256")
                .digest(mCodeVerifier!!.toByteArray(charset("UTF-8")))
            mCodeChallenge =
                Base64.encodeToString(body, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
            Log.e(TAG, "generateCodeChallenge: $mCodeChallenge")
        } catch (t: Throwable) {
            if (!TextUtils.isEmpty(t.message)) {
                t.message?.let { Log.d(TAG, it) }
            }
        }
    }

    companion object {
        const val TAG = "[SAMPLE]" + "Google"
    }
}