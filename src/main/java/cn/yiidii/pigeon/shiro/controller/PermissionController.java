package cn.yiidii.pigeon.shiro.controller;

import cn.yiidii.pigeon.shiro.entity.Permission;
import cn.yiidii.pigeon.shiro.service.impl.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/permission")
@Api(tags = "权限")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/all")
    @ApiOperation(value = "获取所有权限列表")
    public Object getAllPermission() {
        Set<Permission> permissions = permissionService.queryAllPermission();
        return permissions.stream().sorted(Comparator.comparing(Permission::getId)).collect(Collectors.toList());


    }

}
