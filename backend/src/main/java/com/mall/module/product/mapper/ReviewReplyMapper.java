package com.mall.module.product.mapper;

import com.mall.module.product.entity.ReviewReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReviewReplyMapper {

    ReviewReply selectByReviewId(@Param("reviewId") Integer reviewId);

    void insert(ReviewReply reviewReply);
}
