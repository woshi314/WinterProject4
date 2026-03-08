package com.java.demo.dao;

import com.java.demo.entity.User;
import com.java.demo.utils.JDBCUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private static final Logger log = LoggerFactory.getLogger(UserDAO.class);

    public int addUser(User user) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int generatedId = -1;

        try {
            conn = JDBCUtils.getConnection();
            String sql = "INSERT INTO tb_user(phone, password, nick_name, avatar) VALUES(?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, user.getPhone());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getAvatar());
            int count = pstmt.executeUpdate();
            if (count > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            log.error("addUser error", e);
        } finally {
            JDBCUtils.close(conn, pstmt, rs);
        }
        return generatedId;
    }

    public User login(String phone, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = JDBCUtils.getConnection();
            String sql = "SELECT * FROM tb_user WHERE phone = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, phone);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setPhone(rs.getString("phone"));
                user.setPassword(rs.getString("password"));
                user.setName(rs.getString("nick_name"));
                user.setAvatar(rs.getString("avatar"));
                user.setCreateTime(rs.getTimestamp("create_time"));
            }
        } catch (SQLException e) {
            log.error("selectUser error", e);
        } finally {
            JDBCUtils.close(conn, pstmt, rs);
        }
        return user;
    }

    public User findUserByUsername(String phone) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = JDBCUtils.getConnection();
            String sql = "SELECT * FROM tb_user WHERE phone = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, phone);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setPhone(rs.getString("phone"));
                user.setPassword(rs.getString("password"));
                user.setName(rs.getString("nick_name"));
                user.setAvatar(rs.getString("avatar"));
                user.setCreateTime(rs.getTimestamp("create_time"));
            }
        } catch (SQLException e) {
            log.error("findUserByUsername error", e);
        } finally {
            JDBCUtils.close(conn, pstmt, rs);
        }
        return user;
    }

    public int updateAvatar(int id,String avatar){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;
        int count=0;
        try {
            conn=JDBCUtils.getConnection();
            String sql = "UPDATE tb_user SET avatar = ? WHERE id = ?";
            pstmt=conn.prepareStatement(sql);

            pstmt.setString(1,avatar);
            pstmt.setInt(2,id);

            count=pstmt.executeUpdate();

        }catch (SQLException e){
            log.error("updateAvatar error", e);
        }finally {
            JDBCUtils.close(conn, pstmt, rs);
        }
        return count;
    }
}
