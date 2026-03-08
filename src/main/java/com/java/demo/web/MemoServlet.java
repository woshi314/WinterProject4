package com.java.demo.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.demo.entity.Memo;
import com.java.demo.entity.PageResult;
import com.java.demo.entity.Result;
import com.java.demo.entity.User;
import com.java.demo.service.IMemoService;
import com.java.demo.service.impl.MemoServiceImpl;
import com.java.demo.utils.LoginFilter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@Slf4j
@WebServlet("/memo/*")
public class MemoServlet extends HttpServlet {
    private final IMemoService memoService = new MemoServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/read".equals(pathInfo)) {
            readMemo(request, response);
        } else if ("/list".equals(pathInfo)) {
            readMemoList(request, response);
        } else {
            readMemoList(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/create".equals(pathInfo)) {
            createMemo(request, response);
        } else if ("/delete".equals(pathInfo)) {
            deleteMemo(request, response);
        } else if ("/updateCompleted".equals(pathInfo)) {
            updateMemoCompleted(request, response);
        }else if ("/updateContent".equals(pathInfo)) {
            updateMemoContent(request, response);
        }
    }

    private void readMemo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
    }

    private void readMemoList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = LoginFilter.getCurrentUser();
        if (user == null) {
            response.setContentType("application/json;charset=utf-8");
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("errorMsg", "用户未登录");
            objectMapper.writeValue(response.getWriter(), result);
            return;
        }

        int userId = user.getId();
        
        int page = 1;
        int pageSize = 12;
        
        String pageStr = request.getParameter("page");
        String sizeStr = request.getParameter("size");
        
        if (pageStr != null && !pageStr.isEmpty()) {
            page = Integer.parseInt(pageStr);
        }
        if (sizeStr != null && !sizeStr.isEmpty()) {
            pageSize = Integer.parseInt(sizeStr);
        }

        log.info("查询备忘录列表 - userId: {}, page: {}, size: {}", userId, page, pageSize);

        PageResult<Memo> pageResult = memoService.queryMemoByPage(page, pageSize, userId);

        response.setContentType("application/json;charset=utf-8");
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", true);
        result.put("data", pageResult);
        objectMapper.writeValue(response.getWriter(), result);
    }

    private void createMemo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = LoginFilter.getCurrentUser();
        if (user == null) {
            response.setContentType("application/json;charset=utf-8");
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("errorMsg", "用户未登录");
            objectMapper.writeValue(response.getWriter(), result);
            return;
        }

        String title = request.getParameter("title");
        String content = request.getParameter("content");

        if (title == null || title.trim().isEmpty()) {
            response.setContentType("application/json;charset=utf-8");
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("errorMsg", "标题不能为空");
            objectMapper.writeValue(response.getWriter(), result);
            return;
        }

        if (content == null || content.trim().isEmpty()) {
            response.setContentType("application/json;charset=utf-8");
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("errorMsg", "内容不能为空");
            objectMapper.writeValue(response.getWriter(), result);
            return;
        }

        Memo memo = new Memo();
        memo.setUserId(user.getId());
        memo.setTitle(title);
        memo.setContent(content);
        memo.setCompleted(false);

        log.info("创建备忘录 - userId: {}, title: {}", user.getId(), title);

        Result result = memoService.addMemo(memo);
        response.setContentType("application/json;charset=utf-8");
        Map<String, Object> responseResult = new java.util.HashMap<>();
        responseResult.put("success", result.getSuccess());
        if (result.getErrorMsg() != null) {
            responseResult.put("errorMsg", result.getErrorMsg());
        }
        objectMapper.writeValue(response.getWriter(), responseResult);
    }

    private void deleteMemo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.setContentType("application/json;charset=utf-8");
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("errorMsg", "备忘录ID不能为空");
            objectMapper.writeValue(response.getWriter(), result);
            return;
        }

        int id = Integer.parseInt(idStr);
        int userId = LoginFilter.getCurrentUser().getId();
        Result result = memoService.deleteMemo(id, userId);
        
        response.setContentType("application/json;charset=utf-8");
        Map<String, Object> responseResult = new java.util.HashMap<>();
        responseResult.put("success", result.getSuccess());
        if (result.getErrorMsg() != null) {
            responseResult.put("errorMsg", result.getErrorMsg());
        }
        objectMapper.writeValue(response.getWriter(), responseResult);
    }

