package com.qingguatang.spider.control;

import com.qingguatang.spider.dao.DemoDAO;
import com.qingguatang.spider.dataobject.DemoDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

/**
* @author      caomengxi
* @date        2018/7/24 15:20
*/
@Controller
public class DemoControl {

    @Autowired
    private DemoDAO demoDAO;

    @RequestMapping(value = "/demo/queryAll")
    @ResponseBody
    public List<DemoDO> queryAll(){
        return demoDAO.selectAll();
    }

    @RequestMapping(value = "/demo/insert")
    @ResponseBody
    public DemoDO insert(DemoDO demoDO){
        demoDO.setId(UUID.randomUUID().toString().replaceAll("-",""));
        demoDAO.insert(demoDO);
        return demoDO;
    }
}
