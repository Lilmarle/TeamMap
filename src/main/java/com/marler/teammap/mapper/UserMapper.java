package com.marler.teammap.mapper;

import com.marler.teammap.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper {

    User selectById(Long id);

    User findByUsername(String username);

    List<User> findByUsernamePrefix(@Param("prefix") String prefix);

    int insert(User user);

    int updateById(User user);

    int deleteById(Long id);
}
