package cn.yiidii.pigeon.cmdb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("param")
public class Param {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    @TableField("`key`")
    private String key;
    @TableField("`value`")
    private String value;

    public Param(String name, String key, String value) {
        this.name = name;
        this.key = key;
        this.value = value;
    }
}
