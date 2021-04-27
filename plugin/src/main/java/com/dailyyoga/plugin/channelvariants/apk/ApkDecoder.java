package com.dailyyoga.plugin.channelvariants.apk;

import com.dailyyoga.plugin.channelvariants.util.FileOperation;

import java.io.File;
import java.util.HashMap;

/**
 * @author: YougaKingWu@gmail.com
 * @created on: 2019/7/19 17:23
 * @description:
 */
public class ApkDecoder {

    private static final String DIR_RESOURCES = "resources";
    private static final String FILE_ANDROID_MANIFEST = "AndroidManifest.xml";

    private final File originApk;
    private File resourcesDir;
    private HashMap<String, Integer> compressData;

    public ApkDecoder(File originApk) {
        this.originApk = originApk;
    }

    public void decode() throws Exception {
        ensureFilePath();
    }

    private void ensureFilePath() throws Exception {
        File parentFile = originApk.getParentFile();
        resourcesDir = new File(parentFile, DIR_RESOURCES);
        System.out.printf("unziping apk to %s\n", resourcesDir.getAbsolutePath());
        compressData = FileOperation.unZipAPk(originApk, resourcesDir);
    }

    public File getResourcesDir() {
        return resourcesDir;
    }

    public HashMap<String, Integer> getCompressData() {
        return compressData;
    }

    public File getAndroidManifestFile() {
        return new File(resourcesDir, FILE_ANDROID_MANIFEST);
    }
}
