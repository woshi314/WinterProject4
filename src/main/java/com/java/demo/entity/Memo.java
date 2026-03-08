package com.java.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Memo {

    private Integer id;
    private Integer userId;
    private String title;
    private Boolean completed;
    private String content;
    private Date createTime;
    private Date updateTime;

}
