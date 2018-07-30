"use strict";

//采用文件系统
var fs = require('fs');
//创建music文件
var root = fs.absolute("./data/music");
fs.makeDirectory(root);
console.log('workingDirectory:' + root);

//创建page实例
var page = require('webpage').create();

//缓存歌单文件名
var playListFileName = [];

page.open('https://music.163.com/#/song?id=35847388',function (status) {
    if(status !== 'success'){
        console.log('Fail to load' + page.url);
        return;
    }
    console.log('Success to load' + page.url);

    eachSongId(0);
});

page.onError = function (message) {
    //__getURL__存在
    if(message.indexOf('__getURL__') != -1){
        var data = message.substring(10);
        var json = JSON.parse(data);
        var musicDataFile = root + fs.separator + json.id + ".json";
        fs.write(musicDataFile,data,'w');
    }
};

/**
 * 循环监听歌曲文件
 * @param time
 */
function eachSongId(time) {
    setTimeout(readPlayListFile,time);
    eachSongId(10000);
}

/**
 * 读取文件
 */
function readPlayListFile() {
    var dataPath = fs.absolute('./data');
    if(!fs.exists(dataPath)){
        return;
    }
    //获取data下的json文件
    var playLists = fs.list(dataPath);

    for(var x = 0; x < playLists.length; x++){
        //得到歌单的文件路径
        var file = dataPath + fs.separator + playLists[x];
        //windows和mac的分隔符不一样，同一调用函数
        if(playListFileName.indexOf(file) != -1 || !fs.isFile(file)){
            continue;
        }
        var content = fs.read(file);
        var json = JSON.parse(content);
        var songIds = [];
        for (var i = 0; i< json.songs.length; i++){
            var songId = json.songs[i].id;
            var musicDataFile = root + fs.separator + songId + ".json";

            if(!fs.exists(musicDataFile)){
                songIds.push(songId);
            }
        }
        //执行歌曲爬虫
        if(songIds.length > 0){
            console.log(JSON.stringify(songIds));

            var evalJS = getUrl(songIds);

            //异步打开上下文
            page.evaluateAsync(evalJS);
            playListFileName.push(file);
        }

    }
}

/**
 * 获取歌曲的URL回调函数字符串
 * @param ids
 * @return {String}
 */
function getUrl(ids) {
    var jsonIds = JSON.stringify(ids);
    var result = function () {
        //得到函数
        var root = window.NEJ.P("nej.j");
        var server;
        //代表object的循环
        for (var n in root){
            //root[n]代表n个的value
            if(root[n].toString().indexOf('encText') != -1){
                server = root[n];
            }
        }
        server("/api/song/enhance/player/url",{
            type : "json",
            query : {
                ids:jsonIds,
                br:128000
            },
            onload:function (data) {

                //循环遍历每个歌曲的详细数据，data.data是歌曲详细数据
                for (var i = 0; i < data.data.length; i++){
                    var item = {
                        id: data.data[i].id,
                        url: data.data[i].url
                    };
                    console.error("__getURL__" + JSON.stringify(item));
                }
            }
        })
    };
    return result.toString().replace('jsonIds',jsonIds);
}