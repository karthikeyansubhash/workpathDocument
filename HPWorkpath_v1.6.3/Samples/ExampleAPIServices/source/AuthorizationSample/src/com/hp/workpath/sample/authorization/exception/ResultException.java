package com.hp.workpath.sample.authorization.exception;

import com.hp.workpath.api.Result;

public class ResultException extends Exception {
    private final Result result;

    public ResultException(Result result) {
        super(result.getCause());
        this.result = result;
    }

    public Result getResult() {
        return result;
    }
}
