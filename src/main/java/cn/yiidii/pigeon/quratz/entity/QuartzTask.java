package cn.yiidii.pigeon.quratz.entity;

import lombok.Data;

import java.util.Date;

@Data
public class QuartzTask {
    private Integer quartzId;  //id  主键
    private String jobName;  //任务名称
    private String jobGroup;  //任务分组
    private String startTime;  //任务开始时间
    private String cronExpression;  //corn表达式
    private String invokeParam;//需要传递的参数

    private Integer type;//区分不同的任务类型
    private Integer times;//已经执行的次数
    private Date lastExcuteTime;//上次执行时间
    private Date createTime;//创建时间
    private Integer uid;//user id
}
