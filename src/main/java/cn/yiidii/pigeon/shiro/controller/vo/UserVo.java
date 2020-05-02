package cn.yiidii.pigeon.shiro.controller.vo;

import lombok.Data;

@Data
public class UserVo {
    private Integer id;
    private String username;
    //private String password;
    private String name;
    private String email;
    private String phone;
    //private String salt;
    //private String emailCode;
    private boolean state;

}
