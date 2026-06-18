// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.googlesigninsample.task;

import com.hp.workpath.sample.googlesigninsample.model.AccountInfo;

public interface TaskInterface {
    void onFailure(Throwable t);

    void refreshedToken(String token);

    void revokedToken();

    void receivedToken(AccountInfo accountInfo);
}
