package com.mall.common;

import lombok.Data;
import java.util.List;

/**
 * 分页结果封装类
 */
@Data
public class PageResult<T> {
    private long total;
    private List<T> list;
    private int pageNum;
    private int pageSize;
    private int totalPages;

    public PageResult() {
    }

    public PageResult(long total, List<T> list, int pageNum, int pageSize) {
        this.total = total;
        this.list = list;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }

    public static <T> PageResult<T> of(long total, List<T> list, int pageNum, int pageSize) {
        return new PageResult<>(total, list, pageNum, pageSize);
    }
}
