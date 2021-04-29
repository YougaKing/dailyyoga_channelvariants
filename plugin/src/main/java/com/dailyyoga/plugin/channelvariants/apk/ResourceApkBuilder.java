package com.dailyyoga.plugin.channelvariants.apk;

import com.dailyyoga.plugin.channelvariants.signer.ApkSignerTool;
import com.dailyyoga.plugin.channelvariants.manifest.ChannelEditor;
import com.dailyyoga.plugin.channelvariants.manifest.decode.AXMLDoc;
import com.dailyyoga.plugin.channelvariants.util.FileOperation;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author: YougaKingWu@gmail.com
 * @created on: 2019/7/19 9:05
 * @description:
 */
public class ResourceApkBuilder {

    private final InputParam inputParam;
    private final Channel channel;
    private final String apkName;
    private final ApkDecoder decoder;

    private File unSignedApk;
    private File signedApk;

    public ResourceApkBuilder(InputParam inputParam,
                              Channel channel,
                              String apkName,
                              ApkDecoder decoder) {
        this.inputParam = inputParam;
        this.channel = channel;
        this.apkName = apkName;
        this.decoder = decoder;
    }

    public void buildApkWithV2sign(HashMap<String, Integer> compressData, int minSDKVersion) throws Exception {
        generalApkFile();
        writeChannel();
        zipUnSignApkFile(compressData);
        signApkFile(unSignedApk, signedApk, minSDKVersion);
        deleteUnSignApkFile();
    }

    private void generalApkFile() throws IOException {
        File parentFile = inputParam.originApk.getParentFile();

        String prefixName = apkName.substring(0, apkName.indexOf(".apk"));
        String unSignedApkName = prefixName + "_signed.apk";
        unSignedApk = new File(parentFile, unSignedApkName);

        File dir;
        if (inputParam.outApkDir == null) {
            dir = parentFile;
        } else {
            dir = inputParam.outApkDir;
            FileOperation.mkdirs(dir);
            FileUtils.copyFileToDirectory(inputParam.originApk, dir);
        }
        signedApk = new File(dir, apkName);
    }

    private void writeChannel() throws Exception {
        File archiveFile = decoder.getAndroidManifestFile();

        AXMLDoc doc = new AXMLDoc();
        doc.parse(new FileInputStream(archiveFile.getAbsolutePath()));

        for (String key : channel.manifestPlaceholders.keySet()) {
            ChannelEditor editor = new ChannelEditor(doc, key);
            editor.setChannel(channel.manifestPlaceholders.get(key));
            editor.commit();
        }

        FileOperation.deleteFile(archiveFile.getAbsolutePath());
        doc.build(new FileOutputStream(archiveFile));
    }

    private void zipUnSignApkFile(HashMap<String, Integer> compressData) throws IOException, InterruptedException {

        File resourcesDir = decoder.getResourcesDir();

        File[] unZipFiles = resourcesDir.listFiles();
        if (unZipFiles == null) return;
        List<File> collectFiles = new ArrayList<>(Arrays.asList(unZipFiles));

        FileOperation.zipFiles(collectFiles, resourcesDir, unSignedApk, compressData);

        if (!unSignedApk.exists()) {
            throw new IOException(String.format("can not found the unsign apk file path=%s", unSignedApk.getAbsolutePath()));
        }
    }

    private void signApkFile(File unSignedApk, File signedApk, int minSDKVersion) throws Exception {
        System.out.printf("signing apk: %s\n", signedApk.getName());
        signWithV2sign(unSignedApk, signedApk, minSDKVersion);
        if (!signedApk.exists()) {
            throw new IOException("Can't Generate signed APK v2. Plz check your v2sign info is correct.");
        }
    }

    private void signWithV2sign(File unSignedApk, File signedApk, int minSDKVersion) throws Exception {
        String[] params = new String[]{
                "sign",
                "--ks",
                inputParam.signFile.getAbsolutePath(),
                "--ks-pass",
                "pass:" + inputParam.storePassword,
                "--min-sdk-version",
                String.valueOf(minSDKVersion),
                "--ks-key-alias",
                inputParam.keyAlias,
                "--key-pass",
                "pass:" + inputParam.keyPassword,
                "--out",
                signedApk.getAbsolutePath(),
                unSignedApk.getAbsolutePath()
        };
        ApkSignerTool.main(params);
    }

    private void deleteUnSignApkFile() throws IOException {
        FileOperation.deleteFile(unSignedApk.getAbsolutePath());
    }
}
