package cn.yiidii.pigeon.ascept;

import cn.yiidii.pigeon.annotation.OptLogAnnotation;
import cn.yiidii.pigeon.common.util.server.IPUtil;
import cn.yiidii.pigeon.optlog.entity.OptLog;
import cn.yiidii.pigeon.optlog.service.impl.OptLogService;
import cn.yiidii.pigeon.shiro.entity.User;
import cn.yiidii.pigeon.shiro.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Objects;

/**
 * 操作日志切面
 */
@Aspect
@Component
@Slf4j
public class OptLogAscept {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private OptLogService optLogService;
    @Autowired
    private SecurityUtil securityUtil;

    @Around(value = "@annotation(cn.yiidii.pigeon.annotation.OptLogAnnotation)")//已注解 @OptLogAnnotation 为切点
    public Object insertOptLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();//注解的方法
            OptLogAnnotation logAnnotation = method.getAnnotation(OptLogAnnotation.class);//注解
            OptLog optLog = packageOptLog(getLogContent(method), getModule(method));
            optLogService.insert(optLog);
        } catch (Exception e) {
        }
        return result;
    }

    private OptLog packageOptLog(String content, String module) {
        OptLog optLog = new OptLog();
        User user = securityUtil.getCurrUser();
        if (!Objects.isNull(user)) {
            optLog.setUid(user.getId());
            optLog.setSubject(user.getUsername());
        }
        String ip = IPUtil.getIpAddr(request);
        optLog.setIp(ip);
        optLog.setLocationInfo(IPUtil.getLocationByIp(ip));
        optLog.setModule(module);
        optLog.setContent(content);
        optLog.setCreateTime(new Date());
        return optLog;
    }

    private String getLogContent(Method method) {
        String content = "";
        try {
            OptLogAnnotation optLogAnnotation = method.getAnnotation(OptLogAnnotation.class);
            content = optLogAnnotation.desc();
        } catch (Exception e) {
        }
        if (StringUtils.isEmpty(content)) {
            try {
                ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
                content = apiOperation.value();
            } catch (Exception e) {
            }
        }
        return content;
    }


    private String getModule(Method method) {
        String module = "NO_MODULE";
        try {
            Api apiClazz = method.getClass().getAnnotation(Api.class);
            module = StringUtils.join(apiClazz.tags(), " ");
        } catch (Exception e) {
        }

        return module;
    }

}
