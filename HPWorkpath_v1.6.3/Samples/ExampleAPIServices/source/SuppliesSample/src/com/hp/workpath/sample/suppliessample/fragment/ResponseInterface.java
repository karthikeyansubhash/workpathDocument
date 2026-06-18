// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.suppliessample.fragment;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.supplies.supplyinfo.Supply;

import java.util.List;

public interface ResponseInterface {
    void success(List<Supply> supplies);
    void failure(String msg, Result result);
}
