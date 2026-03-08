package com.java.demo.dao;

import com.java.demo.entity.Memo;
import com.java.demo.entity.PageResult;
import com.java.demo.utils.JDBCUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemoDAO {

    private static final Logger log = LoggerFactory.getLogger(MemoDAO.class);

    public int addMemo(Memo memo) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int generatedId = -1;

        try {
            conn = JDBCUtils.getConnection();
            String sql = "INSERT INTO tb_memo(user_id, title, content, completed) VALUES(?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);

            pstmt.setInt(1, memo.getUserId());
            pstmt.setString(2, memo.getTitle());
            pstmt.setString(3, memo.getContent());
            pstmt.setBoolean(4, memo.getCompleted());

            int count = pstmt.executeUpdate();
            if (count > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            log.error("addMemo error", e);
        } finally {
            JDBCUtils.close(conn, pstmt, rs);
        }
        return generatedId;
    }

    public int deleteMemo(int id,int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int count = 0;

        try {
            conn = JDBCUtils.getConnection();
            String sql = "DELETE FROM tb_memo WHERE id = ? AND user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.setInt(2, userId);
            count = pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("deleteMemo error", e);
        } finally {
            JDBCUtils.close(conn, pstmt, null);
        }
        return count;
    }

    public int updateMemoCompleted(int id,int user_id, boolean completed) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int count = 0;

        try {
            conn = JDBCUtils.getConnection();
            String sql = "UPDATE tb_memo SET completed = ? WHERE id = ? AND user_id = ?";
            pstmt = conn.prepareStatement(sql);

            pstmt.setBoolean(1, completed);
            pstmt.setInt(2, id);
            pstmt.setInt(3, user_id);

            count = pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("updateMemo error", e);
        } finally {
            JDBCUtils.close(conn, pstmt, null);
        }
        return count;
    }
    public int updateMemoContent(int id,int user_id, String title,String content) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int count = 0;

        try {
            conn = JDBCUtils.getConnection();
            String sql = "UPDATE tb_memo SET title=? , content=? WHERE id = ? AND user_id = ?";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setInt(3, id);
            pstmt.setInt(4, user_id);

            count = pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("updateMemo error", e);
        } finally {
            JDBCUtils.close(conn, pstmt, null);
        }
        return count;
    }


    public List<Memo> findAllByUserId(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Memo> memoList = new ArrayList<>();

        try {
            conn = JDBCUtils.getConnection();
            String sql = "SELECT * FROM tb_memo WHERE user_id = ? ORDER BY completed ASC, create_time DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Memo memo = new Memo();
                memo.setId(rs.getInt("id"));
                memo.setUserId(rs.getInt("user_id"));
                memo.setTitle(rs.getString("title"));
                memo.setContent(rs.getString("content"));
                memo.setCompleted(rs.getBoolean("completed"));
                memo.setCreateTime(rs.getTimestamp("create_time"));
                memo.setUpdateTime(rs.getTimestamp("update_time"));
                memoList.add(memo);
            }
        } catch (SQLException e) {
            log.error("findAllByUserId error", e);
        } finally {
            JDBCUtils.close(conn, pstmt, rs);
        }
        return memoList;
    }

    public PageResult<Memo> queryMemoByPage(int page, int pageSize, int userId) {
        Connection conn = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs2 = null;
        int count = 0;
        int lowerbound = (page - 1) * pageSize;

        List<Memo> list = new ArrayList<>();
        PageResult<Memo> pageResult = new PageResult<>();

        try {
            conn = JDBCUtils.getConnection();

            String sql1 = "SELECT COUNT(*) FROM tb_memo WHERE user_id = ?";
            pstmt1 = conn.prepareStatement(sql1);
            pstmt1.setInt(1, userId);
            rs1 = pstmt1.executeQuery();
            if (rs1.next()) {
                count = rs1.getInt(1);
            }

            String sql2 = "SELECT * FROM tb_memo WHERE user_id = ? ORDER BY completed ASC, create_time DESC LIMIT ?, ?";
            pstmt2 = conn.prepareStatement(sql2);
            pstmt2.setInt(1, userId);
            pstmt2.setInt(2, lowerbound);
            pstmt2.setInt(3, pageSize);

            rs2 = pstmt2.executeQuery();
            while (rs2.next()) {
                Memo memo = new Memo();
                memo.setId(rs2.getInt("id"));
                memo.setUserId(rs2.getInt("user_id"));
                memo.setTitle(rs2.getString("title"));
                memo.setContent(rs2.getString("content"));
                memo.setCompleted(rs2.getBoolean("completed"));
                memo.setCreateTime(rs2.getTimestamp("create_time"));
                memo.setUpdateTime(rs2.getTimestamp("update_time"));
                list.add(memo);
            }
        } catch (SQLException e) {
            log.error("", e);
        } finally {
            JDBCUtils.close(null, pstmt1, rs1);
            JDBCUtils.close(conn, pstmt2, rs2);
        }

        pageResult.setRows(list);
        pageResult.setTotal(count);
        return pageResult;
    }
}
