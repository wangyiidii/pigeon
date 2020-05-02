package cn.yiidii.pigeon.shiro.controller.form;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Valid
public class UserRegForm {
    @Pattern(regexp = "^[A-Za-z0-9-_]+$", message = "用户名仅支持英文字母和下划线")
    @NotBlank(message = "用户不能为空")
    private String username;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "密码至少8位，必须包含大小写和数字，不能包含特殊字符")
    private String password;

    @NotBlank(message = "用户姓名不能为空")
    private String name;

    @Email
    @NotBlank(message = "邮件地址不能为空")
    private String email;

    @Pattern(regexp = "^1(3|4|5|6|7|8)\\d{9}$", message = "手机号格式不正确")
    private String phone;
}
