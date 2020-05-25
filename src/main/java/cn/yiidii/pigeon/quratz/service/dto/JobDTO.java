package cn.yiidii.pigeon.quratz.service.dto;

import cn.yiidii.pigeon.quratz.entity.JobParam;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class JobDTO {
    private Integer quartzId;  //id  主键
    private String jobName;  //任务名称
    private String jobGroup;  //任务分组
    private String cronExpression;  //corn表达式

    private Integer type;//区分不同的任务类型
    private Integer times;//已经执行的次数
    private Date lastExcuteTime;//上次执行时间
    private Date createTime;//创建时间
    private Integer uid;//user id

    private Map<String, Object> params;//执行参数
}
