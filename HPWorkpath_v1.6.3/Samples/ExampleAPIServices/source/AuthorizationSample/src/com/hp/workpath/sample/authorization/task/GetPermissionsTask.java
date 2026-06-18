// Copyright 2025 HP Development Company, L.P.
// SPDX-License-Identifier: MIT
package com.hp.workpath.sample.authorization.task;

import android.content.Context;

import com.hp.workpath.api.Result;
import com.hp.workpath.api.authorization.AuthorizationService;
import com.hp.workpath.api.authorization.Permission;
import com.hp.workpath.sample.authorization.exception.ResultException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class GetPermissionsTask implements Callable<ArrayList<Permission>> {

    WeakReference<Context> context;

    public GetPermissionsTask(Context context) {
        this.context = new WeakReference<>(context);
    }

    @Override
    public ArrayList<Permission> call() throws Exception {
        Result result = new Result();
        ArrayList<Permission> permissions = AuthorizationService.getPermissions(context.get(), result);
        if (result.getCode() != Result.RESULT_OK) {
            throw new ResultException(result);
        }
        return permissions;
    }

    public static ArrayList<Permission> getPermissionsFromPermissionSet(Context context, List<String> permissionSet) {
        Result result = new Result();
        ArrayList<Permission> permissions = AuthorizationService.getPermissions(context, result);
        if (permissionSet != null) {
            return permissions.stream()
                    .filter(permission -> permissionSet.contains(permission.getId()))
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        return null;
    }

    public static List<String> getPermissionSetFromPermissions(ArrayList<Permission> permissions) {
        List<String> permissionSet = new ArrayList<>();
        for (Permission permission : permissions) {
            permissionSet.add(permission.getId());
        }
        return permissionSet;
    }
}

