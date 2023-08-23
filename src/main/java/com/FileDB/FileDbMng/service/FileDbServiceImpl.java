package com.FileDB.FileDbMng.service;

import com.FileDB.FileDbMng.dao.FileDbDao;
import com.FileDB.FileDbMng.vo.ApiResult;
import com.FileDB.FileDbMng.vo.FileDb;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FileDbServiceImpl implements FileDbService {

    private FileDbDao fileDbDao;

    public FileDbServiceImpl(FileDbDao labelDao) {
        this.fileDbDao = labelDao;
    }

    @Override
    public void fileReading(FileDb fileDb) {

        ApiResult apiResult = new ApiResult();

        try {
            apiResult = apiResult(fileDb.getPath(), fileDb.getModel());
            //api 결과 값 받아오기
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
            fileDb.setSttResult(apiResult.getResult());
            fileDb.setSttCode(apiResult.getCode());
            fileDb.setLanguageKr(apiResult.getLanguageKr());
            fileDb.setLanguageEn(apiResult.getLanguageEn());

            fileDbDao.insertDB(fileDb);
            fileDbDao.updateId();
            fileDbDao.insertFileDB(fileDb.getFileName());
    }

    public ApiResult apiResult(String path, String model) throws IOException {

        ApiResult apiResult = new ApiResult();

        URL url = new URL("http://127.0.0.1:15000/intra");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoInput(true);
        con.setDoOutput(true);

        JSONObject obj = new JSONObject();
        obj.put("path", path);
        obj.put("model", model);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        bw.write(obj.toString());
        bw.flush(); // 버퍼에 담긴 데이터 전달
        bw.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line = null;

        while ((line = br.readLine()) != null) { // 읽을 수 있을 때 까지 반복
            sb.append(line);
        }

        JSONObject jsonObj;

        try {
            jsonObj = (JSONObject) new JSONParser().parse(sb.toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        apiResult.setResult((String)jsonObj.get("result"));
        apiResult.setCode(Long.valueOf((Long) jsonObj.get("code")).intValue());
        apiResult.setLanguageKr((Double) jsonObj.get("lang_kr"));
        apiResult.setLanguageEn((Double) jsonObj.get("lang_en"));

        if(isEmpty(jsonObj.get("result"))) {
            System.out.println("API 오류");
        }

        return apiResult;
    }
    public static boolean isEmpty(Object obj) {

        if(obj == null) return true;

        if ((obj instanceof String) && (((String)obj).trim().length() == 0)) { return true; }

        if (obj instanceof Map) { return ((Map<?, ?>) obj).isEmpty(); }

        if (obj instanceof Map) { return ((Map<?, ?>)obj).isEmpty(); }

        if (obj instanceof List) { return ((List<?>)obj).isEmpty(); }

        if (obj instanceof Object[]) { return (((Object[])obj).length == 0); }

        return false;

    }
}
