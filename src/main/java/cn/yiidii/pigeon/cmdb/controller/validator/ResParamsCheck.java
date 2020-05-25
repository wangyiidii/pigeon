package cn.yiidii.pigeon.cmdb.controller.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = ResParamValidator.class)
public @interface ResParamsCheck {

    String message() default "Adding resources lacks the necessary parameters.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
