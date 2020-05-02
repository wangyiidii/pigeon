package cn.yiidii.pigeon.shiro.controller.form;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Data
@Valid
public class UserLoginForm {
    @NotBlank(message = "用户不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
}
