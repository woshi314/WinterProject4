package com.java.demo.utils;

import com.java.demo.entity.User;
import com.java.demo.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;

@Slf4j
public class DBConnectionTest {

    public static void main(String[] args) {
        Connection conn = null;
        System.out.println("正在尝试获取数据库连接...");
        try {
            conn = JDBCUtils.getConnection();

            if (conn != null) {
                System.out.println("数据库连接成功！");
                System.out.println("Connection 对象: " + conn);
                UserServiceImpl userService=new UserServiceImpl();
                User user=new User();
                user.setPhone("admin");
                user.setPassword("123456");
                userService.addUser(user);
                log.info(user.getPhone());
            } else {
                System.out.println("获取连接失败，JDBCUtils.getConnection() 返回了 null。请检查 JDBCUtils 的实现逻辑。");
            }

        } catch (Exception e) {
            System.out.println("获取连接时抛出异常！请检查数据库配置、驱动或服务状态。");
            e.printStackTrace();
        } finally {
            System.out.println("测试结束，正在关闭连接...");
            if (conn != null) {
                JDBCUtils.close(conn, null, null);
            }
        }
    }
}
