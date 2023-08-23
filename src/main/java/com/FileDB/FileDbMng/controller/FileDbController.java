package com.FileDB.FileDbMng.controller;

import com.FileDB.FileDbMng.service.FileDbService;
import com.FileDB.FileDbMng.util.TimeFileMng;
import com.FileDB.FileDbMng.vo.FileDb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Slf4j
@RestController
public class FileDbController {

    private FileDbService fileDbService;

    public FileDbController(FileDbService labelService) {
        this.fileDbService = labelService;
    }

    @Async
    @Scheduled(fixedDelay = 2000) //2초 마다 실행
    public void jejuReading(){
        String local = TimeFileMng.LocalRotation(); //지역 파일
        scheduleReading(local);
    }
//    @Async
//    @Scheduled(fixedDelay = 10000)
//    public void ulsanReading(){
//        scheduleReading("ulsan");
//    }
//    @Async
//    @Scheduled(fixedDelay = 10000)
//    public void jindoReading(){
//        scheduleReading("jindo");
//    }
//    @Async
//    @Scheduled(fixedDelay = 10000)
//    public void mokpoReading(){
//        scheduleReading("mokpo");
//    }
//    @Async
//    @Scheduled(fixedDelay = 10000)
//    public void yeosuReading(){
//        scheduleReading("yeosu");
//    }

