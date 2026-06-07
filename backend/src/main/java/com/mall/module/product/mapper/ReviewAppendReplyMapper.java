package com.mall.module.product.mapper;

import com.mall.module.product.entity.ReviewAppendReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReviewAppendReplyMapper {

    void insert(ReviewAppendReply reply);

    ReviewAppendReply selectByAppendId(@Param("appendId") Integer appendId);
}
