package com.mall.module.user.mapper;

import com.mall.module.user.entity.PointsRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PointsRecordMapper {
    void insert(PointsRecord record);
    List<PointsRecord> selectByUserId(Integer userId);
}
