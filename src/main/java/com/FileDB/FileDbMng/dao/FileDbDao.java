package com.FileDB.FileDbMng.dao;

import com.FileDB.FileDbMng.vo.FileDb;
import org.springframework.stereotype.Repository;

@Repository
public interface FileDbDao {
    void insertDB(FileDb fileDb);
    void updateId();

    void insertFileDB(String fileName);
}
