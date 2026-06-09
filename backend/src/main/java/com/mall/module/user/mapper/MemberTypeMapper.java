package com.mall.module.user.mapper;

import com.mall.module.user.entity.MemberType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberTypeMapper {
    List<MemberType> selectAll();
    MemberType selectByCode(String levelCode);
    void insert(MemberType memberType);
}
