package com.qingguatang.spider.dao;

import com.qingguatang.spider.dataobject.SongDO;
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
public interface SongDAO {
    @Insert("insert into song (id,name,singer,duration,url,gmt_modified,gmt_created)"
            + "values("
            + "#{id},#{name},#{singer},#{duration},#{url},now(),now()"
            + ")")
    public int insert(SongDO songDO);


    @Select("select a.id,a.name,a.singer,a.duration,a.url,a.gmt_modified as gmtModified,a.gmt_created as gmtCreated "+
            " from song a,play_list_song b where a.id = b.song_id and b.play_list_id = #{id}")
    public List<SongDO> selectByPlayListId(@Param("id") String id);

    @Select("select id,name,singer,duration,url,gmt_modified as gmtModified,gmt_created as gmtCreated from song"
            +" where id = #{id}")
    public SongDO selectById(@Param("id") String id);
}
