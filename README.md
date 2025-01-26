>[https://github.com/jamienlu/Jamiedfs.git](https://github.com/jamienlu/Jamiedfs.git)

# 相关功能说明

```plain
io/github/jamielu/jamiedfs/conf  配置上传和下载地址，同步方式和同步地址
io/github/jamielu/jamiedfs/core 写元数据 数据同步  http同步转发和消息队列异步同步
io/github/jamielu/jamiedfs/meta  文件元数据
io/github/jamielu/jamiedfs/FileController 文件接口
```


```plain
使用示例：
<dependency>
  <groupId>io.github.jamielu</groupId>
  <artifactId>Jamiedfs</artifactId>
  <version>0.0.1-SNAPSHOT</version>
<dependency>


yaml文件配置
dfs:
  uploadPath: ${user.home}/dfs
  syncBackup: false
  autoMd5: true
  group: dfsGroup
  backupUrl: http://localhost:8091/upload
  downloadUrl: http://localhost:8090/download


rocketmq:
  name-server: 192.168.0.100:9876
  producer:
    group: dfs-producer
    send-message-timeout: 30000


server:
  port: 8090
```


