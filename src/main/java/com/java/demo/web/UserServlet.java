package com.java.demo.web;

import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.demo.entity.Result;
import com.java.demo.entity.User;
import com.java.demo.service.IMemoService;
import com.java.demo.service.IUserService;
import com.java.demo.service.impl.MemoServiceImpl;
import com.java.demo.service.impl.UserServiceImpl;
import com.java.demo.utils.LoginFilter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Slf4j
@WebServlet("/user/*")
@MultipartConfig
public class UserServlet extends HttpServlet {
    private final IUserService userService = new UserServiceImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private void error(HttpServletResponse response,String msg) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", false);
        result.put("errorMsg", msg);
        objectMapper.writeValue(response.getWriter(), result);
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if ("/me".equals(pathInfo)) {
            getCurrentUserInfo(request, response);
        }
    }

    private void getCurrentUserInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = LoginFilter.getCurrentUser();
        response.setContentType("application/json;charset=utf-8");
        Map<String, Object> result = new java.util.HashMap<>();
        
        if (user == null) {
            result.put("success", false);
            result.put("errorMsg", "用户未登录");
            response.setStatus(401);
        } else {
            result.put("success", true);
            result.put("data", user);
        }
        
        objectMapper.writeValue(response.getWriter(), result);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if ("/updateAvater".equals(pathInfo)) {
            updateUserAvater(request, response);
        }
    }

    private void updateUserAvater(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = LoginFilter.getCurrentUser();
        if (user == null) {
            response.setContentType("application/json;charset=utf-8");
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", false);
            result.put("errorMsg", "用户未登录");
            objectMapper.writeValue(response.getWriter(), result);
            return;
        }
        String id=request.getParameter("id");
        Part part=request.getPart("avatar");

        if(id==null||id.isEmpty()){
            error(response,"用户不能为空");
            return;
        }

        if(part==null){
            error(response,"未选择文件");
            return;
        }

        String contentType=part.getContentType();
        if(!contentType.startsWith("image/")){
            error(response,"只能上传图片文件");
            return;
        }

        String ext=".jpg";
//        String contentDisp=part.getHeader("content-disposition");
//        String[] items=contentDisp.split(";");
//        for(String item:items){
//            if(item.startsWith("filename=")){
//                ext=item.split("=")[1];
//            }
//        }

        String fileName=user.getId()+ext;

        String uploadPath=request.getServletContext().getRealPath("/")+"upload"+File.separator+"avatar";
        File uploadDir=new File(uploadPath);
        if(!uploadDir.exists()){
            uploadDir.mkdirs();
        }

        String filePath=uploadPath+File.separator+fileName;
        try(
            InputStream inputStream=part.getInputStream();
            FileOutputStream outputStream=new FileOutputStream(filePath)){
            byte[] buffer=new byte[1024];
            int bytesRead;
            while((bytesRead=inputStream.read(buffer))!=-1){
                outputStream.write(buffer,0,bytesRead);
            }
        }

        String avatarPath="/upload/avatar/"+fileName;

        int intId=Integer.parseInt(id);

        Result result=userService.updateAvatar(intId, avatarPath);

        response.setContentType("application/json;charset=utf-8");
        Map<String, Object> responseResult = new java.util.HashMap<>();
        responseResult.put("success", result.getSuccess());
        if (result.getErrorMsg() != null) {
            responseResult.put("errorMsg", result.getErrorMsg());
        }
        objectMapper.writeValue(response.getWriter(), responseResult);
    }
}
