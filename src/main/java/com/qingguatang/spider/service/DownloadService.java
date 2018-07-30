package com.qingguatang.spider.service;

/**
 * @author cmx 2018/7/30.
 * @version 1.0
 * @date 2018/7/30 17:39
 */
public interface DownloadService {

    /**
     * 下载文件
     * @param fileName
     * @param url
     */
    public void download(String fileName,String url);
}
