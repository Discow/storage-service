package com.example.server.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface SettingsMapper {
    //根据keyName获取value
    @Select("select value from settings where key_name=#{keyName}")
    String getValue(@Param("keyName") String keyName);

    //增加新配置项
    @Insert("insert into settings(key_name,value) values(#{keyName},#{value})")
    void addKey(@Param("keyName") String keyName, @Param("value") String value);

    //修改配置项
    @Update("update settings set value=#{value} where key_name=#{keyName}")
    void updateValue(@Param("keyName") String keyName, @Param("value") String value);
}
