package org.crudboy.cloud.mall.user.model.dao;

import org.apache.ibatis.annotations.Param;
import org.crudboy.cloud.mall.user.model.pojo.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByName(String username);

    User selectForLogin(@Param("username") String username, @Param("password") String password);
}