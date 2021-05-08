package com.dailyyoga.plugin.channelvariants;

import com.dailyyoga.plugin.channelvariants.apk.ApkDecoder;
import com.dailyyoga.plugin.channelvariants.apk.Channel;
import com.dailyyoga.plugin.channelvariants.apk.InputParam;
import com.dailyyoga.plugin.channelvariants.apk.ResourceApkBuilder;
import com.dailyyoga.plugin.channelvariants.util.FileOperation;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {

    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

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
            builder.buildApkWithV2sign(decoder.getCompressData(), 19);
        }
    }

    private String generalApkName(File originApk, Channel originChannel, Channel channel) {
        String originApkName = originApk.getName();
        if (originChannel == null) {
            //tencent_100004_release_8.10.0.0_20210427.apk
            StringBuilder builder = new StringBuilder();
            for (String key : channel.manifestPlaceholders.keySet()) {
                builder.append("_")
                        .append(channel.manifestPlaceholders.get(key));
            }
            builder.append("_")
                    .append(dateFormat.format(new Date()))
                    .append(".apk");
            builder.deleteCharAt(builder.indexOf("_"));

            return builder.toString();
        } else {
            originApkName = originApkName.replaceAll("(?i)" + originChannel.name, channel.name);
            for (String key : channel.manifestPlaceholders.keySet()) {
                String value = originChannel.manifestPlaceholders.get(key);
                if (value == null) continue;
                originApkName = originApkName.replaceAll(value, channel.manifestPlaceholders.get(key));
            }
            return originApkName;
        }
    }

    private void deleteResourcesDir(ApkDecoder decoder) {
        FileOperation.deleteDir(decoder.getResourcesDir());
    }
}
