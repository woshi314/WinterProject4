package com.java.demo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.demo.entity.Result;
import com.java.demo.entity.User;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@WebFilter("/*")
public class LoginFilter implements Filter {

    private static final ThreadLocal<User> userThreadLocal = new ThreadLocal<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/login/login",
            "/login/code",
            "/register",
            ".html",
            ".css",
            ".js",
            ".jpg",
            ".png",
            ".webp"
    );

    private void unauthorized(String eroMsg, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("login.html").forward(request, response);
    }

    public static User getCurrentUser() {
        return userThreadLocal.get();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        for (String path : EXCLUDE_PATHS) {
            if (uri.contains(path)) {
                chain.doFilter(request, response);
                return;
            }
        }

        String token = req.getHeader("authorization");

        if (token == null || token.isEmpty()) {
            unauthorized("尚未登录", req, resp);
            return;
        }

        String userJson = RedisUtils.get("login:token:" + token);

        if (userJson == null || userJson.isEmpty()) {
            unauthorized("登录过期,请重新登录", req, resp);
            return;
        }
        
        try {
            User user = objectMapper.readValue(userJson, User.class);
            userThreadLocal.set(user);
            RedisUtils.expire("login:token:" + token, 60 * 60 * 24 * 7);
        } catch (Exception e) {
            unauthorized("登录信息异常,请重新登录", req, resp);
            return;
        }

        chain.doFilter(request, response);
        userThreadLocal.remove();
    }
}
