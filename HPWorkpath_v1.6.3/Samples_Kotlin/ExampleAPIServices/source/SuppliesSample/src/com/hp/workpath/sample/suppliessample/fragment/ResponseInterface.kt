// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.suppliessample.fragment

import com.hp.workpath.api.Result
import com.hp.workpath.api.supplies.supplyinfo.Supply

interface ResponseInterface {
    fun success(supplies: List<Supply>?)
    fun failed(msg: String?, result: Result?)
}