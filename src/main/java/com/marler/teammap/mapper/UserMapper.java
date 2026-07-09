package com.marler.teammap.mapper;

import com.marler.teammap.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper {

    User selectById(Long id);

    User findByUsername(String username);

    int insert(User user);

    int updateById(User user);

    int deleteById(Long id);
}
