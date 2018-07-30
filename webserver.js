"use strict";
//上一行是JavaScript的严格模式

//JavaScript定义变量
var port,server,service,
    system = require('system');
//引入文件fs文件系统的api
var fs = require('fs');
//得到./data的完整路径
var root = fs.absolute("./data");
//不存在创建root(即data)文件夹
fs.makeDirectory(root);
//讲当前工作目录换成root文件夹
fs.changeWorkingDirectory(root);
console.log('workingDirectory:'+ root);

//创建页面实例，可以通过page打开、关闭页面等
var page = require('webpage').create();

//读取命令行参数，应有2个参数，一个是webserver.js,另一个是端口号
if(system.args.length !== 2){
    console.log("Usage: serverkeepalive.js <portnumber>");
    //退出phantom
    phantom.exit(1);
}else{
    //端口号
    port = system.args[1];
    //创建一个webserver
    server = require('webserver').create();

    /**
     * 创建一个监听的端口，创建一个http的server出去
     * port  端口
     * function   回调函数，接收所有请求并响应请求信息
     */
    service = server.listen(port,function (request,response) {
        //请求url，从post传入一个变量url
        var requestUrl = request.post.url;
        response.headers = {
            'Cache': 'no-cache',
            'Content-Type': 'text/html;charset=utf-8'
        };
        if(requestUrl){
            page.open(requestUrl,function (status) {
                if(status !== 'success'){
                    console.log('Fail to load the address');
                    return;
                }

                //当前页面嵌入到Frame中
                page.switchToFrame(0);
                //page页面结束后，includeJs加载自己的js文件，并且可以执行一些回调函数
                page.includeJs(jq, function () {
                    //回调函数用于去html当中执行一句话
                    page.evaluate(playListsCallback);
                });

                handlerPlayListDetail();

                response.write('success');
                response.close();
            })
        }else{
            var body = 'no spider url';
            response.write(body);
            response.close();
        }
    })

    if(service){
        console.log('web server running on port' + port);
    }else{
        console.log('Error: Could not create web server listening on port' + port);
        phantom.exit();
    }
}

var jq = "https://cdn.bootcss.com/jquery/3.3.1/jquery.min.js";

var playLists = [];

//监听页面错误信息
page.onError = function (message) {
    if(message.indexOf('__data__') != -1){
        //获取__data__后的值
        var data = message.substr(8);
        console.log("data的值："+data);
        //parse，字符串转化为json
        var jsonData = JSON.parse(data);
        if(jsonData.dataType === 'playLists'){
            for(var i=0;i<jsonData.playLists.length;i++){
                playLists.push(jsonData.playLists[i]);
            }
        }else if(jsonData.dataType === 'playListDetail'){
            var playListDetailFile = './' + jsonData.id + '.json';
            if(!fs.exists(playListDetailFile)){
                fs.write(playListDetailFile,data,'w');
            }
        }
    }
    else {
        console.log("message--->>" + message);
    }
}

/**
 * 歌单页回调
 */
function playListsCallback() {
    var items = [];
    //遍历集合
    jQuery('#m-pl-container li').each(function () {
        var target = jQuery(this);
        //获取每个专辑的url
        var href = 'http://music.163.com/#' + target.find('.u-cover a').attr('href');
        //
        var item = {
            href : href,
            id : target.find('.icon-play').data('res-id')
        };
        items.push(item);
    });

    var playLists = {
        dataType : 'playLists',
        playLists : items
    };
    //stringify,json对象转换为字符串，4位保持美观
    console.error('__data__'+JSON.stringify(playLists,undefined,4));
}

/**
 * 详情页回调
 */
function detailCallback() {
    var playList = {
        dataType: 'playListDetail',
        title:jQuery('.tit h2').text(),
        id:jQuery('#content-operation').data('rid'),
        cover:jQuery('.u-cover img').attr('src'),
        desc:jQuery('#album-desc-dot').html(),
        songs:[]
    };

    jQuery('.m-table tbody tr').each(function () {
        var song = {
            id: jQuery(this).find('.hd span').data('res-id'),
            name:jQuery(this).find('b').attr('title'),
            duration:jQuery(this).find('.u-dur').text(),
            singer:jQuery(this).find('.text').attr('title'),
            albumName:jQuery(this).find('.text a').attr('title')
        };
        playList.songs.push(song);
    });

    console.error('__data__' + JSON.stringify(playList,undefined,4));
}

/**
 * 处理歌单详情页面
 */
function handlerPlayListDetail() {
    //回调函数、延时时间
    setTimeout(invokeDetail,0);
}

/**
 * 执行详情页爬虫请求
 */
function invokeDetail() {
    //无数据一直执行
    if(playLists.length === 0){
        handlerPlayListDetail();
        return;
    }
    //第一个歌单专辑链接
    var playListHref = playLists[0].href;
    //歌单文件名
    var playListDetailFile = './' + playLists[0].id + '.json';
    //删除第一条数据，爬虫是爬一条删一条，不会重复执行
    playLists = playLists.slice(1);

    //判断是否存在，不会重复爬虫
    if(fs.exists(playListDetailFile)){
        handlerPlayListDetail();
        return;
    }

    page.open(playListHref,function (status) {
        if(status !== 'success'){
            console.log('Fail to load the address',page.url);
            //失败则push回去重新爬虫，重复几次该操作
            playLists.push(page.url);
            handlerPlayListDetail();
            return;
        }
        console.log('Success to load the address',page.url);

        page.includeJs(jq,function () {
            page.evaluate(detailCallback);
            handlerPlayListDetail();
        });
    });
}


