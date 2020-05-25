package cn.yiidii.pigeon.quratz.controller.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class QuartzTaskForm {
    @NotNull(message = "任务名称不能为空")
    private String jobName;  //任务名称
    @NotNull(message = "任务分组不能为空")
    private String jobGroup;  //任务分组
    @NotNull(message = "开始时间不能为空")
    private String startTime;  //任务开始时间
    @NotNull(message = "corn不能为空")
    private String cronExpression;  //corn表达式
    @NotNull(message = "任务类型不能为空")
    private Integer type;//区分不同的任务类型
}
