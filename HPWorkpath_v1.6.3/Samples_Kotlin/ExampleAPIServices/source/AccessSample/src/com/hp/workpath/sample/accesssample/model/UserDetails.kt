// Copyright 2018 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.accesssample.model

import com.hp.workpath.sample.accesssample.R

enum class UserDetails(val itemId: Int, val titleId: Int) {
    VERSION(R.id.childSdkVersion, R.string.version_line),
    FULLY_QUALIFIED_NAME(R.id.childFullyQualifiedName, R.string.fully_qualified_name),
    PRINCIPAL_ID(R.id.childPrincipalId, R.string.principal_id_line),
    DOMAIN(R.id.childDomain, R.string.domain_line),
    PROVIDER(R.id.childProvider, R.string.provider),
    USER_NAME(R.id.childUsername, R.string.user_name_line),
    PASSWORD(R.id.childPassword, R.string.password),
    USER_EMAIL(R.id.childUserEmail, R.string.user_email_line),
    IS_ADMIN(R.id.childIsAdmin, R.string.is_admin_line),
    IS_AUTHENTICATED(R.id.childIsAuthenticated, R.string.is_authenticated_line),
    SIMPLE_AUTHORITIES(R.id.childSimpleAuthorities, R.string.simple_authorities_line),
    IS_HP_CLOUD_USER(R.id.childIsHpCloudUser, R.string.is_hp_cloud_user),
    IS_GUEST_USER(R.id.childIsGuestUser, R.string.is_guest_user),
    IS_DEVICE_USER(R.id.childIsDeviceUser, R.string.is_device_user),
    IS_SERVICE_USER(R.id.childIsServiceUser, R.string.is_service_user),
    IS_SMART_CARD_USER(R.id.childIsSmartCardUser, R.string.is_smart_card_user),
    PROVIDER_UUID(R.id.childProviderUuid, R.string.provider_uuid);
}