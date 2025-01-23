package io.github.jamielu.jamiedfs.core;

import com.alibaba.fastjson.JSON;
import io.github.jamielu.jamiedfs.meta.FileMeta;
import io.github.jamielu.jamiedfs.uitls.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author jamieLu
 * @create 2024-07-23
 */
@Component
@Slf4j
public class MqSyncer implements Syncer {
    @Value("${dfs.group}")
    private String group;

    @Value("${dfs.uploadPath}")
    private String uploadPath;

    @Value("${dfs.downloadUrl}")
    private String localUrl;

    @Autowired
    RocketMQTemplate rocketMQTemplate;

    private final String TOPIC = "dfs";

    public void sync(FileMeta meta) {
        Message<String> message = MessageBuilder.withPayload(JSON.toJSONString(meta)).build();
        rocketMQTemplate.send(TOPIC, message);
        log.info("### MqSyncer send message: " + message);
    }

    @Service
    @RocketMQMessageListener(topic = TOPIC, consumerGroup = "${dfs.group}")
    public class FileMQSyncer implements RocketMQListener<MessageExt> {

        @Override
        public void onMessage(MessageExt message) {
            // 消息里拿到meta数据
            String json = new String(message.getBody());
            log.info("### receive message json = " + json);
            FileMeta meta = JSON.parseObject(json, FileMeta.class);
            String downloadUrl = meta.getDownloadUrl();
            if(downloadUrl == null || downloadUrl.isEmpty()) {
                log.info("### downloadUrl is empty.");
                return;
            }
            // 避免重复
            if(localUrl.equals(downloadUrl)) {
                log.info("### the same file server, ignore mq sync task.");
                return;
            }

            // 写meta文件
            String dir = uploadPath + "/" + meta.getName().substring(0, 2);
            File metaFile = new File(dir, meta.getName() + ".meta");
            if(metaFile.exists()) {
                log.info("### meta file exists and ignore save: " + metaFile.getAbsolutePath());
            } else {
                log.info("### meta file save: " + metaFile.getAbsolutePath());
                FileUtils.writeString(metaFile, json);
            }

            // 存文件
            File file = new File(dir, meta.getName());
            if(file.exists() && file.length() == meta.getSize()) {
                log.info("### file exists and ignore download: " + file.getAbsolutePath());
                return;
            }
            // 调用下载接口从其他服务器拉文件输出到指定文件
            String download = downloadUrl + "?name=" + file.getName();
            FileUtils.download(download, file);
        }
    }
}
