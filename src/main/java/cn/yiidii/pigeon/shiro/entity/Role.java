package cn.yiidii.pigeon.shiro.entity;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class Role {
    private Integer id;
    private String name;
    private String desc;
}
