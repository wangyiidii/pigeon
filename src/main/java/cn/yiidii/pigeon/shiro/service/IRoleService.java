package cn.yiidii.pigeon.shiro.service;

import cn.yiidii.pigeon.shiro.entity.Role;

import java.util.Set;

public interface IRoleService {

    Set<Role> queryAllRole();

    Set<Role> queryRoleByUserName(String username);

}
