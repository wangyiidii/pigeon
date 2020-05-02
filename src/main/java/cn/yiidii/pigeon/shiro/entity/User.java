package cn.yiidii.pigeon.shiro.entity;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class User {
    private Integer id;
    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String salt;
    private String emailCode;
    private boolean state;
}
