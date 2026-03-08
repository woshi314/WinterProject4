package com.java.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;
    private String phone;
    private String password;
    private String nickName;
    private String avatar;
    private Date createTime;
}
