package com.youga.apk;

import com.youga.apksigner.ApkSignerTool;
import com.youga.manifest.ChannelEditor;
import com.youga.manifest.decode.AXMLDoc;
import com.youga.util.FileOperation;

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

    private InputParam mInputParam;
    private Channel mChannel;
    private String mApkName;

    private File mOutDir;

    private File mUnSignedApk;
    private File mSignedApk;

    public ResourceApkBuilder(InputParam inputParam, Channel channel, String apkName) {
        mInputParam = inputParam;
        mChannel = channel;
        mApkName = apkName;
    }

    public void setOutDir(File outDir) {
        mOutDir = outDir;
    }

    public void buildApkWithV2sign(HashMap<String, Integer> compressData, int minSDKVersion) throws Exception {
        insureFileNameV2();
        writeChannel();
        generalUnsignApk(compressData);
        signApkV2(mUnSignedApk, mSignedApk, minSDKVersion);
        copyFinalApkV2();
    }

    private void insureFileNameV2() {
        mUnSignedApk = new File(mOutDir, mApkName);
        String apkName;
        if (mApkName.contains("unsigned")) {
            apkName = mApkName.replaceAll("unsigned", "signed");
        } else {
            String prefixName = mApkName.substring(0, mApkName.indexOf(".apk"));
            apkName = prefixName + "_signed.apk";
        }
        mSignedApk = new File(mOutDir, apkName);
    }

    private void writeChannel() throws Exception {
        File tempOutDir = new File(mOutDir, FileOperation.UNZIP_FILE_PATH);

        File archiveFile = new File(tempOutDir, "AndroidManifest.xml");

        AXMLDoc doc = new AXMLDoc();
        doc.parse(new FileInputStream(archiveFile.getAbsolutePath()));

        for (String key : mChannel.manifestPlaceholders.keySet()) {
            ChannelEditor editor = new ChannelEditor(doc, key);
            editor.setChannel(mChannel.manifestPlaceholders.get(key));
            editor.commit();
        }

        FileOperation.deleteFile(archiveFile.getAbsolutePath());
        doc.build(new FileOutputStream(archiveFile));
    }

    private void generalUnsignApk(HashMap<String, Integer> compressData) throws IOException, InterruptedException {

        File tempOutDir = new File(mOutDir, FileOperation.UNZIP_FILE_PATH);

        File[] unzipFiles = tempOutDir.listFiles();
        assert unzipFiles != null;
        List<File> collectFiles = new ArrayList<>(Arrays.asList(unzipFiles));

        FileOperation.zipFiles(collectFiles, tempOutDir, mUnSignedApk, compressData);

        if (!mUnSignedApk.exists()) {
            throw new IOException(String.format("can not found the unsign apk file path=%s", mUnSignedApk.getAbsolutePath()));
        }
    }

    private void signApkV2(File unSignedApk, File signedApk, int minSDKVersion) throws Exception {
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
                mInputParam.signFile.getAbsolutePath(),
                "--ks-pass",
                "pass:" + mInputParam.storePassword,
                "--min-sdk-version",
                String.valueOf(minSDKVersion),
                "--ks-key-alias",
                mInputParam.keyAlias,
                "--key-pass",
                "pass:" + mInputParam.keyPassword,
                "--out",
                signedApk.getAbsolutePath(),
                unSignedApk.getAbsolutePath()
        };
        ApkSignerTool.main(params);
    }

    private void copyFinalApkV2() throws IOException {
//        FileOperation.deleteFile(mUnSignedApk.getAbsolutePath());
    }
}
