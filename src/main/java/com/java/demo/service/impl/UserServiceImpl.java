package com.java.demo.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.java.demo.dao.UserDAO;
import com.java.demo.entity.Result;
import com.java.demo.entity.User;
import com.java.demo.service.IUserService;
import com.java.demo.utils.RedisUtils;
import com.java.demo.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpSession;

@Slf4j
public class UserServiceImpl implements IUserService {
    private UserDAO userDAO = new UserDAO();
    @Override
    public Result addUser(User user) {
        if (user == null || user.getPhone() == null || user.getPassword() == null ||
                user.getPhone().trim().isEmpty() || user.getPassword().trim().isEmpty()) {
            return Result.fail("手机号或密码不能为空");
        }

        User existingUser = userDAO.findUserByUsername(user.getPhone());
        if (existingUser != null) {
            return Result.fail("手机号已被注册");
        }

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            user.setName(user.getPhone());
        }

        int generatedId = userDAO.addUser(user);

        if (generatedId > 0) {
            user.setId(generatedId);
            user.setPassword(null);
            return Result.ok();
        } else {
            return Result.fail("注册失败，请稍后重试");
        }
    }

    @Override
    public User selectUser(String phone, String password) {
        return userDAO.login(phone,password);
    }

    @Override
    public User selectUserByPhone(String phone) {
        return userDAO.findUserByUsername(phone);
    }

    @Override
    public Result sendCode(String phone, HttpSession session) {

        if(RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail("手机号格式错误!");
        }

        String code = RandomUtil.randomNumbers(6);

        RedisUtils.set("code:" + phone, code, 300);

        log.info("后台模拟发送短信验证码:{}",code);

        return Result.ok();
    }

    @Override
    public Result updateAvatar(int id, String avatar) {
        return userDAO.updateAvatar(id, avatar) == 1 ? Result.ok() : Result.fail("更新头像失败");
    }
}
