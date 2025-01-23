package io.github.jamielu.jamiedfs.core;

import io.github.jamielu.jamiedfs.meta.FileMeta;

import java.io.File;

/**
 * @author jamieLu
 * @create 2024-07-23
 */
public interface Syncer {
    void sync(FileMeta meta);
}
