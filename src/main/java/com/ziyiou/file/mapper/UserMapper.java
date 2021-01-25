package com.ziyiou.file.mapper;

import com.ziyiou.file.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

@Mapper
public interface UserMapper {
    @Select("select * from user where username = #{username}")
    User selectByUsername(String username);

    @Select("select * from user")
    Set<User> getAllUsers();

    @Select("select * from user where id=#{id}")
    User getUserById(Integer id);

    @Select("select id from user where username=#{username}")
    Integer getIdByUsername(String username);

}