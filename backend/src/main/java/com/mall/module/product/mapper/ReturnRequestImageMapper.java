package com.mall.module.product.mapper;

import com.mall.module.product.entity.ReturnRequestImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReturnRequestImageMapper {

    List<ReturnRequestImage> selectByReturnRequestId(@Param("returnRequestId") Integer returnRequestId);

    void insert(ReturnRequestImage image);
}
