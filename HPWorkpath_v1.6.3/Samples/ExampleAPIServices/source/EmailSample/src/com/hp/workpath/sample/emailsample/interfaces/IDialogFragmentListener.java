// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample.interfaces;

import java.util.HashMap;

public interface IDialogFragmentListener {
    void onReturnValue(HashMap<String, Object> result);
}