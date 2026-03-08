package com.java.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Boolean success;
    private String errorMsg;

    public static Result ok(){
        return new Result(true,null);
    }
    public static Result fail(String errorMsg){
        return new Result(false,errorMsg);
    }
}
