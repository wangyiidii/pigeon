package cn.yiidii.pigeon.shiro.mapper;

import cn.yiidii.pigeon.shiro.entity.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.Set;

@Mapper
public interface RoleMapper {
    Set<Role> queryAllRole();

    Set<Role> queryRoleByUserName(String username);

    Role queryRoleByRoleName(String roleName);

//    List<Role> queryRoleByRoleName(Integer id);

    //Integer insert(Role role);

    Integer update(Role role);

    Integer del(Integer id);
}
