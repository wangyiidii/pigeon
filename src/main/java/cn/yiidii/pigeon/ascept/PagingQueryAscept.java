package cn.yiidii.pigeon.ascept;

import cn.yiidii.pigeon.annotation.PagingQuery;
import cn.yiidii.pigeon.common.util.page.dto.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 分页查询切面
 */
@Aspect
@Component
@Slf4j
public class PagingQueryAscept {
    @Around(value = "@annotation(cn.yiidii.pigeon.annotation.PagingQuery)")
    public Object pagingQuery(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        try {
            // 准备开始分页
            before(joinPoint);
            // 执行被注解的方法
            Object obj = joinPoint.proceed();
            // 包装被注解方法返回的值
            Object result = after(obj);
            return result;
        } catch (Throwable throwable) {
            log.error("aspect execute error : ", throwable);
            throw throwable;
        } finally {
            PageHelper.clearPage();
        }
    }

    private void before(ProceedingJoinPoint point) throws Exception {
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        PagingQuery pagingQuery = targetMethod.getAnnotation(PagingQuery.class);
        // page和size的默认值
        int page = pagingQuery.page();
        int pageSize = pagingQuery.pageSize();

        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            page = Integer.valueOf(request.getParameter("page"));
            pageSize = Integer.valueOf(request.getParameter("pageSize"));
        } catch (Exception e) {
        }

        // 调用PageHelper的分页
        PageHelper.startPage(page, pageSize);
    }

    private Object after(Object obj) {
        assert obj instanceof List;
        PageInfo<?> pageInfo = new PageInfo((List<?>) obj);
        PageResult<?> pageResult = new PageResult<>(pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPages(), pageInfo.getList());
        // 清除分页信息
        PageHelper.clearPage();
        return pageResult;
    }


}
