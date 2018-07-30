package com.qingguatang.spider.dataobject;

import java.util.Date;

/**
* @author      caomengxi
* @date        2018/7/24 15:09
*/
public class DemoDO {

    /**
     * id
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 修改时间
     */
    private Date gmtModified;
    /**
     * 创建时间
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
