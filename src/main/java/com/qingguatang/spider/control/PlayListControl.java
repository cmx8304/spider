package com.qingguatang.spider.control;

import com.qingguatang.spider.dao.PlayListDAO;
import com.qingguatang.spider.dao.PlayListSongDAO;
import com.qingguatang.spider.dao.SongDAO;
import com.qingguatang.spider.dataobject.PlayListDO;
import com.qingguatang.spider.dataobject.PlayListSongDO;
import com.qingguatang.spider.dataobject.SongDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

/**
 * @author chenchen 2018/7/26.
 * @version 1.0
 */
@Controller
public class PlayListControl {

    @Autowired
    private PlayListDAO playListDAO;

    @Autowired
    private PlayListSongDAO PlayListSongDAO;

    @Autowired
    private SongDAO songDAO;


    @RequestMapping(value="/playlist/query")
    @ResponseBody
    public List<PlayListDO> query(){
        return  playListDAO.selectAll();
    }

    @RequestMapping(value="/playlist/add")
    @ResponseBody
    public boolean add(PlayListDO playListDO){
        return  playListDAO.insert(playListDO) > 0;
    }

    @RequestMapping(value="/playlist/addsong")
    @ResponseBody
    public boolean addSong(
            @RequestParam("playListId") String playListId,SongDO songDO){

        songDAO.insert(songDO);

        PlayListSongDO playListSongDO = new PlayListSongDO();
        playListSongDO.setId(UUID.randomUUID().toString().replaceAll("-",""));
        playListSongDO.setPlayListId(playListId);
        playListSongDO.setSongId(songDO.getId());

        return PlayListSongDAO.insert(playListSongDO)>0;
    }

    @RequestMapping(value="/playlist/querysongs")
    @ResponseBody
    public List<SongDO> querySongs(
            @RequestParam("playListId") String playListId){
        return  songDAO.selectByPlayListId(playListId);
    }
}
