// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.massstoragesample.model

import com.hp.workpath.api.massstorage.CustomerDataFile

class FileInfo(var file: CustomerDataFile) {
    var fileName: String
    var filePath: String = file.path

    init {
        fileName = filePath.substring(filePath.lastIndexOf("/") + 1)
    }
}