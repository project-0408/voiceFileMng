package com.FileDB.FileDbMng.vo;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Alias("fileDbVO")
@Data
public class FileDb {
    private String sttResult;
    private int sttCode;
    private String model;
    private String path;
    private String local;
    private String fileName;
    private String recordTime;
    private Double languageKr;
    private Double languageEn;
}
