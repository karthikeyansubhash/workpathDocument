// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample;

public class EmailDialog {
    public static final String DIALOG_TYPE = "dialog_type";

    public enum Type {
        ADD_TO, ADD_CC, ADD_BCC, ATTACH, SMTP, PROXY
    }
}