package com.qingguatang.spider.dao;

import com.qingguatang.spider.dataobject.PlayListDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author caomengxi 2018/7/26.
 * @version 1.0
 */
@Mapper
public interface PlayListDAO {

    @Insert("insert into play_list (id,title,cover,gmt_modified,gmt_created)"
            + "values("
            + "#{id},#{title},#{cover},now(),now()"
            + ")")
    public int insert(PlayListDO playListDO);

    @Select("select id,title,cover,gmt_modified as gmtModified,gmt_created as gmtCreated from play_list")
    public List<PlayListDO> selectAll();

    @Select("select id,title,cover,gmt_modified as gmtModified,gmt_created as gmtCreated from play_list"
            +" where id = #{id}")
    public PlayListDO get(@Param("id") String id);
}
