package com.mall.module.user.mapper;

import com.mall.module.user.entity.MemberInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberInfoMapper {
    MemberInfo selectByUserId(Integer userId);
    void insert(MemberInfo memberInfo);
    void updateByUserId(MemberInfo memberInfo);
}
