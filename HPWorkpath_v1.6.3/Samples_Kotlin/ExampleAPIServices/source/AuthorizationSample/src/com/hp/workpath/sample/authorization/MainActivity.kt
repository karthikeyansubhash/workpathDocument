// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import com.hp.workpath.api.Result
import com.hp.workpath.api.SsdkUnsupportedException
import com.hp.workpath.api.Workpath
import com.hp.workpath.api.authorization.EmailAddressInfo
import com.hp.workpath.api.authorization.Permission
import com.hp.workpath.api.authorization.PermissionToSignInMethod
import com.hp.workpath.api.authorization.ProxyConfiguration
import com.hp.workpath.api.authorization.SignInMethod
import com.hp.workpath.api.authorization.UserOverrides
import com.hp.workpath.api.config.ConfigService
import com.hp.workpath.sample.authorization.databinding.ActivityMainBinding
import com.hp.workpath.sample.authorization.exception.ResultException
import com.hp.workpath.sample.authorization.fragments.MailFragment
import com.hp.workpath.sample.authorization.fragments.PermissionsFragment
import com.hp.workpath.sample.authorization.interfaces.IDialogFragmentListener
import com.hp.workpath.sample.authorization.task.GetConfigurationTask
import com.hp.workpath.sample.authorization.task.GetPermissionsTask
import com.hp.workpath.sample.authorization.task.InitializationTask
import com.hp.workpath.sample.authorization.task.SetConfigurationTask
import com.hp.workpath.sample.authorization.task.SetConfigurationUsingDefaultConfigTask
import org.json.JSONObject
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), IDialogFragmentListener {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAlertDialog: AlertDialog
    private lateinit var mInitializationTask: InitializationTask
    private val mToList = ArrayList<EmailAddressInfo>()
    private val mCcList = ArrayList<EmailAddressInfo>()
    private val mBccList = ArrayList<EmailAddressInfo>()
    private val mGetPermissionSet = HashSet<Permission>()
    private val mPermissionToSignInMethodMap = HashSet<PermissionToSignInMethod>()
    private lateinit var mSnackBar: Snackbar
    private lateinit var mDefaultConfigChangeObserver: DefaultConfigChangeObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        if (SCREEN_4_3_INCH == findViewById<View>(R.id.container).tag) {
            val toolBar = findViewById<Toolbar>(R.id.toolbar)
            setSupportActionBar(toolBar)
        }

        // add click listener to call the MFP
        addListener()
        mDefaultConfigChangeObserver = DefaultConfigChangeObserver(Handler(Looper.getMainLooper()))
    }

    override fun onResume() {
        super.onResume()
        mBinding.container.isEnabled = false
        mInitializationTask = InitializationTask(this)
        mInitializationTask.taskExecute()
        mDefaultConfigChangeObserver.register(applicationContext)

        if (!::mSnackBar.isInitialized) {
            mSnackBar = Snackbar.make(mBinding.container, "", Snackbar.LENGTH_INDEFINITE)
            val snackBarView = mSnackBar.view
            val tv =
                snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            tv.maxLines = 3
            mSnackBar.setAction(getString(android.R.string.ok)) {
                mSnackBar.dismiss()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.version, menu)
        val versionMenu = menu.findItem(R.id.menuVersion)
        try {
            val sdkInfo = Workpath.getInstance()
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            versionMenu.title = getString(
                R.string.version,
                pInfo.versionName,
                pInfo.versionCode,
                sdkInfo.versionName,
                sdkInfo.versionCode
            )
        } catch (t: Throwable) {
            handleException(t)
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        mInitializationTask.cancel()
        mDefaultConfigChangeObserver.unregister(applicationContext)

        if (::mAlertDialog.isInitialized) {
            mAlertDialog.dismiss()
        }

        if (::mSnackBar.isInitialized) {
            mSnackBar.dismiss()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addListener() {
        if (SCREEN_4_3_INCH == findViewById<View>(R.id.container).tag) {
            mBinding.floatSettings?.setOnClickListener {
                mBinding.buttonBar.visibility =
                    if (mBinding.buttonBar.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
        }

        mBinding.addToButton.setOnClickListener { showMailDialog(DialogType.Email.ADD_TO) }
        mBinding.addCcButton.setOnClickListener { showMailDialog(DialogType.Email.ADD_CC) }
        mBinding.addBccButton.setOnClickListener { showMailDialog(DialogType.Email.ADD_BCC) }
        mBinding.getConfigurationButton.setOnClickListener { displayConfiguration() }
        mBinding.setConfigurationButton.setOnClickListener { setConfiguration() }
        mBinding.getPermissionButton.setOnClickListener {
            showPermissionDialog<Permission>(
                DialogType.Data.GET_PERMISSIONS,
                null
            )
        }
        mBinding.getSignInMethodButton.setOnClickListener {
            showPermissionDialog<Permission>(
                DialogType.Data.GET_SIGN_IN_METHODS,
                null
            )
        }
        mBinding.guestPermissionSetLayout.setOnClickListener {
            showPermissionDialog(
                DialogType.Data.GUEST_PERMISSION_SET,
                mGetPermissionSet
            )
        }
        mBinding.defaultSignInMethodEditText.setOnClickListener {
            showPermissionDialog<Permission>(
                DialogType.Data.DEFAULT_SIGN_IN_METHOD,
                null
            )
        }
        mBinding.permissionIdEditText.setOnClickListener {
            showPermissionDialog<Permission>(
                DialogType.Data.PERMISSION_TO_SIGN_IN_METHOD__PERMISSION_ID,
                null
            )
        }
        mBinding.signInMethodIdEditText.setOnClickListener {
            showPermissionDialog(
                DialogType.Data.PERMISSION_TO_SIGN_IN_METHOD__SIGN_IN_METHOD,
                mPermissionToSignInMethodMap
            )
        }
        mBinding.permissionToSignInMethodMapButton.setOnClickListener { addPermissionToSignInMethodMapView() }
        mBinding.disableAuthorizationCheckBox.setOnCheckedChangeListener { _, isChecked ->
            mBinding.scrollView?.let {
                dimLayout(it, isChecked)
            }

        }
        mBinding.guestPermissionSetCheckBox.setOnCheckedChangeListener { _, isChecked ->
            dimLayout(
                mBinding.guestPermissionSetLayout,
                isChecked
            )
        }
        mBinding.permissionToSignInMethodMapCheckBox.setOnCheckedChangeListener { _, isChecked ->
            dimLayout(
                mBinding.permissionToSignInMethodMapLayout,
                isChecked
            )
        }
        mBinding.guestUserOverridesCheckBox.setOnCheckedChangeListener { _, isChecked ->
            dimLayout(
                mBinding.guestUserOverridesLayout,
                isChecked
            )
        }
    }

    private fun displayConfiguration() {
        try {
            clearAllViews()
            val executorService = Executors.newSingleThreadExecutor()
            val future = executorService.submit(GetConfigurationTask(applicationContext))
            val proxyConfiguration = future.get()
            proxyConfiguration?.let {
                displayGuestPermissionSet(
                    GetPermissionsTask.getPermissionsFromPermissionSet(
                        applicationContext,
                        it.guestPermissionSet
                    )
                )
                displayDefaultSignInMethod(it.defaultSignInMethod)
                it.permissionToSignInMethodMap?.forEach { permissionToSignInMethod ->
                    displayPermissionToSignInMethodMap(
                        permissionToSignInMethod.permissionId,
                        permissionToSignInMethod.signInMethodId
                    )
                }
                if (it.guestUserOverrides != null)
                    displayGuestUserOverrides(it.guestUserOverrides)
                mBinding.addNewPermissionToGuestPermissionSetCheckBox.isChecked =
                    it.isAddNewPermissionToGuestPermissionSet
                mBinding.enableSignInChoiceCheckBox.isChecked = it.isEnableSignInChoice
            } ?: showSnackBar(getString(R.string.proxy_not_configured))
        } catch (ee: ExecutionException) {
            val cause = ee.cause
            if (cause is ResultException) {
                val result = cause.result
                showSnackBar(Logger.build(result))
            }
            Log.e(TAG, cause?.message ?: "Error occurred")
        } catch (e: Exception) {
            showSnackBar(e.message ?: "Error")
        }
    }

    private fun showMailDialog(type: DialogType.Email) {
        val mailDialog = MailFragment()
        val bundle = Bundle()
        bundle.putSerializable(DialogType.DIALOG_TYPE, type)
        mailDialog.arguments = bundle
        mailDialog.show(supportFragmentManager, getString(R.string.email))
    }

    private fun <E : Parcelable> showPermissionDialog(
        type: DialogType.Data,
        list: kotlin.collections.Set<E>?
    ) {
        val permissionsDialog = PermissionsFragment()
        val bundle = Bundle()
        bundle.putSerializable(DialogType.DIALOG_TYPE, type)
        list?.let {
            val setList = ArrayList(it)
            bundle.putParcelableArrayList(DialogType.DIALOG_DATA, setList)
        }
        permissionsDialog.arguments = bundle
        permissionsDialog.show(supportFragmentManager, type.name)
    }

    private fun addPermissionToSignInMethodMapView() {
        val permissionId = mBinding.permissionIdEditText.text.toString()
        val signInMethodId = mBinding.signInMethodIdEditText.text.toString()
        if (permissionId.isEmpty() || signInMethodId.isEmpty()) {
            showSnackBar(getString(R.string.permission_to_sign_in_method_empty))
        } else {
            val result = mPermissionToSignInMethodMap.stream()
                .filter { it.permissionId == permissionId }
                .findFirst()
            if (result.isPresent) {
                showSnackBar(getString(R.string.permission_to_sign_in_method_exist))
            } else {
                displayPermissionToSignInMethodMap(permissionId, signInMethodId)
            }
        }
    }

    private fun dimLayout(view: View, isChecked: Boolean) {
        view.alpha = if (isChecked) 0.5f else 1.0f
        setViewAndChildrenEnabled(view, !isChecked)
    }

    private fun setViewAndChildrenEnabled(view: View, enabled: Boolean) {
        if (view !is CheckBox) view.isEnabled = enabled
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                setViewAndChildrenEnabled(child, enabled)
            }
        }
    }

    override fun onDialogResult(result: HashMap<String, Any>) {
        hideKeyboard(mBinding.container)
        when (val type = result[DialogType.DIALOG_TYPE]) {
            is DialogType.Email -> {
                val mail = result[getString(R.string.email)] as EmailAddressInfo
                when (type) {
                    DialogType.Email.ADD_TO -> displayMailView(mBinding.toListLayout, mail, mToList)
                    DialogType.Email.ADD_CC -> displayMailView(mBinding.ccListLayout, mail, mCcList)
                    DialogType.Email.ADD_BCC -> displayMailView(
                        mBinding.bccListLayout,
                        mail,
                        mBccList
                    )
                }
            }

            is DialogType.Data -> {
                when (type) {
                    DialogType.Data.GUEST_PERMISSION_SET -> {
                        val permissions = result[type.name] as List<Permission>
                        displayGuestPermissionSet(permissions)
                    }

                    DialogType.Data.DEFAULT_SIGN_IN_METHOD -> {
                        val signInMethod = result[type.name] as SignInMethod
                        displayDefaultSignInMethod(signInMethod.id)
                    }

                    DialogType.Data.PERMISSION_TO_SIGN_IN_METHOD__PERMISSION_ID -> {
                        val permission = result[type.name] as Permission
                        mBinding.permissionIdEditText.setText(permission.id)
                    }

                    DialogType.Data.PERMISSION_TO_SIGN_IN_METHOD__SIGN_IN_METHOD -> {
                        val signInMethod = result[type.name] as SignInMethod
                        mBinding.signInMethodIdEditText.setText(signInMethod.id)
                    }

                    else -> {
                        Log.i(TAG, "onDialogResult other type: $type")
                    }
                }
            }
        }
    }

    override fun onDialogError(result: Result) {
        showSnackBar(Logger.build(result))
    }

    private fun displayGuestPermissionSet(permissions: List<Permission>?) {
        mGetPermissionSet.clear()
        mBinding.guestPermissionSetInnerLayout.removeAllViews()
        permissions?.forEach { permission ->
            mGetPermissionSet.add(permission)
            PermissionsFragment.addView(
                mBinding.guestPermissionSetInnerLayout,
                permission,
                mGetPermissionSet
            )
        }
    }

    private fun displayPermissionToSignInMethodMap(permissionId: String, signInMethodId: String) {
        val permissionToSignInMethod = PermissionToSignInMethod(permissionId, signInMethodId)
        mPermissionToSignInMethodMap.add(permissionToSignInMethod)
        PermissionsFragment.addView(
            mBinding.permissionToSignInMethodMapInnerLayout,
            permissionToSignInMethod,
            mPermissionToSignInMethodMap
        )
        mBinding.permissionIdEditText.setText("")
        mBinding.signInMethodIdEditText.setText("")
    }

    private fun displayGuestUserOverrides(userOverrides: UserOverrides) {
        mBinding.faxBillingCodeEditText.setText(userOverrides.faxBillingCode)
        mBinding.faxCompanyNameEditText.setText(userOverrides.faxCompanyName)
        mBinding.subjectEditText.setText(userOverrides.subject)
        mBinding.messageEditText.setText(userOverrides.message)
        userOverrides.from?.let {
            mBinding.fromNameEditText.setText(it.name)
            mBinding.fromAddressEditText.setText(it.address)
        }
        userOverrides.to?.forEach { mail ->
            displayMailView(mBinding.toListLayout, mail, mToList)
        }
        userOverrides.cc?.forEach { mail ->
            displayMailView(mBinding.ccListLayout, mail, mCcList)
        }
        userOverrides.bcc?.forEach { mail ->
            displayMailView(mBinding.bccListLayout, mail, mBccList)
        }
    }

    private fun displayMailView(
        layout: LinearLayout,
        mail: EmailAddressInfo,
        mailList: ArrayList<EmailAddressInfo>
    ) {
        val viewGroup = layoutInflater.inflate(R.layout.layout_box, null) as ViewGroup
        mailList.add(mail)
        MailFragment.addMailView(viewGroup, layout, mail, mailList)
    }

    private fun displayDefaultSignInMethod(id: String) {
        mBinding.defaultSignInMethodEditText.setText(id)
    }

    private fun clearAllViews() {
        clearObjects()
        clearBoxViews()
        clearTexts(mBinding.container)
    }

    private fun clearObjects() {
        mGetPermissionSet.clear()
        mPermissionToSignInMethodMap.clear()
        mToList.clear()
        mCcList.clear()
        mBccList.clear()
    }

    private fun clearBoxViews() {
        mBinding.guestPermissionSetInnerLayout.removeAllViews()
        mBinding.permissionToSignInMethodMapInnerLayout.removeAllViews()
        mBinding.toListLayout.removeAllViews()
        mBinding.ccListLayout.removeAllViews()
        mBinding.bccListLayout.removeAllViews()
    }

    private fun clearTexts(viewGroup: ViewGroup) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            when (child) {
                is ViewGroup -> clearTexts(child)
                is EditText -> child.setText("")
                is CheckBox -> child.isChecked = false
            }
        }
    }

    fun handleComplete() {
        mBinding.container.isEnabled = true
    }

    private fun setConfiguration() {
        try {
            val proxyConfiguration = if (mBinding.disableAuthorizationCheckBox.isChecked) {
                null
            } else {
                ProxyConfiguration.Builder()
                    .setGuestPermissionSet(
                        if (!mBinding.guestPermissionSetCheckBox.isChecked) GetPermissionsTask.getPermissionSetFromPermissions(
                            ArrayList(mGetPermissionSet)
                        ) else null
                    )
                    .setAddNewPermissionToGuestPermissionSet(mBinding.addNewPermissionToGuestPermissionSetCheckBox.isChecked)
                    .setEnableSignInChoice(mBinding.enableSignInChoiceCheckBox.isChecked)
                    .setDefaultSignInMethod(mBinding.defaultSignInMethodEditText.text.toString())
                    .setGuestUserOverrides(
                        if (!mBinding.guestUserOverridesCheckBox.isChecked) UserOverrides.Builder()
                            .setToAddresses(mToList)
                            .setBccAddresses(mBccList)
                            .setCcAddresses(mCcList)
                            .setFrom(
                                mBinding.fromAddressEditText.text.toString(),
                                mBinding.fromNameEditText.text.toString()
                            )
                            .setMessage(mBinding.messageEditText.text.toString())
                            .setSubject(mBinding.subjectEditText.text.toString())
                            .setFaxBillingCode(mBinding.faxBillingCodeEditText.text.toString())
                            .setFaxCompanyName(mBinding.faxCompanyNameEditText.text.toString())
                            .build() else null
                    )
                    .setPermissionToSignInMethodMap(
                        if (!mBinding.permissionToSignInMethodMapCheckBox.isChecked) ArrayList(
                            mPermissionToSignInMethodMap
                        ) else null
                    )
                    .build()
            }
            Log.i(TAG, "setConfiguration: $proxyConfiguration")
            val executorService = Executors.newSingleThreadExecutor()
            val future =
                executorService.submit(SetConfigurationTask(applicationContext, proxyConfiguration))
            val result = future.get()
            showSnackBar(Logger.build(result))
        } catch (e: Exception) {
            showSnackBar(e.message ?: "Error")
        }
    }

    fun handleException(t: Throwable?) {
        val errorMsg = when (t) {
            is SsdkUnsupportedException -> when (t.type) {
                SsdkUnsupportedException.LIBRARY_NOT_INSTALLED, SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED -> getString(
                    R.string.sdk_support_missing
                )

                else -> getString(R.string.unknown_error)
            }

            else -> t?.message ?: "Unknown error"
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

    private fun showSnackBar(message: String) {
        Log.i(TAG, message)
        mSnackBar.let {
            it.setText(message)
            it.show()
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }

    private inner class DefaultConfigChangeObserver(handler: Handler) :
        ConfigService.AbstractConfigChangeObserver(handler) {
        override fun onChange(updatedData: JSONObject) {
            mAlertDialog = AlertDialog.Builder(this@MainActivity)
                .setTitle(getString(R.string.default_config_observer))
                .setMessage(getString(R.string.default_config_change))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    try {
                        val executorService = Executors.newSingleThreadExecutor()
                        val future = executorService.submit(
                            SetConfigurationUsingDefaultConfigTask(applicationContext)
                        )
                        val result = future.get()
                        showSnackBar(Logger.build(result))
                    } catch (ee: ExecutionException) {
                        val cause = ee.cause
                        if (cause is ResultException) {
                            val result = cause.result
                            showSnackBar(Logger.build(result))
                        }
                        Log.e(TAG, cause?.message ?: "Error occurred")
                    } catch (e: Exception) {
                        showSnackBar(e.message ?: "Error")
                    }
                    dialog.dismiss()
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    companion object {
        const val TAG = "[SAMPLE]Authorization"
        public const val SCREEN_4_3_INCH = "Screen_4.3_Inch"
    }
}
