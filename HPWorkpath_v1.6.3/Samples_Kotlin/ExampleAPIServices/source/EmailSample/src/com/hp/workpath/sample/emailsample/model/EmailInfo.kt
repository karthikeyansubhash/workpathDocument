// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.emailsample.model

import java.io.File

class EmailInfo {
    var attachments: Array<File>? = null
    var bcc: List<EmailAddress>? = null
    var cc: List<EmailAddress>? = null
    var from: EmailAddress? = null
    var message: String? = null
    var subject: String? = null
    var to: List<EmailAddress>? = null
}