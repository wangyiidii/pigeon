package cn.yiidii.pigeon.cmdb.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("indicator")
public class Indicator {
    private Integer id;
    @TableField("`desc`")
    private String desc;
    private String name;//deName + sequence
    private String defName;
    private String alias;
    private Integer state;
    @TableField("`interval`")
    private Long interval = 60L;//unit: s
    private String method;
    private Long timeout = 30L;//unit: s
    private Integer retryTimes = 3;
    private Date createTime = new Date();
    private Date updateTime;
    private Date lastCollectTime;

}
