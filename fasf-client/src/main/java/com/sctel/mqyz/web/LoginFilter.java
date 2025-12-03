package com.sctel.mqyz.web;

import com.sctel.mqyz.domain.vo.JsonResult;
import org.fasf.mqyz.api.MeleApi;
import org.fasf.mqyz.model.vo.mele.LoginInfo;
import org.fasf.mqyz.model.vo.mele.MeleResult;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author duankun
 * @date: 2025/12/3
 */
public class LoginFilter extends OncePerRequestFilter {
    private final MeleApi meleApi;
    public LoginFilter(MeleApi meleApi) {
        this.meleApi = meleApi;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String sessionId = getSessionId(httpServletRequest);
        if (sessionId == null) {
            writeResponse(httpServletResponse, JsonResult.fail(HttpServletResponse.SC_BAD_REQUEST, "获取cookie失败"));
            return;
        }
        MeleResult<LoginInfo> loginInfoMeleResult = meleApi.queryUserBySessionId(sessionId);
        if (loginInfoMeleResult.getCode() != 200) {
            writeResponse(httpServletResponse, JsonResult.fail(HttpServletResponse.SC_UNAUTHORIZED, "未登录"));
            return;
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void writeResponse(HttpServletResponse response, JsonResult<?> result) throws IOException {
        response.setStatus(result.getCode());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(result.toJsonString());
        response.getWriter().flush();
    }

    private String getSessionId(HttpServletRequest request) {
        javax.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (javax.servlet.http.Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
