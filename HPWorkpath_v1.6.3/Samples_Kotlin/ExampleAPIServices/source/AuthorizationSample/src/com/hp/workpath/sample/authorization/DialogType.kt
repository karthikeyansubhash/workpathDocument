// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization

object DialogType {
    const val DIALOG_TYPE = "dialog_type"
    const val DIALOG_DATA = "dialog_data"

    enum class Email {
        ADD_TO, ADD_CC, ADD_BCC
    }

    enum class Data {
        GET_PERMISSIONS,
        GET_SIGN_IN_METHODS,
        GUEST_PERMISSION_SET,
        DEFAULT_SIGN_IN_METHOD,
        PERMISSION_TO_SIGN_IN_METHOD__PERMISSION_ID,
        PERMISSION_TO_SIGN_IN_METHOD__SIGN_IN_METHOD
    }
}