// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization.interfaces

import com.hp.workpath.api.Result

interface IDialogFragmentListener {
    fun onDialogResult(result: HashMap<String, Any>)
    fun onDialogError(result: Result)
}