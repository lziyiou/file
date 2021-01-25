package com.ziyiou.file.mapper;

import com.ziyiou.file.pojo.MyFile;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MyFileMapper {
    @Insert("insert into myfile (name, userId, uploadTime, size, path) " +
            "values (#{name}, #{userId}, #{uploadTime}, #{size}, #{path})")
    void insert(MyFile myFile);

    @Update("update myfile set downloadTimes = downloadTimes+1 where id = #{id}")
    void update(Integer id);

    @Select("select * from myfile")
    List<MyFile> getAll();

    @Select("select * from myfile where userId=#{userId}")
    List<MyFile> getAllMyFilesByUserId(Integer userId);

    @Delete("delete from myfile where id=#{fileId}")
    void delete(Integer fileId);
}