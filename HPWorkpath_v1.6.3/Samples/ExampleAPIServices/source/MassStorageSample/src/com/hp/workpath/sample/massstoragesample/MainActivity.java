// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.hp.workpath.api.SsdkUnsupportedException;
import com.hp.workpath.api.Workpath;
import com.hp.workpath.api.massstorage.CustomerDataFile;
import com.hp.workpath.api.massstorage.MassStorageInfo;
import com.hp.workpath.api.massstorage.MassStorageService;
import com.hp.workpath.sample.massstoragesample.fragments.FileCreateFragment;
import com.hp.workpath.sample.massstoragesample.fragments.FileInfoFragment;
import com.hp.workpath.sample.massstoragesample.fragments.FileRenameFragment;
import com.hp.workpath.sample.massstoragesample.fragments.ISelectedStorageListener;
import com.hp.workpath.sample.massstoragesample.fragments.NewFileFragment;
import com.hp.workpath.sample.massstoragesample.fragments.StorageListFragment;
import com.hp.workpath.sample.massstoragesample.model.FileInfo;
import com.hp.workpath.sample.massstoragesample.model.StorageInfo;
import com.hp.workpath.sample.massstoragesample.task.DeleteFileTask;
import com.hp.workpath.sample.massstoragesample.task.GetStorageListTask;
import com.hp.workpath.sample.massstoragesample.task.InitializationTask;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ISelectedStorageListener {

    public static final String TAG = "[SAMPLE]" + "MassStorage";

    /**
     * Map {@link StorageInfo}
     * Store references to summary TextViews to provide information
     */
    private final EnumMap<StorageInfo, TextView> mSummaries = new EnumMap<>(StorageInfo.class);

    /* Background task for Workpath SDK API initialization */
    private InitializationTask mInitializationTask;

    private Button mNewFileButton;
    private Button mCreateButton;
    private Button mDeleteButton;
    private Button mRenameButton;
    private Snackbar mSnackBar;

    private ImageButton mRefreshButton;
    private RecyclerView mFileListView;
    private FileListAdapter mFileListAdapter;
    private TextView mPathTextView;
    private TextView mNoFileTextView;

    private AlertDialog mAlertDialog;
    private ProgressBar mProgress;
    private View mContainer;

    List<MassStorageInfo> massStorageInfoList;
    int currentSelected;

    private MassStorageChangeObserver mMassStorageChangeObserver;
    private CustomerDataFile baseCustomerDataFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find the text and button
        findViewElements();

        // add click listener to call the MFP
        addListener();

        mMassStorageChangeObserver = new MassStorageChangeObserver(new Handler());
    }

    @Override
    protected void onResume() {
        super.onResume();

        mContainer.setEnabled(false);

        // register MassStorageChangeObserver to receive mass storage change callback
        mMassStorageChangeObserver.register(getApplicationContext());

        mInitializationTask = new InitializationTask(this);
        mInitializationTask.taskExecute();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // unregister MassStorageChangeObserver
        mMassStorageChangeObserver.unregister(getApplicationContext());

        mInitializationTask.cancel();
        mInitializationTask = null;

        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
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

    private void findViewElements() {
        mProgress = findViewById(R.id.progressBar);
        mContainer = findViewById(R.id.container);
        mNewFileButton = findViewById(R.id.newFileButton);
        mCreateButton = findViewById(R.id.createButton);
        mDeleteButton = findViewById(R.id.deleteButton);
        mRenameButton = findViewById(R.id.renameButton);
        mRefreshButton = findViewById(R.id.refreshButton);

        ((TextView) findViewById(R.id.headerStorageInformation)
                .findViewById(R.id.headerTextView)).setText(getString(R.string.storage_information));

        for (StorageInfo item : StorageInfo.values()) {
            ViewGroup itemLayout = findViewById(item.getItemId());
            ((TextView) itemLayout.findViewById(R.id.titleTextView)).setText(item.getTitleId());
            mSummaries.put(item, itemLayout.findViewById(R.id.summaryTextView));
        }

        mPathTextView = findViewById(R.id.pathTextView);
        mFileListView = findViewById(R.id.fileListView);
        mNoFileTextView = findViewById(R.id.noFileTextView);
        mFileListAdapter = new FileListAdapter(fileListOnClickListener);
        mFileListView.setLayoutManager(new LinearLayoutManager(this));
        mFileListView.setAdapter(mFileListAdapter);
    }

    FileListAdapter.FileListOnClickListener fileListOnClickListener = new FileListAdapter.FileListOnClickListener() {
        @Override
        public void onDeleteButton(boolean enable) {
            mDeleteButton.setEnabled(enable);
        }

        @Override
        public void onRenameButton(boolean enable) {
            mRenameButton.setEnabled(enable);
        }

        @Override
        public void onItemClick(View view) {
            RecyclerView.ViewHolder holder = mFileListView.getChildViewHolder(view);
            int position = holder.getAdapterPosition();

            CustomerDataFile selectedItem = mFileListAdapter.getItem(position).getFile();
            if (selectedItem.isRoot() || selectedItem.isDirectory()) {
                // move to selected folder
                displayFileList(selectedItem);
            } else {
                FileInfoFragment fileInfoFragment
                        = FileInfoFragment.newInstance(selectedItem);
                fileInfoFragment.show(getSupportFragmentManager(), "dialog");
            }
        }
    };

    private void addListener() {
        mNewFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (massStorageInfoList != null && massStorageInfoList.size() > 0) {
                    NewFileFragment newFileFragment
                            = NewFileFragment.newInstance(massStorageInfoList.get(currentSelected), baseCustomerDataFile);
                    newFileFragment.show(getSupportFragmentManager(), "dialog");

                } else {
                    if (getApplicationContext() != null) {
                        Logger.showResult(MainActivity.this, getString(R.string.not_loaded_storage_list));
                    }
                }
            }
        });

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (massStorageInfoList != null && massStorageInfoList.size() > 0) {
                    FileCreateFragment fileCreateFragment
                            = FileCreateFragment.newInstance(massStorageInfoList.get(currentSelected), baseCustomerDataFile);
                    fileCreateFragment.show(getSupportFragmentManager(), "dialog");

                } else {
                    if (getApplicationContext() != null) {
                        Logger.showResult(MainActivity.this, getString(R.string.not_loaded_storage_list));
                    }
                }
            }
        });

        mRenameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (massStorageInfoList != null && massStorageInfoList.size() > 0) {
                    boolean[] checkedList = mFileListAdapter.getCheckedItemArray();
                    for (int x = 0; x < checkedList.length; x++) {
                        if (checkedList[x]) {
                            FileRenameFragment fileRenameFragment
                                    = FileRenameFragment.newInstance(massStorageInfoList.get(currentSelected), mFileListAdapter.getItem(x).getFile());
                            fileRenameFragment.show(getSupportFragmentManager(), "dialog");
                            return;
                        }
                    }
                } else {
                    if (getApplicationContext() != null) {
                        Logger.showResult(MainActivity.this, getString(R.string.not_loaded_storage_list));
                    }
                }
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (massStorageInfoList != null && massStorageInfoList.size() > 0) {
                    List<CustomerDataFile> selectedItemList = new ArrayList<>();
                    boolean[] checkedItemArray = mFileListAdapter.getCheckedItemArray();
                    for (int x = 0; x < checkedItemArray.length; x++) {
                        if (checkedItemArray[x]) {
                            selectedItemList.add(mFileListAdapter.getItem(x).getFile());
                        }
                    }
                    showProgress(View.VISIBLE);
                    new DeleteFileTask(MainActivity.this, selectedItemList, baseCustomerDataFile).taskExecute();
                } else {
                    if (getApplicationContext() != null) {
                        Logger.showResult(MainActivity.this, getString(R.string.not_loaded_storage_list));
                    }
                }
            }
        });

        findViewById(R.id.selectButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStorageList();
            }
        });

        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayFileList(baseCustomerDataFile);
            }
        });
    }

    public void handleComplete() {
        if (getApplicationContext() != null) {
            Logger.showResult(MainActivity.this, getString(R.string.initialized));
        }
    }

    private void getStorageList() {
        showProgress(View.VISIBLE);
        mContainer.setEnabled(true);
        new GetStorageListTask(MainActivity.this).taskExecute();
    }

    public void showProgress(int visibility) {
        if (mProgress != null) {
            mProgress.setVisibility(visibility);
        }
    }

    public void loadStorage(MassStorageInfo massStorageInfo) {
        displayStorageInfo(massStorageInfo);
        displayFileList(new CustomerDataFile(this, massStorageInfo, "/"));
    }

    public void loadStorageList(List<MassStorageInfo> massStorageInfoList) {
        this.massStorageInfoList = massStorageInfoList;
        if (massStorageInfoList != null && massStorageInfoList.size() > 0) {
            StorageListFragment storageDialog
                    = StorageListFragment.newInstance(massStorageInfoList);
            storageDialog.show(getSupportFragmentManager(), "dialog");
        }
    }

    public void displayFileList(CustomerDataFile customerDataFile) {
        if (mFileListAdapter != null) {
            mFileListAdapter.clear();
        }

        if (customerDataFile != null) {
            baseCustomerDataFile = customerDataFile;
            String path = customerDataFile.getPath();

            Log.i(TAG, "CustomerDataFile: " + Logger.build(customerDataFile));
            CustomerDataFile[] listFiles = customerDataFile.listFiles();
            CustomerDataFile parentCustomerDataFile = customerDataFile.getParentFile();

            List<FileInfo> fileInfoList = new ArrayList<>();
            if (parentCustomerDataFile != null) {
                FileInfo parentFileInfo = createRootFolder(parentCustomerDataFile);
                fileInfoList.add(parentFileInfo);
            }

            for (CustomerDataFile cdf : listFiles) {
                fileInfoList.add(new FileInfo(cdf));
            }
            if (fileInfoList.size() == 0) {
                mNoFileTextView.setVisibility(View.VISIBLE);
            } else {
                mNoFileTextView.setVisibility(View.GONE);
                mFileListAdapter.setListItems(fileInfoList);
            }

            mPathTextView.setText(path);
            mCreateButton.setEnabled(true);
            mNewFileButton.setEnabled(true);
            mRenameButton.setEnabled(false);
            mDeleteButton.setEnabled(false);
        } else {
            Logger.showResult(this, "CustomerData is null");
        }
    }

    private FileInfo createRootFolder(CustomerDataFile file) {
        FileInfo fileInfo = new FileInfo(file);
        fileInfo.setFileName(FileListAdapter.BACK);
        return fileInfo;
    }

    @Override
    public void selectedStorage(int index) {
        currentSelected = index;
        loadStorage(massStorageInfoList.get(index));
    }

    private void displayStorageInfo(MassStorageInfo massStorageInfo) {
        for (StorageInfo item : mSummaries.keySet()) {
            switch (item.getItemId()) {
                case R.id.childStorageList:
                case R.id.childName:
                    String name = (massStorageInfo != null) ? massStorageInfo.getName() : "";
                    mSummaries.get(item).setText(name);
                    break;
                case R.id.childExternalFileDirectory:
                    String externalFileDirectory = (massStorageInfo != null) ? massStorageInfo.getExternalFileDirectory() : "";
                    mSummaries.get(item).setText(externalFileDirectory);
                    break;
                case R.id.childFreeSpace:
                    String freeSpace = (massStorageInfo != null) ? String.valueOf(massStorageInfo.getFreeSpace()) : "";
                    mSummaries.get(item).setText(freeSpace);
                    break;
                case R.id.childProtocol:
                    String protocol = (massStorageInfo != null) ? massStorageInfo.getProtocol().toString() : "";
                    mSummaries.get(item).setText(protocol);
                    break;
                case R.id.childTotalSpace:
                    String totalSpace = (massStorageInfo != null) ? String.valueOf(massStorageInfo.getTotalSpace()) : "";
                    mSummaries.get(item).setText(totalSpace);
                    break;
                case R.id.childType:
                    String type = (massStorageInfo != null) ? massStorageInfo.getType().toString() : "";
                    mSummaries.get(item).setText(type);
                    break;
                case R.id.childMounted:
                    String mounted = (massStorageInfo != null) ? String.valueOf(massStorageInfo.isMounted()) : "";
                    mSummaries.get(item).setText(mounted);
                    break;
                case R.id.childVolumeName:
                    String volumeName = (massStorageInfo != null) ? massStorageInfo.getVolumeName() : "";
                    mSummaries.get(item).setText(volumeName);
                    break;
            }
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
                .setTitle("Error")
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


    /**
     * Receives notification when MassStorage is attached or detached.
     */
    private class MassStorageChangeObserver extends MassStorageService.AbstractMassStorageChangeObserver {
        public MassStorageChangeObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange() {
            Log.d(TAG, "Received onChange()");
            showSnackBar(getString(R.string.onchange_event_message));
            cleanAllViews();
        }
    }

    private void cleanAllViews() {
        displayStorageInfo(null);
        massStorageInfoList = null;
        baseCustomerDataFile = null;
        if (mFileListAdapter != null) {
            mFileListAdapter.clear();
            mNoFileTextView.setVisibility(View.VISIBLE);
        }
    }

    public void enableButton(boolean enable) {
        mCreateButton.setEnabled(enable);
        mNewFileButton.setEnabled(enable);
    }

    public void showSnackBar(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSnackBar == null) {
                    mSnackBar = Snackbar.make(mContainer, "", Snackbar.LENGTH_INDEFINITE);
                    View snackBarView = mSnackBar.getView();
                    TextView tv = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
                    tv.setMaxLines(3);
                }
                mSnackBar.setText(text);
                mSnackBar.setActionTextColor(getResources().getColor(R.color.snackbar_button_color));
                mSnackBar.setAction(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mSnackBar != null) {
                            mSnackBar.dismiss();
                            mSnackBar = null;
                        }
                    }
                }).show();
            }
        });
    }
}
