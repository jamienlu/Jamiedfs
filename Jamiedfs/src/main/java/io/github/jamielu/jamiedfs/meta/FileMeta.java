package io.github.jamielu.jamiedfs.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jamieLu
 * @create 2024-07-23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileMeta {
    private String sourcePath;
    private String originalFilename;
    private String name;
    private long size;
    private String downloadUrl;
    @Builder.Default
    private Map<String, String> tags = new HashMap<>();

    public FileMeta(String filename, String originalFilename, long size, String downloadUrl) {
        this.name = filename;
        this.originalFilename = originalFilename;
        this.size = size;
        this.downloadUrl = downloadUrl;
    }
}
