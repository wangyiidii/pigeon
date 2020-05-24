package cn.yiidii.pigeon.shiro.filter;

import cn.yiidii.pigeon.base.exception.TokenAuthFailedException;
import cn.yiidii.pigeon.base.vo.Result;
import cn.yiidii.pigeon.base.vo.ResultCodeEnum;
import cn.yiidii.pigeon.common.util.server.SpringContextUtil;
import cn.yiidii.pigeon.shiro.entity.User;
import cn.yiidii.pigeon.shiro.jwt.JWTToken;
import cn.yiidii.pigeon.shiro.service.impl.UserService;
import cn.yiidii.pigeon.shiro.util.JWTUtil;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 代码的执行流程preHandle->isAccessAllowed->isLoginAttempt->executeLogin
 */
public class JWTFilter extends BasicHttpAuthenticationFilter {

    @Autowired
    private UserService userService;

    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        String authorization = httpServletRequest.getHeader("Token");
        if (StringUtils.isEmpty(authorization)) {
            responseError(request, response, "Token不能为空");
            return false;
        }
        return super.preHandle(request, response);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        try {
            return executeLogin(request, response);
        } catch (Exception e) {
            return false;
        }
    }


//    @Override
//    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
//        HttpServletRequest req = (HttpServletRequest) request;
//        return req.getHeader("Token") != null;
//    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorization = httpServletRequest.getHeader("Token");
        JWTToken token = new JWTToken(authorization);
        try {
            // 提交给realm进行登入，如果错误他会抛出异常并被捕获
            getSubject(request, response).login(token);
            // 如果没有抛出异常则代表登入成功，返回true
            setUserBean(request, response, token);
            return true;
        } catch (Exception e) {
            String msg = "获取登录用户信息失败";
            if (e instanceof TokenExpiredException) {
                msg = e.getMessage();
            } else if (e instanceof TokenAuthFailedException) {
                msg = ResultCodeEnum.TOKEN_EXPIRED.getMsg();
            } else if (e instanceof DisabledAccountException) {
                msg = ResultCodeEnum.DISABLEDACCOUNT.getMsg();
            } else if (e instanceof AuthenticationException) {
                msg = ResultCodeEnum.INCORRECT_USERNAME_OR_PASSWORD.getMsg();
            }
            responseError(request, response, msg);
            return false;
        }
    }

    private void setUserBean(ServletRequest request, ServletResponse response, JWTToken token) {
        String username = JWTUtil.getUsername(token.getPrincipal().toString());
        if (null == userService) {
            userService = SpringContextUtil.getBean(UserService.class);
        }
        User userBean = userService.queryUserByUsername(username);
        request.setAttribute("currentUser", userBean);
    }

    /**
     * 非法url返回身份错误信息
     */
    private void responseError(ServletRequest request, ServletResponse response, String msg) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("utf-8");
            out = response.getWriter();
            response.setContentType("application/json; charset=utf-8");
            out.print(JSONObject.toJSONString(Result.error("500000", msg)));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
