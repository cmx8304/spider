package com.qingguatang.spider.dataobject;

import java.util.Date;

/**
 * @author chenchen 2018/7/26.
 * @version 1.0
 */
public class PlayListSongDO {
    private String id;
    private String playListId;
    private String songId;
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

    public String getPlayListId() {
        return playListId;
    }

    public void setPlayListId(String playListId) {
        this.playListId = playListId;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
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
