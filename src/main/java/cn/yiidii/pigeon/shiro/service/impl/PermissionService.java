package cn.yiidii.pigeon.shiro.service.impl;

import cn.yiidii.pigeon.base.exception.ServiceException;
import cn.yiidii.pigeon.shiro.entity.Permission;
import cn.yiidii.pigeon.shiro.entity.Role;
import cn.yiidii.pigeon.shiro.mapper.PermissionMapper;
import cn.yiidii.pigeon.shiro.mapper.RoleMapper;
import cn.yiidii.pigeon.shiro.service.IPermissionService;
import cn.yiidii.pigeon.shiro.util.SecurityUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class PermissionService implements IPermissionService {

    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private SecurityUtil securityUtil;

    @Override
    public Set<Permission> queryPermissionByUsername(String username) {
        for (Role role : roleMapper.queryRoleByUserName(username)) {
            if (StringUtils.equals(role.getName(), username)) {
                return permissionMapper.queryAllPermission();
            }
        }
        return permissionMapper.queryPermissionByUsername(username);
    }

    @Override
    public Set<Permission> queryPermissionByRoleName(String roleName) throws ServiceException {
        Role role = roleMapper.queryRoleByRoleName(roleName);
        if (null == role) {
            throw new ServiceException("角色【" + roleName + "】不存在");
        }
        Set<Permission> permissions = new HashSet<>();
        if (new SecurityUtil().isAdminRole(roleName)) {
            permissions = permissionMapper.queryAllPermission();
        } else {
            permissions = permissionMapper.queryPermissionByRoleName(roleName);
        }
        return permissions;
    }

    @Override
    public Set<Permission> queryAllPermission() {
        Set<Permission> permissions = permissionMapper.queryAllPermission();
        return permissions;
    }

}
