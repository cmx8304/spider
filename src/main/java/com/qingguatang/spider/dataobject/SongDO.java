package com.qingguatang.spider.dataobject;

import java.util.Date;

/**
 * @author cmx 2018/7/26.
 * @version 1.0
 */
public class SongDO {
    /**
     * id
     */
    private String id;
    private String name;
    private String singer;
    private String duration;
    private String url;
    /**
     * 修改日期
     */
    private Date gmtModified;
    /**
     * 创建日期
     */
    private Date gmtCreated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }
}
