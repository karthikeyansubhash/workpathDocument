// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample.model

class EmailAddress() {
    var address: String = ""
    var name: String? = null

    constructor(address: String, name: String?) : this() {
        this.address = address
        this.name = name
    }
}