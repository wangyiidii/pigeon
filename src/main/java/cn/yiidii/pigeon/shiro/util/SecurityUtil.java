package cn.yiidii.pigeon.shiro.util;

import cn.yiidii.pigeon.shiro.entity.Role;
import cn.yiidii.pigeon.shiro.entity.User;
import cn.yiidii.pigeon.shiro.service.impl.RoleService;
import cn.yiidii.pigeon.shiro.service.impl.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SecurityUtil {
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;

    public boolean isContainsRole(String username) {
        Set<Role> roleSet = roleService.queryRoleByUserName(username);
        for (Role role : roleSet) {
            if ("admin".equals(role.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isAdminRole(String roleName) {
        if (null != roleName && "admin".equals(roleName)) {
            return true;
        }
        return false;
    }


    public User getCurrUser() {
        String token = (String) SecurityUtils.getSubject().getPrincipal();
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        // 解密获得username，用于和数据库进行对比
        String username = JWTUtil.getUsername(token);
        return userService.queryUserByUsername(username);
    }
}
