package com.qingguatang.spider.control;

import okhttp3.FormBody.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * @author cmx 2018/8/3.
 * @version 1.0
 */
@Controller
public class PageControl {
    private static final Logger logger = LoggerFactory.getLogger(PageControl.class);

    private static OkHttpClient client = new OkHttpClient();

    @RequestMapping(value = "/admin")
    public String admin(){
        return "admin";
    }

    @RequestMapping(value = "/spider")
    public String spider(String url){
        if(StringUtils.isBlank(url)){
            return "redirect:admin";
        }

        Builder builder = new Builder();
        builder.add("url",url);

        Request request = new Request.Builder().url("http://localhost:8081").post(builder.build()).build();

        try {
            Response response = client.newCall(request).execute();
            String body = response.body().string();
            logger.error(body);
        }catch (IOException e){
            logger.error("",e);
        }


        return "redirect:admin";
    }
}
