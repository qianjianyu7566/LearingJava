package com.example.springcloud.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper {

    int getStock(Long productId);

    void deductStock(Long productId);
}
