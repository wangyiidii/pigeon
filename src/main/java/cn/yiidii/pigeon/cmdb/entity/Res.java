package cn.yiidii.pigeon.cmdb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class Res {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;//deName + sequence
    private String alias;
    private String host;
    private String defName;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
