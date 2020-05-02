package cn.yiidii.pigeon.shiro.service.impl;

import cn.yiidii.pigeon.shiro.entity.Role;
import cn.yiidii.pigeon.shiro.mapper.RoleMapper;
import cn.yiidii.pigeon.shiro.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RoleService implements IRoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Set<Role> queryAllRole() {
        return roleMapper.queryAllRole();
    }

    @Override
    public Set<Role> queryRoleByUserName(String username) {
        return roleMapper.queryRoleByUserName(username);
    }

}
