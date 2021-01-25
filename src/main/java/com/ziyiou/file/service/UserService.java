package com.ziyiou.file.service;

import com.ziyiou.file.mapper.UserMapper;
import com.ziyiou.file.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public Set<User> getAllUsers(){
        return userMapper.getAllUsers();
    }
}