//    private void updateMemo(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        User user = LoginFilter.getCurrentUser();
//        if (user == null) {
//            response.setContentType("application/json;charset=utf-8");
//            Map<String, Object> result = new java.util.HashMap<>();
//            result.put("success", false);
//            result.put("errorMsg", "用户未登录");
//            objectMapper.writeValue(response.getWriter(), result);
//            return;
//        }
//
//        String id = request.getParameter("id");
//        String title = request.getParameter("title");
//        String content = request.getParameter("content");
//        String completed = request.getParameter("completed");
//
//        if (id == null || id.isEmpty()) {
//            response.setContentType("application/json;charset=utf-8");
//            Map<String, Object> result = new java.util.HashMap<>();
//            result.put("success", false);
//            result.put("errorMsg", "备忘录ID不能为空");
//            objectMapper.writeValue(response.getWriter(), result);
//            return;
//        }
//
//        Memo memo = new Memo();
//        memo.setId(Integer.parseInt(id));
//        memo.setUserId(user.getId());
//        memo.setTitle(title);
//        memo.setContent(content);
//        memo.setCompleted(Boolean.parseBoolean(completed));
//
//        Result result = memoService.updateMemo(memo);
//
//        response.setContentType("application/json;charset=utf-8");
//        Map<String, Object> responseResult = new java.util.HashMap<>();
//        responseResult.put("success", result.getSuccess());
//        if (result.getErrorMsg() != null) {
//            responseResult.put("errorMsg", result.getErrorMsg());
//        }
//        objectMapper.writeValue(response.getWriter(), responseResult);
//    }
    private void updateMemoCompleted(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = LoginFilter.getCurrentUser();
        if (user == null) {
            response.setContentType("application/json;charset=utf-8");
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("errorMsg", "用户未登录");
            objectMapper.writeValue(response.getWriter(), result);
            return;
        }

        String id = request.getParameter("id");
        String completed = request.getParameter("completed");

        if (id == null || id.isEmpty()) {
            response.setContentType("application/json;charset=utf-8");
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("errorMsg", "备忘录ID不能为空");
            objectMapper.writeValue(response.getWriter(), result);
            return;
        }

        int indId=Integer.parseInt(id);
        int intUserId=user.getId();
        boolean booleanCompleted = "true".equalsIgnoreCase(completed);

        Result result = memoService.updateMemoCompleted(indId,intUserId,booleanCompleted);

        response.setContentType("application/json;charset=utf-8");
        Map<String, Object> responseResult = new java.util.HashMap<>();
        responseResult.put("success", result.getSuccess());
        if (result.getErrorMsg() != null) {
            responseResult.put("errorMsg", result.getErrorMsg());
        }
        objectMapper.writeValue(response.getWriter(), responseResult);
    }

    private void updateMemoContent(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = LoginFilter.getCurrentUser();
        if (user == null) {
            response.setContentType("application/json;charset=utf-8");
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("errorMsg", "用户未登录");
            objectMapper.writeValue(response.getWriter(), result);
            return;
        }

        String id = request.getParameter("id");
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        if (id == null || id.isEmpty()) {
            response.setContentType("application/json;charset=utf-8");
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("errorMsg", "备忘录ID不能为空");
            objectMapper.writeValue(response.getWriter(), result);
            return;
        }

        int indId=Integer.parseInt(id);
        int intUserId=user.getId();

        Result result = memoService.updateMemoContent(indId,intUserId,title,content);

        response.setContentType("application/json;charset=utf-8");
        Map<String, Object> responseResult = new java.util.HashMap<>();
        responseResult.put("success", result.getSuccess());
        if (result.getErrorMsg() != null) {
            responseResult.put("errorMsg", result.getErrorMsg());
        }
        objectMapper.writeValue(response.getWriter(), responseResult);
    }
}
