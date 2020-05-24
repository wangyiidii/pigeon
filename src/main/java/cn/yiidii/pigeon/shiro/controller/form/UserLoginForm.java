package cn.yiidii.pigeon.shiro.controller.form;

import io.swagger.annotations.ApiParam;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Data
@Validated
public class UserLoginForm {
    @NotBlank(message = "用户不能为空")
    @ApiParam(required = true)
    private String username;
    @NotBlank(message = "密码不能为空")
    @ApiParam(required = true)
    private String password;
}
