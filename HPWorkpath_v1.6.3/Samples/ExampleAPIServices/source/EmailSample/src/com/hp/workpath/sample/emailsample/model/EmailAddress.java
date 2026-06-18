// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample.model;

public class EmailAddress {
    private String address;
    private String name;

    public EmailAddress(String mailAddress, String mailName) {
        this.address = mailAddress;
        this.name = mailName;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }
}