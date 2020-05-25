package cn.yiidii.pigeon.cmdb.controller.form;

import cn.yiidii.pigeon.cmdb.controller.validator.ResParamsCheck;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@ResParamsCheck
public class ResInsertForm {
    @NotNull(message = "别名不能为空")
    private String alias;
    @NotNull(message = "IP不能为空")
    private String host;
    @NotNull(message = "资源定义名称不能为空")
    private String defName;
    private Map<String, String> params;

}
