package cn.yiidii.pigeon.ext.controller.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BjBusForm {
    @NotBlank(message = "公交线路不能为空")
    String bid;
    @NotBlank(message = "线路方向不能为空")
    String rid;
    @NotBlank(message = "公交站点不能为空")
    String sid;
}
