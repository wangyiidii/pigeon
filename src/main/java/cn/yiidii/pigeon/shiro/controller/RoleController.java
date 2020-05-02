package cn.yiidii.pigeon.shiro.controller;

import cn.yiidii.pigeon.shiro.service.impl.PermissionService;
import cn.yiidii.pigeon.shiro.service.impl.RoleService;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/role")
@Api(tags = "角色")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/all")
    @ApiOperation(value = "获取所有角色列表")
    public Object getAllRole() {
        return roleService.queryAllRole();
    }

    @GetMapping("/permission/{roleName}")
    @ApiOperation(value = "获取角色的权限")
    public Object getPermsByRoleName(@NotBlank(message = "角色名不能为空") @PathVariable("roleName") String roleName) {
        return permissionService.queryPermissionByRoleName(roleName);
    }

}
