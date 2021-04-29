package com.dailyyoga.plugin.channelvariants;

import com.dailyyoga.plugin.channelvariants.apk.ApkDecoder;
import com.dailyyoga.plugin.channelvariants.apk.Channel;
import com.dailyyoga.plugin.channelvariants.apk.InputParam;
import com.dailyyoga.plugin.channelvariants.apk.ResourceApkBuilder;
import com.dailyyoga.plugin.channelvariants.util.FileOperation;

import java.io.File;

public class Main {

    public static void gradleRun(InputParam inputParam) {
        System.out.println(inputParam);
        Main main = new Main();
        main.run(inputParam);
    }

    private void run(InputParam inputParam) {
        synchronized (Main.class) {
            try {
                ApkDecoder decoder = new ApkDecoder(inputParam.originApk);
                decoder.decode();
                buildApk(decoder, inputParam);
                deleteResourcesDir(decoder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void buildApk(ApkDecoder decoder, InputParam inputParam) throws Exception {
        for (Channel channel : inputParam.channelList) {
            String apkName = generalApkName(inputParam.originApk, inputParam.originChannel, channel);
            ResourceApkBuilder builder = new ResourceApkBuilder(inputParam, channel, apkName, decoder);
            builder.buildApkWithV2sign(decoder.getCompressData(), 14);
        }
    }

    private String generalApkName(File originApk, String originChannel, Channel channel) {
        String originApkName = originApk.getName();
        originApkName = originApkName.replaceAll("(?i)" + originChannel, channel.name);
        for (String key : channel.manifestPlaceholders.keySet()) {
            String value = channel.manifestPlaceholders.get(key);
            originApkName = originApkName.replaceAll(value, channel.manifestPlaceholders.get(key));
        }
        return originApkName;
    }

    private void deleteResourcesDir(ApkDecoder decoder) {
        FileOperation.deleteDir(decoder.getResourcesDir());
    }
}
