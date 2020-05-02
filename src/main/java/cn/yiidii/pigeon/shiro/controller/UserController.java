package cn.yiidii.pigeon.shiro.controller;

import cn.yiidii.pigeon.base.vo.Result;
import cn.yiidii.pigeon.shiro.controller.vo.UserVo;
import cn.yiidii.pigeon.shiro.entity.User;
import cn.yiidii.pigeon.shiro.service.impl.PermissionService;
import cn.yiidii.pigeon.shiro.service.impl.UserService;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
@Api(tags = "用户")
public class UserController {

    @Autowired
    public UserService userService;

    @Autowired
    public PermissionService permissionService;

    @GetMapping("/all")
    @ApiOperation(value = "获取所有用户")
    public Object getUser() {
        List<User> users = userService.queryAllUser();
        List<UserVo> userVoList = new ArrayList<>();
        users.forEach(u -> {
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(u, userVo);
            userVoList.add(userVo);
        });
        return JSONObject.toJSON(userVoList);
    }

    @GetMapping("/info")
    @ApiOperation(value = "获取当前用户的基本信息")
    public Object getCurrentUser() {
        return null;
    }

    @GetMapping("/{username}")
    @ApiOperation(value = "根据用户名获取用户信息")
    public Object getUser(@NotBlank(message = "参数用户名不能为空") @PathVariable("username") String username) {
        User user = userService.queryUserByUsername(username);
        if (null != user) {
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            return userVo;
        }
        return Result.error();
    }

    @GetMapping("/permission/{username}")
    @ApiOperation(value = "获取当前用户的权限")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", dataType = "String", name = "username", value = "用户名", required = true)})
    public Object getPermsByUsername(@NotBlank(message = "参数用户名不能为空") @PathVariable("username") String username) {
        return permissionService.queryPermissionByUsername(username);
    }


}
