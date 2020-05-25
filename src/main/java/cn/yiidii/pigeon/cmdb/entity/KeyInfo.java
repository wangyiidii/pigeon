package cn.yiidii.pigeon.cmdb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@TableName("sequence")
@AllArgsConstructor
public class KeyInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("`name`")
    private String name;
    @TableField("`key`")
    private Integer key;

    public KeyInfo(String name, Integer key) {
        this.name = name;
        this.key = key;
    }
}
