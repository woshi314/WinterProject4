package com.java.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {

    private long total;

    private List<T> records;
    
    private List<T> rows;

    public List<T> getRows() {
        return records;
    }

    public void setRows(List<T> rows) {
        this.records = rows;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

}
