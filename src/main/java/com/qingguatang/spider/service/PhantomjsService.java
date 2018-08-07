package com.qingguatang.spider.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingguatang.spider.control.PlayListControl;
import com.qingguatang.spider.dao.PlayListDAO;
import com.qingguatang.spider.dao.SongDAO;
import com.qingguatang.spider.dataobject.PlayListDO;
import com.qingguatang.spider.dataobject.SongDO;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author caomengxi 2018/7/26.
 * @version 1.0
 * 注解：@Controller用于API交互，@Component是用于内部service或者组件
 */
@Component
public class PhantomjsService {

    private static final Logger logger = LoggerFactory.getLogger(PhantomjsService.class);
    //创建两个线程池
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(6);

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PlayListDAO playListDAO;

    @Autowired
    private SongDAO songDAO;

    @Autowired
    private PlayListControl playListControl;

    /**
     * @PostConstruct用于Spring Bean初始化后先执行的方法
     */
    @PostConstruct
    public void init(){

        PlayListTask playListTask = new PlayListTask();
        threadPool.execute(playListTask);

        MusicTask musicTask = new MusicTask();
        threadPool.execute(musicTask);
    }

    class PlayListTask implements Runnable{
        @Override
        public void run(){
            //ProcessBuilder是java用于执行本地脚本的一个工具
            ProcessBuilder pb = new ProcessBuilder("phantomjs","webserver.js","8088");
            pb.redirectErrorStream(true);

            try {
                //在进程中开辟新的线程
                Process process = pb.start();
                ResultStreamHandler inputStreamHandler = new ResultStreamHandler(process.getInputStream());
                ResultStreamHandler errorStreamHandler = new ResultStreamHandler(process.getErrorStream());
                threadPool.execute(inputStreamHandler);
                threadPool.execute(errorStreamHandler);
            }catch (IOException e){
                logger.error("",e);
            }
        }
    }

    class MusicTask implements Runnable{
        @Override
        public void run(){
            //ProcessBuilder是java用于执行本地脚本的一个工具
            ProcessBuilder pb = new ProcessBuilder("phantomjs","music.js");
            pb.redirectErrorStream(true);

            try {
                //在进程中开辟新的线程
                Process process = pb.start();
                ResultStreamHandler inputStreamHandler = new ResultStreamHandler(process.getInputStream());
                ResultStreamHandler errorStreamHandler = new ResultStreamHandler(process.getErrorStream());
                threadPool.execute(inputStreamHandler);
                threadPool.execute(errorStreamHandler);
            }catch (IOException e){
                logger.error("",e);
            }
        }
    }

    @Scheduled(initialDelay = 15000,fixedRate = 30000)
    public void initData() {
        File root = new File("data");
        if (!root.exists()) {
            return;
        }

        for (File file : root.listFiles()) {
            if (file.getName().endsWith(".json")) {
                try {
                    Map map = objectMapper.readValue(file, Map.class);

                    String playListId = map.get("id").toString();
                    PlayListDO playListDO = playListDAO.get(playListId);

                    if (playListDO == null) {
                        playListDO = new PlayListDO();
                        playListDO.setId(playListId);
                        playListDO.setTitle((String) map.get("title"));
                        playListDO.setCover((String) map.get("cover"));

                        playListDAO.insert(playListDO);
                    }

                    List<Map> songs = (List<Map>) map.get("songs");
                    songs.forEach(song -> {
                        String songId = song.get("id").toString();

                        SongDO songDO = songDAO.selectById(songId);

                        if (songDO == null) {
                            songDO = new SongDO();
                            songDO.setId(songId);
                            songDO.setDuration((String) song.get("duration"));
                            songDO.setSinger((String) song.get("singer"));
                            songDO.setName((String) song.get("name"));

                            playListControl.addSong(playListId, songDO);

                        }
                    });
                } catch (IOException e) {
                    logger.error("", e);
                }

            }
        }
    }
    /**
     * stream线程类，用户输出日志
     */
    class ResultStreamHandler implements Runnable{

        private InputStream in;
        ResultStreamHandler(InputStream in){
            this.in = in;
        }
        @Override
        public void run(){
            //缓存区
            BufferedReader bufferedReader = null;
            try{
                bufferedReader = new BufferedReader(new InputStreamReader(in));
                String line = null;
                while ((line = bufferedReader.readLine()) != null){
                    logger.error("phantomjs:" + line);
                }
            }catch (Throwable t){
                logger.error("",t);
            }finally {
                try {
                    //手动关闭缓存区
                    bufferedReader.close();
                }catch (IOException e){
                    logger.error("",e);
                }
            }
        }
    }

}
