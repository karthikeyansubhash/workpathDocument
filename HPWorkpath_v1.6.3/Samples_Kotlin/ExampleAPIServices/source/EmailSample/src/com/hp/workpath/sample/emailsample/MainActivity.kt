// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.DataSetObserver
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.Toast
import android.widget.LinearLayout
import android.widget.Button
import android.widget.TextView
import android.widget.BaseAdapter
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath
import com.hp.workpath.api.helper.email.EmailAttributes
import com.hp.workpath.api.helper.email.ProxyAttributes
import com.hp.workpath.api.helper.email.SmtpAttributes
import com.hp.workpath.sample.emailsample.databinding.ActivityMainBinding
import com.hp.workpath.sample.emailsample.filebrowser.FileUtils
import com.hp.workpath.sample.emailsample.fragments.AddMailFragment
import com.hp.workpath.sample.emailsample.fragments.ProxySettingFragment
import com.hp.workpath.sample.emailsample.fragments.SmtpSettingFragment
import com.hp.workpath.sample.emailsample.interfaces.IDialogFragmentListener
import com.hp.workpath.sample.emailsample.model.EmailAddress
import com.hp.workpath.sample.emailsample.model.EmailInfo
import com.hp.workpath.sample.emailsample.task.InitializationTask
import com.hp.workpath.sample.emailsample.task.LoadEmailDefaultsTask
import com.hp.workpath.sample.emailsample.task.SendEmailTask
import com.hp.workpath.sample.emailsample.task.SendEmailTask.SendType
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), IDialogFragmentListener {
    private lateinit var mAlertDialog: AlertDialog
    private lateinit var mBindingActivityMain: ActivityMainBinding

    //To, Cc, Bcc
    private var mToList: ArrayList<EmailAddress> = ArrayList()
    private var mCcList: ArrayList<EmailAddress> = ArrayList()
    private var mBccList: ArrayList<EmailAddress> = ArrayList()

    //Attachment
    private var mAttachedFileList: ArrayList<File> = ArrayList()
    private var mAttachedFileAdapter: AttachAdapter = AttachAdapter()
    private lateinit var mAttachedFileDataSetObserver: DataSetObserver

    private var SCREEN_4_3_INCH = "Screen_4.3_Inch"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBindingActivityMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBindingActivityMain.root)

        if (SCREEN_4_3_INCH.equals(mBindingActivityMain.container.tag)) {
            setSupportActionBar(mBindingActivityMain.toolbar)
        }

        // find the text and button
        initView()

        // add click listener to call the MFP
        addListener()
    }

    override fun onResume() {
        super.onResume()
        mBindingActivityMain.container.isEnabled = false
        lifecycleScope.launch (Dispatchers.Default) {
            InitializationTask(this@MainActivity).execute()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.version, menu)
        val versionMenu = menu.findItem(R.id.menuVersion)
        try {
            val sdkInfo = Workpath.getInstance()
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            versionMenu.title = getString(R.string.version, pInfo.versionName, pInfo.longVersionCode.toInt(), sdkInfo.versionName, sdkInfo.versionCode)
        } catch (t: Throwable) {
            handleException(t)
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        if (this::mAlertDialog.isInitialized) {
            mAlertDialog.dismiss()
        }
    }

    private fun initView() {
        findViewElements()
        mBindingActivityMain.attachmentListView.adapter = mAttachedFileAdapter
    }

    private fun findViewElements() {
        mBindingActivityMain.attachedFileCounter.text = getString(R.string.attachment_count, 0)
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        sharedPref.edit()
                .putBoolean(getString(R.string.smtp), false)
                .putBoolean(getString(R.string.proxy), false)
                .apply()
        setSendButtonsStatus()
    }

    private var mFileBrowserLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        onActivityResult(FILE_BROWSER_REQUEST_CODE, result)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addListener() {
        if (SCREEN_4_3_INCH.equals(mBindingActivityMain.container.tag)) {
            mBindingActivityMain.floatSettings?.setOnClickListener {
                if (mBindingActivityMain.buttonBar.visibility == View.VISIBLE) {
                    mBindingActivityMain.buttonBar.visibility = View.GONE
                } else {
                    mBindingActivityMain.buttonBar.visibility = View.VISIBLE
                }
            }
        }
        mBindingActivityMain.attachButton.setOnClickListener {
            val intent = Intent(this@MainActivity, FileBrowserActivity::class.java)
            mFileBrowserLauncher.launch(intent)
        }
        mBindingActivityMain.smtpDetailLayout.setOnClickListener { SmtpSettingFragment().show(supportFragmentManager, getString(R.string.smtp)) }
        mBindingActivityMain.proxyDetailLayout.setOnClickListener { ProxySettingFragment().show(supportFragmentManager, getString(R.string.proxy)) }
        val toClickListener = View.OnClickListener { showAddMailDialog(EmailDialog.Type.ADD_TO) }
        mBindingActivityMain.toTextView.setOnClickListener(toClickListener)
        mBindingActivityMain.addToButton.setOnClickListener(toClickListener)
        val ccClickListener = View.OnClickListener { showAddMailDialog(EmailDialog.Type.ADD_CC) }
        mBindingActivityMain.ccTextView.setOnClickListener(ccClickListener)
        mBindingActivityMain.addCcButton.setOnClickListener(ccClickListener)
        val bccClickListener = View.OnClickListener { showAddMailDialog(EmailDialog.Type.ADD_BCC) }
        mBindingActivityMain.bccTextView.setOnClickListener(bccClickListener)
        mBindingActivityMain.addBccButton.setOnClickListener(bccClickListener)
        val allowScrollListener = View.OnTouchListener { view, ev ->
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> view.parent.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP -> view.parent.requestDisallowInterceptTouchEvent(false)
            }
            view.onTouchEvent(ev)
            true
        }
        mBindingActivityMain.messageEditText.setOnTouchListener(allowScrollListener)
        mBindingActivityMain.attachmentListView.setOnTouchListener(allowScrollListener)
        mAttachedFileDataSetObserver = object : DataSetObserver() {
            override fun onChanged() {
                super.onChanged()
                mBindingActivityMain.attachedFileCounter.text = getString(R.string.attachment_count, mAttachedFileList.size)
            }
        }
        mAttachedFileAdapter.registerDataSetObserver(mAttachedFileDataSetObserver)
        mBindingActivityMain.smtpEnableCheckBox.setOnClickListener { view ->
            val checkBox = view as CheckBox
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            if (checkBox.isChecked) {
                if (sharedPref.getString(getString(R.string.pref_email_hostname), null) == null) {
                    Toast.makeText(applicationContext, getString(R.string.no_smtp_setting),
                            Toast.LENGTH_SHORT).show()
                    checkBox.isChecked = false
                } else {
                    mBindingActivityMain.smtpTextView.text = smtpSettings
                }
            }
            sharedPref.edit().putBoolean(getString(R.string.smtp), checkBox.isChecked).apply()
        }
        mBindingActivityMain.smtpEnableCheckBox.setOnCheckedChangeListener { _, _ -> setSendButtonsStatus() }
        mBindingActivityMain.proxyEnableCheckBox.setOnClickListener { view ->
            val checkBox = view as CheckBox
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            if (checkBox.isChecked) {
                if (sharedPref.getString(ProxySettingFragment.PREF_EMAIL_PROXY_HOST, null) == null) {
                    Toast.makeText(applicationContext, getString(R.string.no_proxy_setting),
                            Toast.LENGTH_SHORT).show()
                    checkBox.isChecked = false
                } else {
                    mBindingActivityMain.proxyTextView.text = proxySettings
                }
            }
            sharedPref.edit().putBoolean(getString(R.string.proxy), checkBox.isChecked).apply()
        }
        mBindingActivityMain.proxyEnableCheckBox.setOnCheckedChangeListener { _, _ -> setSendButtonsStatus() }
        mBindingActivityMain.getDefaultButton.setOnClickListener { loadDefaults() }
        mBindingActivityMain.sendButton.setOnClickListener { sendEmail(SendType.SEND) }
        mBindingActivityMain.sendWithSmtpButton.setOnClickListener { sendEmail(SendType.SEND_WITH_SMTP) }
        mBindingActivityMain.sendWithSmtpProxyButton.setOnClickListener { sendEmail(SendType.SEND_WITH_SMTP_PROXY) }
    }

    private fun loadDefaults() {
        showProgressBar(View.VISIBLE)
        lifecycleScope.launch (Dispatchers.Default) {
            LoadEmailDefaultsTask(this@MainActivity).execute()
        }
    }

    private fun sendEmail(sendType: SendType) {
        val emailInfo = EmailInfo()
        emailInfo.attachments = mAttachedFileList.toTypedArray()
        emailInfo.bcc = mBccList
        emailInfo.cc = mCcList
        emailInfo.to = mToList
        val fromAddress = mBindingActivityMain.fromAddressEditText.text.toString()
        val fromName = mBindingActivityMain.fromNameEditText.text.toString()
        emailInfo.from = EmailAddress(fromAddress, fromName)
        if (!TextUtils.isEmpty(mBindingActivityMain.messageEditText.text.toString())) {
            emailInfo.message = mBindingActivityMain.messageEditText.text.toString()
        }
        if (!TextUtils.isEmpty(mBindingActivityMain.subjectEditText.text.toString())) {
            emailInfo.subject = mBindingActivityMain.subjectEditText.text.toString()
        }
        showProgressBar(View.VISIBLE)

        lifecycleScope.launch (Dispatchers.Default) {
            SendEmailTask(this@MainActivity, sendType, emailInfo).execute()
        }
    }

    fun showProgressBar(visibility: Int) {
        mBindingActivityMain.progressbar.visibility = visibility
    }

    private fun showAddMailDialog(type: EmailDialog.Type) {
        val mailDialog = AddMailFragment()
        val bundle = Bundle()
        bundle.putSerializable(EmailDialog.DIALOG_TYPE, type)
        mailDialog.arguments = bundle
        mailDialog.show(supportFragmentManager, getString(R.string.email))
    }

    override fun onReturnValue(result: HashMap<String, Any>) {
        val mail: EmailAddress
        hideKeyboard(mBindingActivityMain.container)
        when (result[EmailDialog.DIALOG_TYPE] as EmailDialog.Type) {
            EmailDialog.Type.ATTACH -> addAttachment(result[getString(R.string.attachment)] as File)
            EmailDialog.Type.ADD_TO -> {
                mail = result[getString(R.string.email)] as EmailAddress
                addMailView(mBindingActivityMain.toListLayout, mail, mToList)
            }
            EmailDialog.Type.ADD_CC -> {
                mail = result[getString(R.string.email)] as EmailAddress
                addMailView(mBindingActivityMain.ccListLayout, mail, mCcList)
            }
            EmailDialog.Type.ADD_BCC -> {
                mail = result[getString(R.string.email)] as EmailAddress
                addMailView(mBindingActivityMain.bccListLayout, mail, mBccList)
            }
            EmailDialog.Type.SMTP -> {
                mBindingActivityMain.smtpTextView.text = smtpSettings
                mBindingActivityMain.smtpEnableCheckBox.isChecked = true
            }
            EmailDialog.Type.PROXY -> {
                mBindingActivityMain.proxyTextView.text = proxySettings
                mBindingActivityMain.proxyEnableCheckBox.isChecked = true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mAttachedFileAdapter.unregisterDataSetObserver(mAttachedFileDataSetObserver)
    }

    private fun onActivityResult(requestCode: Int, result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            when (requestCode) {
                FILE_BROWSER_REQUEST_CODE -> {
                    if (data != null) {
                        val filePath = data.getStringExtra(FileUtils.PATH)
                        if (!filePath.isNullOrEmpty()) {
                            val file = File(filePath)
                            val fileResult = HashMap<String, Any>()
                            fileResult[EmailDialog.DIALOG_TYPE] = EmailDialog.Type.ATTACH
                            fileResult[getString(R.string.attachment)] = file
                            onReturnValue(fileResult)
                        }
                    }
                }
            }
        }
    }

    private val smtpSettings: String
        get() {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val hostname = sharedPref.getString(getString(R.string.pref_email_hostname), "")
            val port = sharedPref.getInt(getString(R.string.pref_email_port), SmtpSettingFragment.DEFAULT_PORT)
            val username = sharedPref.getString(getString(R.string.pref_email_username), "")
            val password = sharedPref.getString(getString(R.string.pref_email_password), "")
            val domain = sharedPref.getString(getString(R.string.pref_email_domain), "")
            val transportMode = sharedPref.getString(getString(R.string.pref_email_transport_mode), SmtpAttributes.TransportMode.PLAIN.name)
            val connectionTimeout = sharedPref.getInt(getString(R.string.pref_email_connection_timeout), SmtpSettingFragment.DEFAULT_TIMEOUT)
            val readTimeout = sharedPref.getInt(getString(R.string.pref_email_read_timeout), SmtpSettingFragment.DEFAULT_TIMEOUT)
            return getString(R.string.smtp_setting_detail, hostname, port, username, password, domain, transportMode, connectionTimeout, readTimeout)
        }

    private val proxySettings: String
        get() {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val hostname = sharedPref.getString(ProxySettingFragment.PREF_EMAIL_PROXY_HOST, "")
            val port = sharedPref.getInt(ProxySettingFragment.PREF_EMAIL_PROXY_PORT, ProxySettingFragment.DEFAULT_PORT)
            val configurationMode = sharedPref.getString(ProxySettingFragment.PREF_EMAIL_PROXY_CONFIG_MODE, ProxyAttributes.ProxyConfigurationMode.NONE.name)
            return getString(R.string.proxy_setting_detail, hostname, port, configurationMode)
        }

    private fun addAttachment(file: File) {
        if (mAttachedFileList.contains(file)) {
            Toast.makeText(this, getString(R.string.selected_file), Toast.LENGTH_LONG).show()
        } else {
            mAttachedFileList.add(file)
            mAttachedFileAdapter.notifyDataSetChanged()
        }
    }

    private fun addMailView(parent: LinearLayout?, mail: EmailAddress, mailList: ArrayList<EmailAddress>?) {
        mailList?.add(mail)
        val mailLayout = layoutInflater
                .inflate(R.layout.layout_email_address, null) as ViewGroup
        val mailInfoTextView = mailLayout.findViewById<TextView>(R.id.mailTextView)
        val mailAddress = mail.address
        val mailName = mail.name
        val mailViewText: String = if (TextUtils.isEmpty(mailName)) {
            mailAddress
        } else {
            "$mailName <$mailAddress>"
        }
        mailInfoTextView.text = mailViewText
        val deleteMailButton = mailLayout.findViewById<Button>(R.id.deleteMailButton)
        deleteMailButton.setOnClickListener {
            mailList?.remove(mail)
            parent?.removeView(mailLayout)
        }
        val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(0, 0, 10, 0)
        mailLayout.layoutParams = params
        parent?.addView(mailLayout)
    }

    private inner class AttachAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return mAttachedFileList.size
        }

        override fun getItem(position: Int): File {
            return mAttachedFileList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, container: ViewGroup): View? {
            var view = convertView
            if (view == null) {
                view = layoutInflater.inflate(R.layout.adapter_attachment_list, container, false)
            }
            val file = mAttachedFileList[position]
            val fileNameView = view?.findViewById<TextView>(R.id.attachmentFilenameTextView)
            fileNameView?.text = file.name
            val delFileBtn = view?.findViewById<Button>(R.id.deleteAttachmentButton)
            delFileBtn?.setOnClickListener {
                mAttachedFileList.remove(file)
                mAttachedFileAdapter.notifyDataSetChanged()
            }
            return view
        }
    }

    fun handleComplete() {
        mBindingActivityMain.container.isEnabled = true
    }

    /**
     * Exception in could be because of following reasons
     *
     *  1. Library is not installed
     *  2. Library update is needed
     *  3. Version issue, unsupported
     *
     */
    fun handleException(t: Throwable?) {
        var errorMsg = ""
        if (t is SsdkUnsupportedException) {
            errorMsg = when (t.type) {
                SsdkUnsupportedException.LIBRARY_NOT_INSTALLED, SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED -> getString(R.string.sdk_support_missing)
                else -> getString(R.string.unknown_error)
            }
        } else {
            t?.message?.run {
                errorMsg = this
            }
        }
        Log.e(TAG, errorMsg)
        mAlertDialog = AlertDialog.Builder(this)
                .setTitle(getString(R.string.error))
                .setMessage(errorMsg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                .show()
    }

    fun setDefaultEmailAttributes(defaults: EmailAttributes) {
        defaults.from?.run {
            mBindingActivityMain.fromNameEditText.setText(name)
            mBindingActivityMain.fromAddressEditText.setText(address)
        } ?: run {
            mBindingActivityMain.fromNameEditText.setText("")
            mBindingActivityMain.fromAddressEditText.setText("")
        }
    }

    private fun setSendButtonsStatus() {
        mBindingActivityMain.sendWithSmtpButton.isEnabled = mBindingActivityMain.smtpEnableCheckBox.isChecked
        if (mBindingActivityMain.smtpEnableCheckBox.isChecked) {
            mBindingActivityMain.sendWithSmtpProxyButton.isEnabled = mBindingActivityMain.proxyEnableCheckBox.isChecked
        } else {
            mBindingActivityMain.sendWithSmtpProxyButton.isEnabled = false
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    companion object {
        const val TAG = "[SAMPLE]" + "Email"
        const val FILE_BROWSER_REQUEST_CODE = 1
    }
}