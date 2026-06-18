// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.hp.workpath.api.Result;
import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.api.authorization.EmailAddressInfo;
import com.hp.workpath.api.authorization.Permission;
import com.hp.workpath.api.authorization.PermissionToSignInMethod;
import com.hp.workpath.api.authorization.ProxyConfiguration;
import com.hp.workpath.api.authorization.SignInMethod;
import com.hp.workpath.api.authorization.UserOverrides;
import com.hp.workpath.api.config.ConfigService;
import com.hp.workpath.sample.authorization.databinding.ActivityMainBinding;
import com.hp.workpath.sample.authorization.exception.ResultException;
import com.hp.workpath.sample.authorization.fragments.MailFragment;
import com.hp.workpath.sample.authorization.fragments.PermissionsFragment;
import com.hp.workpath.sample.authorization.interfaces.IDialogFragmentListener;
import com.hp.workpath.sample.authorization.task.GetConfigurationTask;
import com.hp.workpath.sample.authorization.task.GetPermissionsTask;
import com.hp.workpath.sample.authorization.task.InitializationTask;
import com.hp.workpath.sample.authorization.task.SetConfigurationTask;
import com.hp.workpath.sample.authorization.task.SetConfigurationUsingDefaultConfigTask;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity implements IDialogFragmentListener {

    public static final String TAG = "[SAMPLE]" + "Authorization";
    public static final String SCREEN_4_3_INCH = "Screen_4.3_Inch";
    ActivityMainBinding mBinding;
    private AlertDialog mAlertDialog;

    /* Background task for Workpath SDK API initialization */
    private InitializationTask mInitializationTask;
    private ArrayList<EmailAddressInfo> mToList = new ArrayList<>();
    private ArrayList<EmailAddressInfo> mCcList = new ArrayList<>();
    private ArrayList<EmailAddressInfo> mBccList = new ArrayList<>();
    private Set<Permission> mGetPermissionSet = new HashSet<>();
    private Set<PermissionToSignInMethod> mPermissionToSignInMethodMap = new HashSet<>();

    private Snackbar mSnackBar;
    private DefaultConfigChangeObserver mDefaultConfigChangeObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (SCREEN_4_3_INCH.equals(findViewById(R.id.container).getTag())) {
            Toolbar toolBar = findViewById(R.id.toolbar);
            setSupportActionBar(toolBar);
        }

        // add click listener to call the MFP
        addListener();

        mDefaultConfigChangeObserver = new DefaultConfigChangeObserver(new Handler(Looper.getMainLooper()));
    }

    @Override
    protected void onResume() {
        super.onResume();

        mBinding.container.setEnabled(false);

        mInitializationTask = new InitializationTask(this);
        mInitializationTask.taskExecute();

        mDefaultConfigChangeObserver.register(getApplicationContext());

        if (mSnackBar == null) {
            mSnackBar = Snackbar.make(mBinding.container, "", Snackbar.LENGTH_INDEFINITE);
            View snackBarView = mSnackBar.getView();
            TextView tv = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
            tv.setMaxLines(3);
            mSnackBar.setAction(getString(android.R.string.ok), v -> {
                mSnackBar.dismiss();
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.version, menu);
        MenuItem versionMenu = menu.findItem(R.id.menuVersion);
        String version = "";
        try {
            Workpath sdkInfo = Workpath.getInstance();
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = getString(R.string.version, pInfo.versionName, pInfo.versionCode, sdkInfo.getVersionName(), sdkInfo.getVersionCode());
        } catch (Throwable t) {
            handleException(t);
        }
        versionMenu.setTitle(version);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        mInitializationTask.cancel();
        mInitializationTask = null;

        mDefaultConfigChangeObserver.unregister(getApplicationContext());

        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }

        if (mSnackBar != null) {
            mSnackBar.dismiss();
            mSnackBar = null;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addListener() {
        if (SCREEN_4_3_INCH.equals(findViewById(R.id.container).getTag())) {
            if (mBinding.floatSettings != null) {
                mBinding.floatSettings.setOnClickListener(view -> {
                    if (mBinding.buttonBar.getVisibility() == View.VISIBLE) {
                        mBinding.buttonBar.setVisibility(View.GONE);
                    } else {
                        mBinding.buttonBar.setVisibility(View.VISIBLE);
                    }
                });
            }
        }

        mBinding.addToButton.setOnClickListener(view -> showMailDialog(DialogType.Email.ADD_TO));
        mBinding.addCcButton.setOnClickListener(view -> showMailDialog(DialogType.Email.ADD_CC));
        mBinding.addBccButton.setOnClickListener(view -> showMailDialog(DialogType.Email.ADD_BCC));
        mBinding.getConfigurationButton.setOnClickListener(view -> displayConfiguration());
        mBinding.setConfigurationButton.setOnClickListener(view -> setConfiguration());
        mBinding.getPermissionButton.setOnClickListener(view -> showPermissionDialog(DialogType.Data.GET_PERMISSIONS, null));
        mBinding.getSignInMethodButton.setOnClickListener(view -> showPermissionDialog(DialogType.Data.GET_SIGN_IN_METHODS, null));
        mBinding.guestPermissionSetLayout.setOnClickListener(view -> showPermissionDialog(DialogType.Data.GUEST_PERMISSION_SET, mGetPermissionSet));
        mBinding.defaultSignInMethodEditText.setOnClickListener(view -> showPermissionDialog(DialogType.Data.DEFAULT_SIGN_IN_METHOD, null));
        mBinding.permissionIdEditText.setOnClickListener(view -> showPermissionDialog(DialogType.Data.PERMISSION_TO_SIGN_IN_METHOD__PERMISSION_ID, null));
        mBinding.signInMethodIdEditText.setOnClickListener(view -> showPermissionDialog(DialogType.Data.PERMISSION_TO_SIGN_IN_METHOD__SIGN_IN_METHOD, mPermissionToSignInMethodMap));
        mBinding.permissionToSignInMethodMapButton.setOnClickListener(view -> addPermissionToSignInMethodMapView());

        mBinding.disableAuthorizationCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dimLayout(mBinding.scrollView, isChecked);
        });

        mBinding.guestPermissionSetCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dimLayout(mBinding.guestPermissionSetLayout, isChecked);
        });
        mBinding.permissionToSignInMethodMapCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dimLayout(mBinding.permissionToSignInMethodMapLayout, isChecked);
        });
        mBinding.guestUserOverridesCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dimLayout(mBinding.guestUserOverridesLayout, isChecked);
        });
    }

    private void displayConfiguration() {
        try {
            clearAllViews();

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<ProxyConfiguration> future = executorService.submit(new GetConfigurationTask(getApplicationContext()));
            ProxyConfiguration proxyConfiguration = future.get();

            if (proxyConfiguration != null) {
                displayGuestPermissionSet(GetPermissionsTask.getPermissionsFromPermissionSet(getApplicationContext(), proxyConfiguration.getGuestPermissionSet()));
                displayDefaultSignInMethod(proxyConfiguration.getDefaultSignInMethod());
                if (proxyConfiguration.getPermissionToSignInMethodMap() != null) {
                    for (PermissionToSignInMethod permissionToSignInMethod : proxyConfiguration.getPermissionToSignInMethodMap()) {
                        displayPermissionToSignInMethodMap(permissionToSignInMethod.getPermissionId(), permissionToSignInMethod.getSignInMethodId());
                    }
                }
                displayGuestUserOverrides(proxyConfiguration.getGuestUserOverrides());

                mBinding.addNewPermissionToGuestPermissionSetCheckBox.setChecked(proxyConfiguration.isAddNewPermissionToGuestPermissionSet());
                mBinding.enableSignInChoiceCheckBox.setChecked(proxyConfiguration.isEnableSignInChoice());
            } else {
                showSnackBar(getString(R.string.proxy_not_configured));
            }
        } catch (ExecutionException ee) {
            Throwable cause = ee.getCause();
            if (cause instanceof ResultException) {
                Result result = ((ResultException) cause).getResult();
                showSnackBar(Logger.build(result));
            }
        } catch (Exception e) {
            showSnackBar(e.getMessage());
        }
    }

    private void showMailDialog(DialogType.Email type) {
        MailFragment mailDialog = new MailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DialogType.DIALOG_TYPE, type);
        mailDialog.setArguments(bundle);
        mailDialog.show(getSupportFragmentManager(), getString(R.string.email));
    }

    private <E extends Parcelable> void showPermissionDialog(DialogType.Data type, Set<E> list) {
        PermissionsFragment permissionsDialog = new PermissionsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DialogType.DIALOG_TYPE, type);
        if (list != null) {
            ArrayList<E> setList = new ArrayList<>(list);
            bundle.putParcelableArrayList(DialogType.DIALOG_DATA, setList);
        }
        permissionsDialog.setArguments(bundle);
        permissionsDialog.show(getSupportFragmentManager(), type.name());
    }

    private void addPermissionToSignInMethodMapView() {
        String permissionId = mBinding.permissionIdEditText.getText().toString();
        String signInMethodId = mBinding.signInMethodIdEditText.getText().toString();
        if (permissionId.isEmpty() || signInMethodId.isEmpty()) {
            showSnackBar(getString(R.string.permission_to_sign_in_method_empty));
        } else {
            Optional<PermissionToSignInMethod> result = mPermissionToSignInMethodMap.stream()
                    .filter(p -> p.getPermissionId().equals(permissionId))
                    .findFirst();

            if (result.isPresent()) {
                showSnackBar(getString(R.string.permission_to_sign_in_method_exist));
            } else {
                displayPermissionToSignInMethodMap(permissionId, signInMethodId);
            }
        }
    }

    private void dimLayout(View view, boolean isChecked) {
        if (isChecked) {
            view.setAlpha(0.5f);
            setViewAndChildrenEnabled(view, false);
        } else {
            view.setAlpha(1.0f);
            setViewAndChildrenEnabled(view, true);
        }
    }

    private void setViewAndChildrenEnabled(View view, boolean enabled) {
        if (!(view instanceof CheckBox))
            view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewAndChildrenEnabled(child, enabled);
            }
        }
    }

    @Override
    public void onDialogResult(HashMap<String, Object> result) {
        hideKeyboard(mBinding.container);
        if (result.get(DialogType.DIALOG_TYPE) instanceof DialogType.Email) {
            DialogType.Email type = (DialogType.Email) result.get(DialogType.DIALOG_TYPE);
            if (type != null) {
                EmailAddressInfo mail = (EmailAddressInfo) result.get(getString(R.string.email));
                switch (type) {
                    case ADD_TO:
                        displayMailView(mBinding.toListLayout, mail, mToList);
                        break;

                    case ADD_CC:
                        displayMailView(mBinding.ccListLayout, mail, mCcList);
                        break;

                    case ADD_BCC:
                        displayMailView(mBinding.bccListLayout, mail, mBccList);
                        break;
                }
            }
        } else if (result.get(DialogType.DIALOG_TYPE) instanceof DialogType.Data) {
            DialogType.Data type = (DialogType.Data) result.get(DialogType.DIALOG_TYPE);
            if (type != null) {
                Permission permission;
                SignInMethod signInMethod;
                switch (type) {
                    case GUEST_PERMISSION_SET:
                        List<Permission> permissions = (List<Permission>) result.get(type.name());
                        displayGuestPermissionSet(permissions);
                        break;

                    case DEFAULT_SIGN_IN_METHOD:
                        signInMethod = (SignInMethod) result.get(type.name());
                        displayDefaultSignInMethod(signInMethod.getId());
                        break;

                    case PERMISSION_TO_SIGN_IN_METHOD__PERMISSION_ID:
                        permission = (Permission) result.get(type.name());
                        mBinding.permissionIdEditText.setText(permission.getId());
                        break;

                    case PERMISSION_TO_SIGN_IN_METHOD__SIGN_IN_METHOD:
                        signInMethod = (SignInMethod) result.get(type.name());
                        mBinding.signInMethodIdEditText.setText(signInMethod.getId());
                        break;
                }
            }
        }
    }

    @Override
    public void onDialogError(Result result) {
        if (result != null) {
            showSnackBar(Logger.build(result));
        }
    }

    private void displayGuestPermissionSet(List<Permission> permissions) {
        mGetPermissionSet.clear();
        mBinding.guestPermissionSetInnerLayout.removeAllViews();
        if (permissions != null) {
            for (Permission p : permissions) {
                mGetPermissionSet.add(p);
                PermissionsFragment.addView(mBinding.guestPermissionSetInnerLayout, p, mGetPermissionSet);
            }
        }
    }

    private void displayPermissionToSignInMethodMap(String permissionId, String signInMethodId) {
        PermissionToSignInMethod permissionToSignInMethod = new PermissionToSignInMethod(permissionId, signInMethodId);
        mPermissionToSignInMethodMap.add(permissionToSignInMethod);
        PermissionsFragment.addView(mBinding.permissionToSignInMethodMapInnerLayout, permissionToSignInMethod, mPermissionToSignInMethodMap);
        mBinding.permissionIdEditText.setText("");
        mBinding.signInMethodIdEditText.setText("");
    }

    private void displayGuestUserOverrides(UserOverrides userOverrides) {
        if (userOverrides != null) {
            mBinding.faxBillingCodeEditText.setText(userOverrides.getFaxBillingCode());
            mBinding.faxCompanyNameEditText.setText(userOverrides.getFaxCompanyName());
            mBinding.subjectEditText.setText(userOverrides.getSubject());
            mBinding.messageEditText.setText(userOverrides.getMessage());
            if (userOverrides.getFrom() != null) {
                mBinding.fromNameEditText.setText(userOverrides.getFrom().getName());
                mBinding.fromAddressEditText.setText(userOverrides.getFrom().getAddress());
            }
            if (userOverrides.getTo() != null) {
                for (EmailAddressInfo mail : userOverrides.getTo()) {
                    displayMailView(mBinding.toListLayout, mail, mToList);
                }
            }
            if (userOverrides.getCc() != null) {
                for (EmailAddressInfo mail : userOverrides.getCc()) {
                    displayMailView(mBinding.ccListLayout, mail, mCcList);
                }
            }
            if (userOverrides.getBcc() != null) {
                for (EmailAddressInfo mail : userOverrides.getBcc()) {
                    displayMailView(mBinding.bccListLayout, mail, mBccList);
                }
            }
        }
    }

    private void displayMailView(LinearLayout layout, EmailAddressInfo mail, ArrayList<EmailAddressInfo> mailList) {
        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.layout_box, null);
        mailList.add(mail);
        MailFragment.addMailView(viewGroup, layout, mail, mailList);
    }

    private void displayDefaultSignInMethod(String id) {
        mBinding.defaultSignInMethodEditText.setText(id);
    }

    private void clearAllViews() {
        clearObjects();
        clearBoxViews();
        clearTexts(mBinding.container);
    }

    private void clearObjects() {
        mGetPermissionSet.clear();
        mPermissionToSignInMethodMap.clear();
        mToList.clear();
        mCcList.clear();
        mBccList.clear();
    }

    private void clearBoxViews() {
        mBinding.guestPermissionSetInnerLayout.removeAllViews();
        mBinding.permissionToSignInMethodMapInnerLayout.removeAllViews();
        mBinding.toListLayout.removeAllViews();
        mBinding.ccListLayout.removeAllViews();
        mBinding.bccListLayout.removeAllViews();
    }

    private void clearTexts(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                clearTexts((ViewGroup) child);
            } else if (child instanceof EditText) {
                ((EditText) child).setText("");
            } else if (child instanceof CheckBox) {
                ((CheckBox) child).setChecked(false);
            }
        }
    }

    public void handleComplete() {
        mBinding.container.setEnabled(true);
    }

    private void setConfiguration() {
        try {
            ProxyConfiguration proxyConfiguration;
            if (mBinding.disableAuthorizationCheckBox.isChecked()) {
                proxyConfiguration = null;
            } else {
                proxyConfiguration = new ProxyConfiguration.Builder()
                        .setGuestPermissionSet(!mBinding.guestPermissionSetCheckBox.isChecked() ?
                                (GetPermissionsTask.getPermissionSetFromPermissions(new ArrayList<>(mGetPermissionSet))) : null)
                        .setAddNewPermissionToGuestPermissionSet(mBinding.addNewPermissionToGuestPermissionSetCheckBox.isChecked())
                        .setEnableSignInChoice(mBinding.enableSignInChoiceCheckBox.isChecked())
                        .setDefaultSignInMethod(mBinding.defaultSignInMethodEditText.getText().toString())
                        .setGuestUserOverrides(!mBinding.guestUserOverridesCheckBox.isChecked() ? new UserOverrides.Builder()
                                .setToAddresses(mToList)
                                .setBccAddresses(mBccList)
                                .setCcAddresses(mCcList)
                                .setFrom(mBinding.fromAddressEditText.getText().toString(), mBinding.fromNameEditText.getText().toString())
                                .setMessage(mBinding.messageEditText.getText().toString())
                                .setSubject(mBinding.subjectEditText.getText().toString())
                                .setFaxBillingCode(mBinding.faxBillingCodeEditText.getText().toString())
                                .setFaxCompanyName(mBinding.faxCompanyNameEditText.getText().toString())
                                .build() : null)
                        .setPermissionToSignInMethodMap(!mBinding.permissionToSignInMethodMapCheckBox.isChecked() ?
                                new ArrayList<>(mPermissionToSignInMethodMap) : null)
                        .build();
                Log.i(TAG, "setConfiguration: " + proxyConfiguration.toString());
            }
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<Result> future = executorService.submit(new SetConfigurationTask(getApplicationContext(), proxyConfiguration));
            Result result = future.get();
            showSnackBar(Logger.build(result));

        } catch (Exception e) {
            showSnackBar(e.getMessage());
        }
    }

    /**
     * Exception in could be because of following reasons
     * <ol>
     * <li>Library is not installed</li>
     * <li>Library update is needed</li>
     * <li>Version issue, unsupported</li>
     * </ol>
     */
    public void handleException(final Throwable t) {
        String errorMsg;
        if (t instanceof SsdkUnsupportedException) {
            switch (((SsdkUnsupportedException) t).getType()) {
                case SsdkUnsupportedException.LIBRARY_NOT_INSTALLED:
                case SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED:
                    errorMsg = getString(R.string.sdk_support_missing);
                    break;
                default:
                    errorMsg = getString(R.string.unknown_error);
            }
        } else {
            errorMsg = t.getMessage();
        }
        Log.e(TAG, errorMsg);
        mAlertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error))
                .setMessage(errorMsg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .show();
    }

    private void showSnackBar(String message) {
        Log.i(TAG, message);
        if (mSnackBar != null) {
            mSnackBar.setText(message);
            mSnackBar.show();
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }

    private class DefaultConfigChangeObserver extends ConfigService.AbstractConfigChangeObserver {
        public DefaultConfigChangeObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(JSONObject updatedData) {
            mAlertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getString(R.string.default_config_observer))
                    .setMessage(getString(R.string.default_config_change))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        try {
                            ExecutorService executorService = Executors.newSingleThreadExecutor();
                            Future<Result> future = executorService.submit(new SetConfigurationUsingDefaultConfigTask(getApplicationContext()));
                            Result result = future.get();
                            showSnackBar(Logger.build(result));
                        } catch (ExecutionException ee) {
                            Throwable cause = ee.getCause();
                            if (cause instanceof ResultException) {
                                Result result = ((ResultException) cause).getResult();
                                showSnackBar(Logger.build(result));
                            }
                        } catch (Exception e) {
                            showSnackBar(e.getMessage());
                        }
                        dialog.dismiss();
                    })
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        }
    }
}