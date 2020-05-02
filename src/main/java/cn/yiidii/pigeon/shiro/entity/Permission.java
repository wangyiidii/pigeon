package cn.yiidii.pigeon.shiro.entity;

import lombok.Data;

@Data
public class Permission {
    private Integer id;
    private String url;
    private String method;
    private String permission;
    private String name;
}
