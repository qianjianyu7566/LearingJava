package com.example.springcloud.mapper;

import model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper{

    User selectById(Long userId);

    void updateById(User user);
}
