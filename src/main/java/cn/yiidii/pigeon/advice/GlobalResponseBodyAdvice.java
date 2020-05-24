package cn.yiidii.pigeon.advice;

import cn.yiidii.pigeon.base.vo.Result;
import cn.yiidii.pigeon.common.util.page.PageUtil;
import cn.yiidii.pigeon.common.util.page.dto.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局处理响应体
 */
@RestControllerAdvice
@Slf4j
public class GlobalResponseBodyAdvice implements ResponseBodyAdvice {

    /**
     * 需要忽略的地址
     */
    private static String[] ignores = new String[]{
            //过滤swagger相关的请求的接口，不然swagger会提示base-url被拦截
            "/swagger-resources",
            "/v2/api-docs"
    };

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object obj, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest request, ServerHttpResponse serverHttpResponse) {

        //判断url是否需要拦截
        if (this.ignoring(request.getURI().toString())) {
            return obj;
        }

        //如果返回的数据是ResultObjectModel、Byte类型则不进行封装
        if (obj instanceof Result || obj instanceof Byte) {
            return obj;
        }
        if (obj instanceof PageResult) {
            return PageUtil.parsePageResult(((PageResult) obj));
        }

        return getWrapperResponse(request, obj);
    }

    /**
     * 判断url是否需要拦截
     *
     * @param uri
     * @return
     */
    private boolean ignoring(String uri) {
        for (String string : ignores) {
            if (uri.contains(string)) {
                return true;
            }
        }
        return false;
    }

    private Result getWrapperResponse(ServerHttpRequest request, Object data) {
        return Result.success(data);
    }
}
