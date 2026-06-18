// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample

object EmailDialog {
    const val DIALOG_TYPE = "dialog_type"

    enum class Type {
        ADD_TO, ADD_CC, ADD_BCC, ATTACH, SMTP, PROXY
    }
}