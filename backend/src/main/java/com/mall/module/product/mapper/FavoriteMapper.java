package com.mall.module.product.mapper;

import com.mall.module.product.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    List<Favorite> selectByUserId(@Param("userId") Integer userId,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);

    int countByUserId(@Param("userId") Integer userId);

    int exists(@Param("userId") Integer userId, @Param("productId") Integer productId);

    void insert(Favorite favorite);

    void delete(@Param("userId") Integer userId, @Param("productId") Integer productId);
}
