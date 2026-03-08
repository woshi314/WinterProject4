package com.java.demo.service.impl;

import com.java.demo.dao.MemoDAO;
import com.java.demo.entity.Memo;
import com.java.demo.entity.PageResult;
import com.java.demo.entity.Result;
import com.java.demo.service.IMemoService;

import java.util.List;

public class MemoServiceImpl implements IMemoService {

    private MemoDAO memoDAO = new MemoDAO();

    @Override
    public Result addMemo(Memo memo) {

        if (memo.getUserId() == null) {
            return Result.fail("用户信息错误");
        }
        // 设置默认完成状态为 false
        if (memo.getCompleted() == null) {
            memo.setCompleted(false);
        }

        int generatedId = memoDAO.addMemo(memo);
        if (generatedId > 0) {
            memo.setId(generatedId);
            return Result.ok();
        } else {
            return Result.fail("添加备忘录失败");
        }
    }

    @Override
    public Result deleteMemo(int id,int userId) {
        int count = memoDAO.deleteMemo(id,userId);
        if (count > 0) {
            return Result.ok();
        } else {
            return Result.fail("删除失败，备忘录可能不存在");
        }
    }

    @Override
    public Result updateMemoCompleted(int id, int user_id, boolean completed) {
        int count = memoDAO.updateMemoCompleted(id,user_id,completed);
        if (count > 0) {
            return Result.ok();
        } else {
            return Result.fail("设置完成状态失败，备忘录可能不存在");
        }
    }

    @Override
    public Result updateMemoContent(int id, int user_id, String title, String content) {
        int count = memoDAO.updateMemoContent(id,user_id,title,content);
        if (count > 0) {
            return Result.ok();
        } else {
            return Result.fail("编辑文本失败，备忘录可能不存在");
        }
    }

//    @Override
//    public Result updateMemo(Memo memo) {
//        if (memo == null || memo.getId() == null) {
//            return Result.fail("备忘录ID不能为空");
//        }
//        int count = memoDAO.updateMemo(memo);
//        if (count > 0) {
//            return Result.ok();
//        } else {
//            return Result.fail("更新失败，备忘录可能不存在");
//        }
//    }

    @Override
    public List<Memo> findAllByUserId(int userId) {
        return memoDAO.findAllByUserId(userId);
    }

    @Override
    public PageResult<Memo> queryMemoByPage(int page, int pageSize, int userId) {
        return memoDAO.queryMemoByPage(page, pageSize, userId);
    }
}
