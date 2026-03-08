package com.java.demo.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.demo.entity.Result;
import com.java.demo.entity.User;
import com.java.demo.service.IUserService;
import com.java.demo.service.impl.UserServiceImpl;
import com.java.demo.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private IUserService userService = new UserServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String code = request.getParameter("code");

        log.info("注册请求 - phone: {}, code: {}", phone, code);

        String redisCode = RedisUtils.get("code:" + phone);
        log.info("Redis中的验证码:key: code:{}, value: {}", phone, redisCode);

        if (code == null || !code.equalsIgnoreCase(redisCode)) {
            log.warn("验证码验证失败:输入: {}, Redis: {}", code, redisCode);
            response.setContentType("application/json;charset=utf-8");
            objectMapper.writeValue(response.getWriter(), Result.fail("验证码错误"));
            return;
        }

        User user = new User();
        user.setPhone(phone);
        user.setPassword(password);
        user.setNickName(name);

        Result result = userService.addUser(user);

        if (result.getSuccess()) {
            RedisUtils.del("code:" + phone);
        }

        response.setContentType("application/json;charset=utf-8");
        objectMapper.writeValue(response.getWriter(), result);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}
