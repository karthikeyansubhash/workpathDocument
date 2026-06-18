// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample.model;

import java.io.File;
import java.util.List;

public class EmailInfo {
    private File[] attachments;
    private List<EmailAddress> bcc;
    private List<EmailAddress> cc;
    private EmailAddress from;
    private String message;
    private String subject;
    private List<EmailAddress> to;

    public File[] getAttachments() {
        return attachments;
    }

    public void setAttachments(File[] attachments) {
        this.attachments = attachments;
    }

    public List<EmailAddress> getBcc() {
        return bcc;
    }

    public void setBcc(List<EmailAddress> bcc) {
        this.bcc = bcc;
    }

    public List<EmailAddress> getCc() {
        return cc;
    }

    public void setCc(List<EmailAddress> cc) {
        this.cc = cc;
    }

    public EmailAddress getFrom() {
        return from;
    }

    public void setFrom(EmailAddress from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<EmailAddress> getTo() {
        return to;
    }

    public void setTo(List<EmailAddress> to) {
        this.to = to;
    }
}