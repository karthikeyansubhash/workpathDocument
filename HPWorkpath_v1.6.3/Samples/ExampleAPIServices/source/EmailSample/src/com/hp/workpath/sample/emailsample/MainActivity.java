// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.helper.email.EmailAddressInfo;
import com.hp.workpath.api.helper.email.EmailAttributes;
import com.hp.workpath.api.helper.email.ProxyAttributes;
import com.hp.workpath.api.helper.email.SmtpAttributes;
import com.hp.workpath.sample.emailsample.filebrowser.FileUtils;
import com.hp.workpath.sample.emailsample.fragments.AddMailFragment;
import com.hp.workpath.sample.emailsample.fragments.ProxySettingFragment;
import com.hp.workpath.sample.emailsample.fragments.SmtpSettingFragment;
import com.hp.workpath.sample.emailsample.interfaces.IDialogFragmentListener;
import com.hp.workpath.sample.emailsample.model.EmailAddress;
import com.hp.workpath.sample.emailsample.model.EmailInfo;
import com.hp.workpath.sample.emailsample.task.InitializationTask;
import com.hp.workpath.sample.emailsample.task.LoadEmailDefaultsTask;
import com.hp.workpath.sample.emailsample.task.SendEmailTask;
import com.hp.workpath.sample.emailsample.task.SendEmailTask.SendType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements IDialogFragmentListener {

    public static final String TAG = "[SAMPLE]" + "Email";

    public static final int FILE_BROWSER_REQUEST_CODE = 1;

    private static final String SCREEN_4_3_INCH = "Screen_4.3_Inch";

    private View mContainer;
    private AlertDialog mAlertDialog;

    /* Background task for Workpath SDK API initialization */
    private InitializationTask mInitializationTask;

    //From
    private EditText mFromNameEditText;
    private EditText mFromAddressEditText;

    //To, Cc, Bcc
    private LinearLayout mToListLayout;
    private LinearLayout mCcListLayout;
    private LinearLayout mBccListLayout;

    private Button mAddMailToButton;
    private Button mAddMailCcButton;
    private Button mAddMailBccButton;

    private TextView mToTextView;
    private TextView mCcTextView;
    private TextView mBccTextView;

    private ArrayList<EmailAddress> mToList;
    private ArrayList<EmailAddress> mCcList;
    private ArrayList<EmailAddress> mBccList;

    //Attachment
    private TextView mAttachedFileCounter;
    private ArrayList<File> mAttachedFileList;
    private AttachAdapter mAttachedFileAdapter;
    private Button mAttachedFileButton;
    private ListView mAttachedFileListView;
    private DataSetObserver mAttachedFileDataSetObserver;

    //body
    private EditText mSubjectEditText;
    private EditText mMessageEditText;

    //smtp, proxy settings
    private RelativeLayout mSmtpDetailLayout;
    private RelativeLayout mProxyDetailLayout;
    private TextView mSmtpTextView;
    private TextView mProxyTextView;
    private CheckBox mSmtpEnableCheckBox;
    private CheckBox mProxyEnableCheckBox;

    //bottom
    private Button mGetDefaultButton;
    private Button mSendButton;
    private Button mSendWithSmtpButton;
    private Button mSendWithSmtpProxyButton;

    //progress bar
    private ProgressBar mProgressBar;

    //float settings
    private FloatingActionButton mFloatingActionButton;
    private LinearLayout mButtonBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (SCREEN_4_3_INCH.equals(findViewById(R.id.container).getTag())) {
            Toolbar toolBar = findViewById(R.id.toolbar);
            setSupportActionBar(toolBar);
        }

        // find the text and button
        initView();

        // add click listener to call the MFP
        addListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mContainer.setEnabled(false);

        mInitializationTask = new InitializationTask(this);
        mInitializationTask.taskExecute();
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

        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }

    private void initView() {
        findViewElements();

        mAttachedFileList = new ArrayList<>();
        mToList = new ArrayList<>();
        mCcList = new ArrayList<>();
        mBccList = new ArrayList<>();

        mAttachedFileAdapter = new AttachAdapter();
        mAttachedFileListView.setAdapter(mAttachedFileAdapter);
    }

    private void findViewElements() {
        mContainer = findViewById(R.id.container);

        mFromNameEditText = findViewById(R.id.fromNameEditText);
        mFromAddressEditText = findViewById(R.id.fromAddressEditText);

        mToListLayout = findViewById(R.id.toListLayout);
        mCcListLayout = findViewById(R.id.ccListLayout);
        mBccListLayout = findViewById(R.id.bccListLayout);

        mAddMailToButton = findViewById(R.id.addToButton);
        mAddMailCcButton = findViewById(R.id.addCcButton);
        mAddMailBccButton = findViewById(R.id.addBccButton);

        mToTextView = findViewById(R.id.toTextView);
        mCcTextView = findViewById(R.id.ccTextView);
        mBccTextView = findViewById(R.id.bccTextView);

        mSubjectEditText = findViewById(R.id.subjectEditText);
        mMessageEditText = findViewById(R.id.messageEditText);

        mAttachedFileCounter = findViewById(R.id.attachedFileCounter);
        mAttachedFileListView = findViewById(R.id.attachmentListView);
        mAttachedFileButton = findViewById(R.id.attachButton);
        mAttachedFileCounter.setText(getString(R.string.attachment_count, 0));

        mSmtpDetailLayout = findViewById(R.id.smtpDetailLayout);
        mProxyDetailLayout = findViewById(R.id.proxyDetailLayout);

        mSmtpTextView = findViewById(R.id.smtpTextView);
        mProxyTextView = findViewById(R.id.proxyTextView);

        mSmtpEnableCheckBox = findViewById(R.id.smtpEnableCheckBox);
        mProxyEnableCheckBox = findViewById(R.id.proxyEnableCheckBox);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPref.edit()
                .putBoolean(getString(R.string.smtp), false)
                .putBoolean(getString(R.string.proxy), false)
                .apply();

        mGetDefaultButton = findViewById(R.id.getDefaultButton);
        mSendButton = findViewById(R.id.sendButton);
        mSendWithSmtpButton = findViewById(R.id.sendWithSmtpButton);
        mSendWithSmtpProxyButton = findViewById(R.id.sendWithSmtpProxyButton);
        setSendButtonsStatus();

        mProgressBar = findViewById(R.id.progressbar);

        mFloatingActionButton = findViewById(R.id.floatSettings);
        mButtonBar = findViewById(R.id.buttonBar);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addListener() {
        if (SCREEN_4_3_INCH.equals(findViewById(R.id.container).getTag())) {
            mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mButtonBar.getVisibility() == View.VISIBLE) {
                        mButtonBar.setVisibility(View.GONE);
                    } else {
                        mButtonBar.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
        mAttachedFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FileBrowserActivity.class);
                startActivityForResult(intent, FILE_BROWSER_REQUEST_CODE);
            }
        });

        mSmtpDetailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SmtpSettingFragment().show(getSupportFragmentManager(), getString(R.string.smtp));
            }
        });

        mProxyDetailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ProxySettingFragment().show(getSupportFragmentManager(), getString(R.string.proxy));
            }
        });

        View.OnClickListener toClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddMailDialog(EmailDialog.Type.ADD_TO);
            }
        };
        mToTextView.setOnClickListener(toClickListener);
        mAddMailToButton.setOnClickListener(toClickListener);

        View.OnClickListener ccClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddMailDialog(EmailDialog.Type.ADD_CC);
            }
        };
        mCcTextView.setOnClickListener(ccClickListener);
        mAddMailCcButton.setOnClickListener(ccClickListener);

        View.OnClickListener bccClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddMailDialog(EmailDialog.Type.ADD_BCC);
            }
        };
        mBccTextView.setOnClickListener(bccClickListener);
        mAddMailBccButton.setOnClickListener(bccClickListener);

        View.OnTouchListener allowScrollListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent ev) {
                int action = ev.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                view.onTouchEvent(ev);
                return true;
            }
        };
        mMessageEditText.setOnTouchListener(allowScrollListener);
        mAttachedFileListView.setOnTouchListener(allowScrollListener);

        mAttachedFileDataSetObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mAttachedFileCounter.setText(getString(R.string.attachment_count, mAttachedFileList.size()));
            }
        };
        mAttachedFileAdapter.registerDataSetObserver(mAttachedFileDataSetObserver);

        mSmtpEnableCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view;
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if (checkBox.isChecked()) {
                    if (sharedPref.getString(getString(R.string.pref_email_hostname), null) == null) {
                        Toast.makeText(getApplicationContext(), getString(R.string.no_smtp_setting),
                                Toast.LENGTH_SHORT).show();
                        checkBox.setChecked(false);
                    } else {
                        mSmtpTextView.setText(getSmtpSettings());
                    }
                }
                sharedPref.edit().putBoolean(getString(R.string.smtp), checkBox.isChecked()).apply();
            }
        });
        mSmtpEnableCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSendButtonsStatus();
            }
        });

        mProxyEnableCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view;
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if (checkBox.isChecked()) {
                    if (sharedPref.getString(ProxySettingFragment.PREF_EMAIL_PROXY_HOST, null) == null) {
                        Toast.makeText(getApplicationContext(), getString(R.string.no_proxy_setting),
                                Toast.LENGTH_SHORT).show();
                        checkBox.setChecked(false);
                    } else {
                        mProxyTextView.setText(getProxySettings());
                    }
                }
                sharedPref.edit().putBoolean(getString(R.string.proxy), checkBox.isChecked()).apply();
            }
        });
        mProxyEnableCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setSendButtonsStatus();
            }
        });

        mGetDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDefaults();
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail(SendType.SEND);
            }
        });

        mSendWithSmtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail(SendType.SEND_WITH_SMTP);
            }
        });

        mSendWithSmtpProxyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail(SendType.SEND_WITH_SMTP_PROXY);
            }
        });
    }

    private void loadDefaults() {
        showProgressBar(View.VISIBLE);
        new LoadEmailDefaultsTask(MainActivity.this).taskExecute();
    }

    private void sendEmail(SendType sendType) {
        EmailInfo emailInfo = new EmailInfo();
        emailInfo.setAttachments(mAttachedFileList.toArray(new File[mAttachedFileList.size()]));
        emailInfo.setBcc(mBccList);
        emailInfo.setCc(mCcList);
        emailInfo.setTo(mToList);
        String fromAddress = mFromAddressEditText.getText().toString();
        String fromName = mFromNameEditText.getText().toString();
        emailInfo.setFrom(new EmailAddress(fromAddress, fromName));

        if (!TextUtils.isEmpty(mMessageEditText.getText().toString())) {
            emailInfo.setMessage(mMessageEditText.getText().toString());
        }
        if (!TextUtils.isEmpty(mSubjectEditText.getText().toString())) {
            emailInfo.setSubject(mSubjectEditText.getText().toString());
        }

        showProgressBar(View.VISIBLE);
        new SendEmailTask(this, sendType, emailInfo).taskExecute();
    }

    public void showProgressBar(int visibility) {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(visibility);
        }
    }

    private void showAddMailDialog(EmailDialog.Type type) {
        AddMailFragment mailDialog = new AddMailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EmailDialog.DIALOG_TYPE, type);
        mailDialog.setArguments(bundle);
        mailDialog.show(getSupportFragmentManager(), getString(R.string.email));
    }

    @Override
    public void onReturnValue(HashMap<String, Object> result) {
        EmailAddress mail;

        hideKeyboard(mContainer);

        EmailDialog.Type dType = (EmailDialog.Type) result.get(EmailDialog.DIALOG_TYPE);
        switch (dType) {
            case ATTACH:
                addAttachment((File) result.get(getString(R.string.attachment)));
                break;

            case ADD_TO:
                mail = (EmailAddress) result.get(getString(R.string.email));
                addMailView(mToListLayout, mail, mToList);
                break;

            case ADD_CC:
                mail = (EmailAddress) result.get(getString(R.string.email));
                addMailView(mCcListLayout, mail, mCcList);
                break;

            case ADD_BCC:
                mail = (EmailAddress) result.get(getString(R.string.email));
                addMailView(mBccListLayout, mail, mBccList);
                break;

            case SMTP:
                mSmtpTextView.setText(getSmtpSettings());
                mSmtpEnableCheckBox.setChecked(true);
                break;

            case PROXY:
                mProxyTextView.setText(getProxySettings());
                mProxyEnableCheckBox.setChecked(true);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAttachedFileAdapter.unregisterDataSetObserver(mAttachedFileDataSetObserver);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_BROWSER_REQUEST_CODE && data != null) {
            String filePath = data.getStringExtra(FileUtils.PATH);
            if (!TextUtils.isEmpty(filePath)) {
                File file = new File(filePath);
                HashMap<String, Object> result = new HashMap<>();
                result.put(EmailDialog.DIALOG_TYPE, EmailDialog.Type.ATTACH);
                result.put(getString(R.string.attachment), file);
                onReturnValue(result);
            }
        }
    }

    private String getSmtpSettings() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String hostname = sharedPref.getString(getString(R.string.pref_email_hostname), "");
        int port = sharedPref.getInt(getString(R.string.pref_email_port), SmtpSettingFragment.DEFAULT_PORT);
        String username = sharedPref.getString(getString(R.string.pref_email_username), "");
        String password = sharedPref.getString(getString(R.string.pref_email_password), "");
        String domain = sharedPref.getString(getString(R.string.pref_email_domain), "");
        String transportMode = sharedPref.getString(getString(R.string.pref_email_transport_mode), SmtpAttributes.TransportMode.PLAIN.name());
        int connectionTimeout = sharedPref.getInt(getString(R.string.pref_email_connection_timeout), SmtpSettingFragment.DEFAULT_TIMEOUT);
        int readTimeout = sharedPref.getInt(getString(R.string.pref_email_read_timeout), SmtpSettingFragment.DEFAULT_TIMEOUT);
        String smtpResult = getString(R.string.smtp_setting_detail, hostname, port, username, password, domain, transportMode, connectionTimeout, readTimeout);
        return smtpResult;
    }

    private String getProxySettings() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String hostname = sharedPref.getString(ProxySettingFragment.PREF_EMAIL_PROXY_HOST, "");
        int port = sharedPref.getInt(ProxySettingFragment.PREF_EMAIL_PROXY_PORT, ProxySettingFragment.DEFAULT_PORT);
        String configurationMode = sharedPref.getString(ProxySettingFragment.PREF_EMAIL_PROXY_CONFIG_MODE, ProxyAttributes.ProxyConfigurationMode.NONE.name());
        String proxyResult = getString(R.string.proxy_setting_detail, hostname, port, configurationMode);
        return proxyResult;
    }

    private void addAttachment(File file) {
        if (mAttachedFileList.contains(file)) {
            Toast.makeText(this, getString(R.string.selected_file), Toast.LENGTH_LONG).show();
        } else {
            mAttachedFileList.add(file);
            mAttachedFileAdapter.notifyDataSetChanged();
        }
    }

    private void addMailView(final LinearLayout parent, final EmailAddress mail, final ArrayList<EmailAddress> mailList) {
        mailList.add(mail);

        final ViewGroup mailLayout = (ViewGroup) getLayoutInflater()
                .inflate(R.layout.layout_email_address, null);

        TextView mailInfoTextView = mailLayout.findViewById(R.id.mailTextView);
        String mailAddress = mail.getAddress();
        String mailName = mail.getName();
        String mailViewText;
        if (TextUtils.isEmpty(mailName)) {
            mailViewText = mailAddress;
        } else {
            mailViewText = mailName + " <" + mailAddress + ">";
        }
        mailInfoTextView.setText(mailViewText);

        Button deleteMailButton = mailLayout.findViewById(R.id.deleteMailButton);

        deleteMailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mailList.remove(mail);
                parent.removeView(mailLayout);
            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 0, 10, 0);
        mailLayout.setLayoutParams(params);
        parent.addView(mailLayout);

    }

    private class AttachAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mAttachedFileList.size();
        }

        @Override
        public File getItem(int position) {
            return mAttachedFileList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.adapter_attachment_list, container, false);
            }
            final File file = mAttachedFileList.get(position);
            TextView fileNameView = convertView.findViewById(R.id.attachmentFilenameTextView);
            fileNameView.setText(file.getName());

            Button delFileBtn = convertView.findViewById(R.id.deleteAttachmentButton);

            delFileBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAttachedFileList.remove(file);
                    mAttachedFileAdapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }
    }

    public void handleComplete() {
        mContainer.setEnabled(true);
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
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .show();
    }

    public void setDefaultEmailAttributes(EmailAttributes defaults) {
        if (defaults != null && defaults.getFrom() != null) {
            EmailAddressInfo from = defaults.getFrom();
            mFromNameEditText.setText(from != null ? from.getName() : "");
            mFromAddressEditText.setText(from != null ? from.getAddress() : "");
        }
    }

    private void setSendButtonsStatus() {

        mSendWithSmtpButton.setEnabled(mSmtpEnableCheckBox.isChecked());
        if (mSmtpEnableCheckBox.isChecked()) {
            mSendWithSmtpProxyButton.setEnabled(mProxyEnableCheckBox.isChecked());
        } else {
            mSendWithSmtpProxyButton.setEnabled(false);
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }
}