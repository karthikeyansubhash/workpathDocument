// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample.model;

import com.hp.workpath.sample.massstoragesample.R;

public enum StorageInfo {
    // Mass Storage List
    STORAGE_LIST(R.id.childStorageList, R.string.storage_list),
    // Mass Storage information
    EXTERNAL_FILE_DIRECTORY(R.id.childExternalFileDirectory, R.string.external_file_directory),
    NAME(R.id.childName, R.string.name),
    VOLUME_NAME(R.id.childVolumeName, R.string.volume_name),
    MOUNTED(R.id.childMounted, R.string.mounted),
    PROTOCOL(R.id.childProtocol, R.string.protocol),
    TYPE(R.id.childType, R.string.type),
    FREE_SPACE(R.id.childFreeSpace, R.string.free_space),
    TOTAL_SPACE(R.id.childTotalSpace, R.string.total_space);

    private int mItemId;
    private int mTitleId;

    public int getItemId() {
        return mItemId;
    }

    public int getTitleId() {
        return mTitleId;
    }

    /**
     * @param itemId  in layout
     * @param titleId in child layout
     */
    StorageInfo(final int itemId, final int titleId) {
        mItemId = itemId;
        mTitleId = titleId;
    }
}

