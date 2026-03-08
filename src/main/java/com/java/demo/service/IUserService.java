package com.java.demo.service;

import com.java.demo.entity.Result;
import com.java.demo.entity.User;

import javax.servlet.http.HttpSession;

public interface IUserService {
    Result addUser(User user);

    User selectUser(String phone, String password);

    User selectUserByPhone(String phone);

    Result sendCode(String phone, HttpSession session);

    Result updateAvatar(int id, String avatar);
}
