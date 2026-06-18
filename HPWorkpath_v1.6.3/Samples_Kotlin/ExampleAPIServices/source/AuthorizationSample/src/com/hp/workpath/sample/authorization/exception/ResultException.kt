package com.hp.workpath.sample.authorization.exception

import com.hp.workpath.api.Result
class ResultException(val result: Result) : Exception(result.cause)