// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample.model;

import com.hp.workpath.api.massstorage.CustomerDataFile;

public class FileInfo {
    CustomerDataFile file;
    String fileName;
    String filePath;

    public FileInfo(CustomerDataFile file) {
        this.file = file;
        this.filePath = file.getPath();
        this.fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
    }

    public CustomerDataFile getFile() {
        return file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
