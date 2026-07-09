package com.marler.teammap.mapper;

import com.marler.teammap.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper {

    User selectById(Long id);

    int insert(User user);

    int updateById(User user);

    int deleteById(Long id);
}
