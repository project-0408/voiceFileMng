package com.FileDB.FileDbMng.util;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeFileMng {

    public static String LocalRotation(){

        File local = new File("/home/user/Documents/voiceFileMng/voiceFileMng/src/main/java/com/FileDB/FileDbMng/util/local.txt");
        //지역 로테이션 파일 경로

        BufferedReader reader = null;
        BufferedWriter writer = null;

        String line;
        String nextLine;

        try {
            reader = new BufferedReader(new FileReader(local));
            line = reader.readLine();

            if (line.equals("jeju")){
                nextLine = "ulsan";
            }else if(line.equals("ulsan")){
                nextLine = "jindo";
            }else if(line.equals("jindo")){
                nextLine = "mokpo";
            }else if(line.equals("mokpo")){
                nextLine = "yeosu";
            }else if(line.equals("yeosu")){
                nextLine = "jeju";
            }else{
                nextLine = "jeju";
            }
            //지역 별 다음 로테이션 폴더명 변경

            writer = new BufferedWriter(new FileWriter(local,false));
            writer.write(nextLine);
            writer.flush();
            writer.close();
            reader.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return line;
    }

    public static void FileWrite(String text , File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file,false));
        writer.write(text);
        writer.flush();
        writer.close();
    }

    public static String FileRead(File txt) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(txt));
        String line = reader.readLine();
        reader.close();
        return line;
    }

    public static Date dateFormat(String Otime){

        DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

        Date date = null;

        try {
            date = dateFormat.parse(Otime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return date;
    }
    public static String kstFormat(String time){
        String strDate = null;

        SimpleDateFormat recvSimpleFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        // 여기에 원하는 포맷을 넣어주면 된다
        SimpleDateFormat tranSimpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        try {
            Date data = recvSimpleFormat.parse(time);
            strDate = tranSimpleFormat.format(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strDate;
    }

    public static Date NameFormat(String name) {

        String nameFormat[] = name.split("_");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String nameform = nameFormat[0].substring(0,4)
                + "-" + nameFormat[0].substring(4,6)
                + "-" + nameFormat[0].substring(6,8)
                + " " + nameFormat[1].substring(0,2)
                + ":" + nameFormat[1].substring(2,4)
                + ":" + nameFormat[1].substring(4,6);

        Date date = null;

        try {
            date = format.parse(nameform);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return date;
    }

    public static File getLastModified(String directoryFilePath)
    {
        File directory = new File(directoryFilePath);
        File[] files = directory.listFiles(File::isFile);
        long lastModifiedTime = Long.MIN_VALUE;
        File chosenFile = null;

        if (files != null)
        {
            for (File file : files)
            {
                if (file.lastModified() > lastModifiedTime)
                {
                    chosenFile = file;
                    lastModifiedTime = file.lastModified();
                }
            }
        }
        return chosenFile;
    }
}
