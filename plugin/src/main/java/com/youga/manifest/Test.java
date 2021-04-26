package com.youga.manifest;

import com.youga.apk.InputParam;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class Test {


    public static void main(String[] args) {
        try {
            testModifyPackSign();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testModifyPackSign() throws Exception {

        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream(new File("D:\\StudioProjects\\PackApplication\\local.properties"));
        properties.load(inputStream);
        String storeFilePath = properties.getProperty("storeFile");
        String keyAlias = properties.getProperty("keyAlias");
        String keyPassword = properties.getProperty("keyPassword");
        String storePassword = properties.getProperty("storePassword");


        String parentDir = "D:\\StudioProjects\\PackApplication\\app\\build\\outputs\\apk\\MirrorDailyYoga\\debug";

        File originApk = new File(parentDir + "\\app-Mirror-BaiDu-debug_7zip_aligned_unsigned.apk");
        String originChannelName = "BaiDu";
        File apkAssembleDir = new File(parentDir + "\\productFlavorsBuildApk");


        Map<String, Object> manifestPlaceholders = new HashMap<>();
        manifestPlaceholders.put("CHANNEL_NAME", "BaiDu");
        manifestPlaceholders.put("CHANNEL_ID", "100003");

        InputParam.Builder builder = new InputParam.Builder()
                .setOriginApk(originApk)
                .setSignFile(new File(storeFilePath))
                .setStorePassword(storePassword)
                .setKeyAlias(keyAlias)
                .setKeyPassword(keyPassword);

        Main.gradleRun(builder.create());
    }
}
