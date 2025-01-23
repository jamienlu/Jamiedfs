package io.github.jamielu.jamiedfs.core;

import com.alibaba.fastjson.JSONObject;
import io.github.jamielu.jamiedfs.meta.FileMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

/**
 * @author jamieLu
 * @create 2024-07-23
 */
@Component
@Slf4j
public class HttpSyncer implements Syncer {
    public final static String XFILENAME = "X-Filename";
    public final static String XORIGFILENAME = "X-Orig-Filename";
    @Value("${dfs.backupUrl}")
    private String backendUrl;

    @Override
    public void sync(FileMeta meta) {
        log.info("### HttpSyncer sync meta: " + JSONObject.toJSON(meta));
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add(XFILENAME, meta.getName());
        headers.add(XORIGFILENAME, meta.getOriginalFilename());

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new FileSystemResource(new File(meta.getSourcePath())));

        HttpEntity<MultiValueMap<String, HttpEntity<?>>> httpEntity
                = new HttpEntity<>(builder.build(), headers);

        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(backendUrl, httpEntity, String.class);
        String result = stringResponseEntity.getBody();
        log.info("###sync result = " + result);
    }
}
