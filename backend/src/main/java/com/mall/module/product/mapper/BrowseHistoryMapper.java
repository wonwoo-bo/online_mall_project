package com.mall.module.product.mapper;

import com.mall.module.product.entity.BrowseHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BrowseHistoryMapper {

    List<BrowseHistory> selectByUserId(@Param("userId") Integer userId,
                                       @Param("offset") int offset,
                                       @Param("limit") int limit);

    void insert(BrowseHistory browseHistory);

    void deleteById(@Param("id") Integer id);

    void deleteByUserId(@Param("userId") Integer userId);

    void deleteByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);
}
