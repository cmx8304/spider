package com.qingguatang.spider.service;


import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(2);


    /**
     * @PostConstruct用于Spring Bean初始化后先执行的方法
     */
    @PostConstruct
    public void init(){
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
