package com.java.demo.web;

import cn.hutool.core.lang.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.demo.entity.Result;
import com.java.demo.entity.User;
import com.java.demo.service.impl.UserServiceImpl;
import com.java.demo.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@WebServlet("/login/*")
public class LoginServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/code".equals(pathInfo)) {
            sendCode(request, response);
        } else {
            handleLogin(request, response);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String phone = request.getParameter("phone");
        String loginType = request.getParameter("loginType");
        String password = request.getParameter("password");
        String code = request.getParameter("code");

        log.info("登录请求:phone: {}, loginType: {}", phone, loginType);

        Map<String, Object> result = new HashMap<>();
        UserServiceImpl userService = new UserServiceImpl();

        if ("code".equals(loginType)) {
            String redisCode = RedisUtils.get("code:" + phone);
            log.info("输入验证码: {},Redis验证码: {}", code, redisCode);

            if (code == null || !code.equalsIgnoreCase(redisCode)) {
                result.put("success", false);
                result.put("errorMsg", "验证码错误");
            } else {
                User user = userService.selectUserByPhone(phone);
                if (user != null) {
                    RedisUtils.del("code:" + phone);
                    
                    String token = UUID.randomUUID().toString();
                    String userJson = objectMapper.writeValueAsString(user);
                    RedisUtils.set("login:token:" + token, userJson, 60*60*24*7);
                    
                    result.put("success", true);
                    Map<String, Object> data = new HashMap<>();
                    data.put("token", token);
                    data.put("user", user);
                    result.put("data", data);
                } else {
                    result.put("success", false);
                    result.put("errorMsg", "用户不存在，请先注册");
                }
            }
        } else {
            User user = userService.selectUser(phone, password);
            log.info("密码登录 - user: {}", user);

            if (user != null) {
                String token = UUID.randomUUID().toString();
                String userJson = objectMapper.writeValueAsString(user);
                RedisUtils.set("login:token:" + token, userJson, 60*60*24*7);

                result.put("success", true);
                Map<String, Object> data = new HashMap<>();
                data.put("token", token);
                data.put("user", user);
                result.put("data", data);
            } else {
                result.put("success", false);
                result.put("errorMsg", "手机号或密码错误");
            }
        }

        response.setContentType("application/json;charset=utf-8");
        objectMapper.writeValue(response.getWriter(), result);
    }

    private void sendCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String phone = request.getParameter("phone");
        UserServiceImpl userService = new UserServiceImpl();
        Result result = userService.sendCode(phone, request.getSession());

        response.setContentType("application/json;charset=utf-8");
        objectMapper.writeValue(response.getWriter(), result);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if ("/logout".equals(pathInfo)) {
            handleLogout(request, response);
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getHeader("authorization");
        Map<String, Object> result = new HashMap<>();

        if (token != null && !token.isEmpty()) {
            RedisUtils.del("login:token:" + token);
            result.put("success", true);
            result.put("message", "退出登录成功");
            log.info("退出了");
        } else {
            result.put("success", false);
            result.put("errorMsg", "未找到登录信息");
        }

        response.setContentType("application/json;charset=utf-8");
        objectMapper.writeValue(response.getWriter(), result);
    }
}
