package cn.yiidii.pigeon.cmdb.controller.validator;

import cn.yiidii.pigeon.cmdb.codefine.CODefine;
import cn.yiidii.pigeon.cmdb.codefine.DefineProxy;
import cn.yiidii.pigeon.cmdb.codefine.ParamDefine;
import cn.yiidii.pigeon.cmdb.controller.form.ResInsertForm;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;
import java.util.Objects;

public class ResParamValidator implements ConstraintValidator<ResParamsCheck, ResInsertForm> {

    @Override
    public boolean isValid(ResInsertForm resInsertForm, ConstraintValidatorContext context) {
        CODefine coDefine = DefineProxy.getCoDefineMap().get(resInsertForm.getDefName());
        if (Objects.isNull(coDefine)) {
            packContext(context, "不支持的资源类型");
            return false;
        } else {
            Map<String, String> paramInput = resInsertForm.getParams();
            Map<String, ParamDefine> paramDefineMap = coDefine.getParams();

            for (Map.Entry<String, ParamDefine> entry : paramDefineMap.entrySet()) {
                String name = entry.getKey();
                ParamDefine paramDefine = entry.getValue();
                if (paramDefine.isRequired()) {
                    if (StringUtils.equals("param.ip", name)) {
                        continue;
                    }
                    if (!paramInput.containsKey(name) || StringUtils.isBlank(paramInput.get(name))) {
                        String alias = paramDefine.getAlias();
                        packContext(context, (StringUtils.isNotBlank(alias) ? alias : paramDefine.getName()) + "不能为空");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void packContext(ConstraintValidatorContext context, String msg) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
    }
}