    public void scheduleReading(String local) {

        LocalDate now = LocalDate.now();
        DateTimeFormatter nowfm = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String today = now.format(nowfm);

        String checkFolder = "";
        String model = "";

        if (local.equals("jeju")) {
            checkFolder = "jejuCheck.txt";
            model = "model_jeju";
        } else if (local.equals("ulsan")) {
            checkFolder = "ulsanCheck.txt";
            model = "model_ulsan";
        } else if (local.equals("jindo")) {
            checkFolder = "jindoCheck.txt";
            model = "model_kor";
        } else if (local.equals("mokpo")) {
            checkFolder = "mokpoCheck.txt";
            model = "model_kor";
        } else if (local.equals("yeosu")){
            checkFolder = "yeosuCheck.txt";
            model = "model_kor";
        } else {
            model= "model_kor";
        }

        //지역 시간 텍스트 파일 저장 경로
        File txt = new File("/home/user/Documents/voiceFileMng/voiceFileMng/src/main/java/com/FileDB/FileDbMng/util/"+checkFolder);

        String Otime = null;

        try {
            Otime = TimeFileMng.FileRead(txt);
            //시간 파일 읽기
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String filePath = "/media/user/vhf/uploads/"+local+"/"+today+"/";
//        String filePath = "C:\\Users\\foit\\SampleFile\\";
       File pa = new File(filePath);

        if (pa.exists()) {

            File[] list = pa.listFiles();

            list = sortFileList(list,COMPARETYPE_DATE);
            //파일 시간 순서 정렬
            Date date1 = TimeFileMng.dateFormat(Otime);
            //날짜 시간 포맷

            List<File> serviceFile = new ArrayList<File>();

            for(int i = 0; i < list.length; i++){

                Date date2 = TimeFileMng.NameFormat(list[i].getName());
                //날짜 데이터 타입 변환

                if(date1.before(date2)){
                    serviceFile.add(list[i]);
                }else{

                }
            }
            if(serviceFile.size() > 0){

                String LastTime = String.valueOf(TimeFileMng.NameFormat(serviceFile.get(serviceFile.size()-1).getName()));
                //시간 저장

                try {
                    TimeFileMng.FileWrite(LastTime,txt);
                    //시간 파일 새 시간 덮어쓰기
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                for(int i = 0 ; i < serviceFile.size();i++){

                    Date date2 = TimeFileMng.NameFormat(serviceFile.get(i).getName());
                    //시간 타입 변환

                    FileDb fileDb = new FileDb();

                    fileDb.setPath(filePath + serviceFile.get(i).getName());
                    fileDb.setModel(model);
                    fileDb.setLocal(local);
                    fileDb.setRecordTime(TimeFileMng.kstFormat(String.valueOf(date2)));
                    fileDb.setFileName(serviceFile.get(i).getName());

                    fileDbService.fileReading(fileDb);
                }
            }else{
                System.out.println(local+"폴더 : 생성 된 파일 없음");
            }
        } else {
            System.out.println(local+"폴더 : 폴더 아직 생성 X");
        }
    }

    public int COMPARETYPE_NAME = 0;
    public int COMPARETYPE_DATE = 1;
    
    public File[] sortFileList(File[] files, final int compareType)
    {
        Arrays.sort(files,
                new Comparator<Object>()
                {
                    @Override
                    public int compare(Object object1, Object object2) {

                        String s1 = "";
                        String s2 = "";

                        if(compareType == COMPARETYPE_NAME){
                            s1 = ((File)object1).getName();
                            s2 = ((File)object2).getName();
                        }
                        else if(compareType == COMPARETYPE_DATE){
                            s1 = ((File)object1).lastModified()+"";
                            s2 = ((File)object2).lastModified()+"";
                        }
                        return s1.compareTo(s2);
                    }
                });
        return files;
    }



//    long beforeTime = System.currentTimeMillis(); //코드 실행 전에 시간 받아오기
////실험할 코드 추가
//    long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
//    long secDiffTime = (afterTime - beforeTime)/1000; //두 시간에 차 계산
//    System.out.println("시간차이(m) : "+secDiffTime);

//    @Component
//    public class StartApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
//
//        @Override
//        public void onApplicationEvent(ContextRefreshedEvent event) {
//            LocalDate now = LocalDate.now();
//            DateTimeFormatter yearfm = DateTimeFormatter.ofPattern("yy");
//            String year = now.format(yearfm);
//            DateTimeFormatter monthfm = DateTimeFormatter.ofPattern("MM");
//            String month = now.format(monthfm);
//            DateTimeFormatter dayfm = DateTimeFormatter.ofPattern("dd");
//            String day = now.format(dayfm);
//
//            String dayfr = year + month + day;
//
//            try {
//                watch(dayfr,"jeju");
//                watch(dayfr,"ulsan");
//                watch(dayfr,"jindo");
//                watch(dayfr,"mokpo");
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//    public void watch(String day,String local) throws IOException {
//        Path path = null;
//        WatchService watchService = FileSystems.getDefault().newWatchService();
//
//        if(local == "jeju"){
//            path = FileSystems.getDefault().getPath("/media/user/data_disk/0. 실시간 수신/제주항VTS_ch12_16k/" + day + "/");
//        }else if(local == "ulsan"){
//            path = FileSystems.getDefault().getPath("/media/user/data_disk/0. 실시간 수신/울산VTS_ch14_16k/" + day + "/");
//        }else if(local == "jindo"){
//            path = FileSystems.getDefault().getPath("/media/user/data_disk/0. 실시간 수신/진도연안VTS_ch67_16k/" + day + "/");
//        }else if(local == "mokpo"){
//            path = FileSystems.getDefault().getPath("/media/user/data_disk/0. 실시간 수신/목포항VTS_ch12_16k/" + day + "/");
//        }
//
////        Path path = Paths.get("C:\\Users\\foit\\SampleFile\\");
//
//        path.register(watchService,
//                StandardWatchEventKinds.ENTRY_CREATE
//        );
//
//        while (true) {
//            // Service 실행
//            WatchKey key = null;
//            try {
//                key = watchService.take();
//            } catch (InterruptedException e) {
////                throw new RuntimeException(e);
//            }
//
//            List<WatchEvent<?>> watchEventList = key.pollEvents();
//            // List에서 WatchEvent를 하나씩 꺼내서 Event의 종류와 path 처리
//            for (WatchEvent<?> event : watchEventList) {
//                // Event 종류
//                WatchEvent.Kind<?> kind = event.kind();
//                // 감지된 file path
//                Path filePath = (Path) event.context();
//
//                if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
//                    // Create됬을 때 실행
//
//                    System.out.println("Create : " + filePath.getFileName());
//
//                }
//            }
//            if (!key.reset())
//                break;
//        }
//        watchService.close();
//    }

}
