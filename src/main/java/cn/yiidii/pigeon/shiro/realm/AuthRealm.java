package cn.yiidii.pigeon.shiro.realm;

import cn.yiidii.pigeon.base.exception.TokenAuthFailedException;
import cn.yiidii.pigeon.shiro.entity.Permission;
import cn.yiidii.pigeon.shiro.entity.Role;
import cn.yiidii.pigeon.shiro.entity.User;
import cn.yiidii.pigeon.shiro.jwt.JWTToken;
import cn.yiidii.pigeon.shiro.service.impl.PermissionService;
import cn.yiidii.pigeon.shiro.service.impl.RoleService;
import cn.yiidii.pigeon.shiro.service.impl.UserService;
import cn.yiidii.pigeon.shiro.util.JWTUtil;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return null != token && token instanceof JWTToken;
    }

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String token = (String) authenticationToken.getCredentials();
        // 解密获得username，用于和数据库进行对比
        String username = JWTUtil.getUsername(token);
        User user = userService.queryUserByUsername(username);
        if (user == null) {
            throw new TokenExpiredException("Token无效");
        }
        if (!user.isState()) {
            throw new DisabledAccountException();
        }
        if (!JWTUtil.verify(token, username, user.getPassword())) {
            throw new TokenAuthFailedException();
        }
        return new SimpleAuthenticationInfo(token, token, getName());
    }

    /**
     * 鉴权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = JWTUtil.getUsername(principals.toString());
        User user = userService.queryUserByUsername(username);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        // 设置角色
        for (Role role : roleService.queryRoleByUserName(user.getUsername())) {
            authorizationInfo.addRole(role.getName());
        }
        // 设置权限
        for (Permission permission : permissionService.queryPermissionByUsername(user.getUsername())) {
            authorizationInfo.addStringPermission(permission.getPermission());
        }
        return info;
    }
}
