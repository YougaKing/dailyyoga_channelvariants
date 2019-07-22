package com.youga.manifest;

import com.youga.apk.ApkDecoder;
import com.youga.apk.Channel;
import com.youga.apk.InputParam;
import com.youga.apk.ResourceApkBuilder;
import com.youga.util.FileOperation;

import java.io.File;

public class Main {


    private File mOutDir;

    public static void gradleRun(InputParam inputParam) {
        Main main = new Main();
        main.run(inputParam);
    }

    private void run(InputParam inputParam) {
        synchronized (Main.class) {
            try {
                ApkDecoder decoder = new ApkDecoder(inputParam.originApk);
                /* 默认使用V1签名 */
                decodeResource(inputParam.originApk, decoder);
                buildApk(decoder, inputParam);
                deleteTempFile(inputParam);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void decodeResource(File targetApk, ApkDecoder decoder) throws Exception {
        mOutDir = targetApk.getParentFile();
        decoder.setOutDir(mOutDir);
        decoder.decode();
    }

    private void buildApk(ApkDecoder decoder, InputParam inputParam) throws Exception {
        for (Channel channel : inputParam.channelList) {
            String apkName = generalApkName(inputParam, channel);
            ResourceApkBuilder builder = new ResourceApkBuilder(inputParam, channel, apkName);
            builder.setOutDir(mOutDir);
            builder.buildApkWithV2sign(decoder.getCompressData(), 14);
        }
    }

    private String generalApkName(InputParam inputParam, Channel channel) {
        String originApkName = inputParam.originApk.getName();
        for (String key : inputParam.originChannel.manifestPlaceholders.keySet()) {
            String value = capitalize(inputParam.originChannel.manifestPlaceholders.get(key));
            originApkName = originApkName.replaceAll(value, capitalize(channel.manifestPlaceholders.get(key)));
        }
        return originApkName;
    }

    private void deleteTempFile(InputParam inputParam) {
        File tempOutDir = new File(mOutDir, FileOperation.UNZIP_FILE_PATH);
        FileOperation.deleteDir(tempOutDir);
        FileOperation.deleteFile(inputParam.originApk.getAbsolutePath());
    }

    private static String capitalize(CharSequence self) {
        return self.length() == 0 ? "" : "" + Character.toUpperCase(self.charAt(0)) + self.subSequence(1, self.length());
    }
}
