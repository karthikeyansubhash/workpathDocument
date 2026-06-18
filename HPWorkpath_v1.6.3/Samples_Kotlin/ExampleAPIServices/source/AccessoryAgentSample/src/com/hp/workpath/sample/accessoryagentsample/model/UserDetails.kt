// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accessoryagentsample.model

import com.hp.workpath.sample.accessoryagentsample.R

enum class UserDetails(val itemId: Int, val titleId: Int) {
    USER_NAME(R.id.childUserName, R.string.user_name_line),
    FULLY_QUALIFIED_NAME(R.id.childFullyQualifiedName, R.string.fully_qualified_name),
    PRINCIPAL_ID(R.id.childPrincipalId, R.string.principal_id_line),
    DOMAIN(R.id.childDomain, R.string.domain_line),
    PROVIDER(R.id.childProvider, R.string.provider),
    IS_AUTHENTICATED(R.id.childIsAuthenticated, R.string.is_authenticated_line);

}