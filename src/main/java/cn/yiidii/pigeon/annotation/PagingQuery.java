package cn.yiidii.pigeon.annotation;

import lombok.Data;

import java.lang.annotation.*;

/**
 * @author yiidii
 * @desc 分页查询注解.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PagingQuery {
    String pageParamName() default "page";//页号的参数名

    String pageSizeParamName() default "pageSize";//每页行数的参数名

    int page() default 10;

    int pageSize() default 1;
}