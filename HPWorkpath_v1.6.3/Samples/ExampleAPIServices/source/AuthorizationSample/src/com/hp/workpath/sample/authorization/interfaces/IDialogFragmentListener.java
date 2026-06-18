// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization.interfaces;

import com.hp.workpath.api.Result;

import java.util.HashMap;

public interface IDialogFragmentListener {
    void onDialogResult(HashMap<String, Object> result);
    void onDialogError(Result result);
}