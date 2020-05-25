package cn.yiidii.pigeon.cmdb.controller.form;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class IndicatorInsertForm {

    @NotNull(message = "defName不能为空")
    private String defName;
    @NotNull(message = "别名不能为空")
    private String alias;
    @Pattern(regexp = "\\d", message = "采集参数格式错误")
    private Long interval = 60L;//unit: s
    private String method;
    @NotNull(message = "超时时间不能为空")
    @Pattern(regexp = "\\d", message = "超时时间格式错误")
    private Long timeout = 30L;//unit: s
    @Pattern(regexp = "\\d", message = "重试次数格式错误")
    private Integer retryTimes = 3;

}
