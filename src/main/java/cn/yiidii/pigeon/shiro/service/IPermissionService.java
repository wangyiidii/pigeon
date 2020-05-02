package cn.yiidii.pigeon.shiro.service;


import cn.yiidii.pigeon.shiro.entity.Permission;

import java.util.Set;

public interface IPermissionService {
    Set<Permission> queryPermissionByUsername(String username);

    Set<Permission> queryPermissionByRoleName(String roleName);

    Set<Permission> queryAllPermission();
}
