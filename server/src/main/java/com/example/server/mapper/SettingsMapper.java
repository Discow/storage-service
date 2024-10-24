package com.example.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SettingsMapper {
    //根据keyName获取value
    @Select("select value from settings where key_name=#{keyName}")
    String getValue(@Param("keyName") String keyName);
}
