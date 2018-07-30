package com.qingguatang.spider.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingguatang.spider.service.DownloadService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * @author cmx 2018/7/30.
 * @version 1.0
 */
public class DownloadServiceImpl implements DownloadService {

    private static final Logger logger = LoggerFactory.getLogger(DownloadServiceImpl.class);

    private static OkHttpClient okHttpClient = new OkHttpClient();
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static Executor executor = Executors.newFixedThreadPool(10);

    @Scheduled(initialDelay = 15000, fixedRate =  30000)
    @PostConstruct
    public void init(){
        File root = new File("data","music");
        if(!root.exists()){
            return;
        }

        for (File file : root.listFiles()){
            try {
                handlerDownload(file);
            }catch (Exception e){
                logger.error("the music file:" + file.getName() + "download error", e);
            }
        }
    }

    private void handlerDownload(File file) throws IOException{
        //使用json解析
        Map map = objectMapper.readValue(file,Map.class);
        Integer id = (Integer)map.get("id");
        String url = (String) map.get("url");
        String fileName = id + ".mp3";

        if(StringUtils.isBlank(url)){
            return;
        }

        File mp3 = new File(getRoot(),fileName);
        if(mp3.exists()){
            return;
        }

        executor.execute(new Runnable() {
            @Override
            public void run() {
                download(fileName,url);
            }
        });
    }
    @Override
    public void download(String fileName, String url) {

        Request request = new Request().Builder().url(url).build();
    }
}
