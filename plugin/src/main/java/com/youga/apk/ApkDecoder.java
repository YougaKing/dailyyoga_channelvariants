package com.youga.apk;

import com.youga.util.FileOperation;

import java.io.File;
import java.util.HashMap;

/**
 * @author: YougaKingWu@gmail.com
 * @created on: 2019/7/19 17:23
 * @description:
 */
public class ApkDecoder {

    private File originApk;
    private File mOutDir;
    private HashMap<String, Integer> mCompressData;

    public ApkDecoder(File originApk) {
        this.originApk = originApk;
    }

    public void setOutDir(File outDir) {
        mOutDir = outDir;
    }

    public void decode() throws Exception {
        ensureFilePath();
    }

    private void ensureFilePath() throws Exception {
        String unZipDest = new File(mOutDir, FileOperation.UNZIP_FILE_PATH).getAbsolutePath();
        System.out.printf("unziping apk to %s\n", unZipDest);
        mCompressData = FileOperation.unZipAPk(originApk.getAbsolutePath(), unZipDest);
    }


    public HashMap<String, Integer> getCompressData() {
        return mCompressData;
    }
}
