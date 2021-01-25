package com.ziyiou.file.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyFile {
    private Integer id;
    private Integer downloadTimes = 0;
    private String name;
    private Integer userId;
    private Date uploadTime;
    private Long size;
    private String path;
}
