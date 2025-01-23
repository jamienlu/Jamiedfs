package io.github.jamielu.jamiedfs;

import io.github.jamielu.jamiedfs.conf.DfsConfigProperties;
import io.github.jamielu.jamiedfs.core.HttpSyncer;
import io.github.jamielu.jamiedfs.core.MqSyncer;
import io.github.jamielu.jamiedfs.meta.FileMeta;
import io.github.jamielu.jamiedfs.uitls.FileUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author jamieLu
 * @create 2025-01-22
 */
@RestController
@Slf4j
public class FileController {
    @Autowired
    DfsConfigProperties configProperties;

    @Autowired
    HttpSyncer httpSyncer;

    @Autowired
    MqSyncer mqSyncer;

    @SneakyThrows
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        boolean needSync = false;
        // 请求头标识是同步文件还是上传文件
        String filename = request.getHeader(HttpSyncer.XFILENAME);
        String originalFilename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) { // 如果这个为空则是正常上传
            filename = FileUtils.getUUIDFile(originalFilename);
            needSync = true;
        } else {
            // 主从同步文件
            String xor = request.getHeader(HttpSyncer.XORIGFILENAME);
            if (xor != null && !xor.isEmpty()) {
                originalFilename = xor;
            }
        }
        // 写文件
        File dest = getFile(FileUtils.getSubdir(filename), filename);
        file.transferTo(dest);
        // 处理meta
       FileMeta meta = FileMeta.builder().sourcePath(dest.getAbsolutePath())
            .name(filename)
            .originalFilename(originalFilename)
            .size(file.getSize())
            .downloadUrl(configProperties.getDownloadUrl())
            .build();
        if (configProperties.isAutoMd5()) {
            meta.getTags().put("md5", DigestUtils.md5DigestAsHex(new FileInputStream(dest)));
        }
        // 存放到本地文件
        FileUtils.writeMeta(new File(dest.getAbsolutePath() + ".meta"), meta);
        // 同步到集群服务器
        if (needSync) {
            // 同步 + 异步
            if (configProperties.isSyncBackup()) {
                try {
                    httpSyncer.sync(meta);
                } catch (Exception exception) {
                   log.error("同步失败", exception);
                    // 同步失败转异步处理
                    mqSyncer.sync(meta);
                }
            } else {
                mqSyncer.sync(meta);
            }
        }
        return filename;
    }

    private File getFile(String subdir, String filename) {
        return new File(configProperties.getUploadPath() + "/" + subdir + "/" + filename);
    }

    @SneakyThrows
    @RequestMapping("/download")
    public void download(String name, HttpServletResponse response) {
        log.info("download name = " + name);
        File file = getFile(FileUtils.getSubdir(name),name);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(FileUtils.getMimeType(name));
        response.setHeader("Content-Length", String.valueOf(file.length()));
        FileUtils.output(file, response.getOutputStream());
    }

    @SneakyThrows
    @RequestMapping("/meta")
    public String meta(String name) {
        return FileUtils.readString(getFile(FileUtils.getSubdir(name),name));
    }

}
