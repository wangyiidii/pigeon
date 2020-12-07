package cn.yiidii.pigeon.shiro.controller;

import cn.yiidii.pigeon.base.exception.ServiceException;
import cn.yiidii.pigeon.base.vo.Result;
import cn.yiidii.pigeon.base.vo.ResultCodeEnum;
import cn.yiidii.pigeon.shiro.controller.form.UserLoginForm;
import cn.yiidii.pigeon.shiro.controller.form.UserRegForm;
import cn.yiidii.pigeon.shiro.controller.vo.UserVo;
import cn.yiidii.pigeon.shiro.entity.User;
import cn.yiidii.pigeon.shiro.service.impl.UserService;
import cn.yiidii.pigeon.shiro.util.JWTUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.DynamicParameter;
import com.github.xiaoymin.knife4j.annotations.DynamicParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@RestController
@Api(tags = "认证")
@RequestMapping("/")
@Validated
public class AuthController {

    @Autowired
    public UserService userService;

    @PostMapping(value = "/login")
    @ApiOperation(value = "用户登录", notes = "")
    @ApiOperationSupport(params = @DynamicParameters(name = "userLoginForm", properties = {
            @DynamicParameter(name = "username", value = "用户名", example = "admin", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "password", value = "密码", example = "admin", required = true, dataTypeClass = String.class)
    }))
    public Object login(@RequestBody @Valid UserLoginForm userLoginForm) {
        User user = userService.login(userLoginForm);
        if (!Objects.isNull(user)) {
            String token = JWTUtil.sign(user.getUsername(), user.getPassword());
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            JSONObject jo = new JSONObject();
            jo.put("info", userVo);
            jo.put("token", token);
            return jo;
        } else {
            throw new AuthenticationException("用户名或密码错误");
        }
    }

    @PostMapping("/reg")
    @ApiOperation(value = "注册", notes = "")
    public Object reg(@RequestBody @Valid UserRegForm userRegForm) {
        if (1 == userService.reg(userRegForm)) {
            return Result.success("系统已向注册邮箱【" + userRegForm.getEmail() + "】发送了一封激活邮件，请前往激活");
        }
        throw new ServiceException("注册发生异常，请联系系统管理员");
    }

    @GetMapping("/activeAccount")
    @ApiOperation(value = "激活用户", notes = "注册的用户默认是没有激活的需要到注册邮箱激活，调用此接口")
    public String activeAccount(@RequestParam("username") @NotBlank(message = "用户名不能为空") String username, @RequestParam("code") @NotBlank(message = "激活码不能为空") @Validated String code) {
        if (userService.activeAccount(username, code)) {
            return "用户" + username + "激活成功";
        }
        throw new ServiceException("用户" + username + "激活失败，请联系系统管理员");
    }

    @GetMapping("/401")
    @ApiOperation(value = "401，没有权限", notes = "没有权限默认都会跳转此接口，返回无权限")
    public Result<Object> unauthorized(HttpSession session) {
        return Result.error(ResultCodeEnum.NO_PERMISSION.getCode(), ResultCodeEnum.NO_PERMISSION.getMsg());
    }

}
