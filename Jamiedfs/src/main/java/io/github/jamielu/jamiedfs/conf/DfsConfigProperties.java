package io.github.jamielu.jamiedfs.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jamieLu
 * @create 2024-07-23
 */
@ConfigurationProperties(prefix = "dfs")
@Data
public class DfsConfigProperties {
    private String uploadPath;
    private String downloadUrl;
    private String backupUrl;
    private String group;
    private boolean autoMd5;
    private boolean syncBackup;
}
