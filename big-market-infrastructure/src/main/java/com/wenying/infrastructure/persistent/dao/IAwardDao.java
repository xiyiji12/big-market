package com.wenying.infrastructure.persistent.dao;

import com.wenying.infrastructure.persistent.po.Award;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 奖品表 DAO
 */
@Mapper
public interface IAwardDao {
    List<Award> queryAwardList();
}
