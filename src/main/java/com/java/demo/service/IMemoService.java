package com.java.demo.service;

import com.java.demo.entity.Memo;
import com.java.demo.entity.PageResult;
import com.java.demo.entity.Result;

import java.util.List;

public interface IMemoService {

    Result addMemo(Memo memo);

    Result deleteMemo(int id,int userId);

    Result updateMemoCompleted(int id,int user_id,boolean completed);

    Result updateMemoContent(int id,int user_id,String title,String content);

    List<Memo> findAllByUserId(int userId);
    
    PageResult<Memo> queryMemoByPage(int page, int pageSize, int userId);
}
