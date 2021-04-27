package com.dailyyoga.plugin.channelvariants.apk;

import java.io.File;
import java.util.List;

/**
 * @author: YougaKingWu@gmail.com
 * @created on: 2019/7/19 16:54
 * @description:
 */
public class InputParam {

    public final File originApk;
    public final File outApkDir;
    public final Channel originChannel;
    public final List<Channel> channelList;
    public final File signFile;
    public final String storePassword;
    public final String keyAlias;
    public final String keyPassword;

    public InputParam(File originApk, File outApkDir, Channel originChannel, List<Channel> channelList,
                      File signFile, String keyPassword, String keyAlias, String storePassword) {
        this.originApk = originApk;
        this.outApkDir = outApkDir;
        this.originChannel = originChannel;
        this.channelList = channelList;
        this.signFile = signFile;
        this.keyPassword = keyPassword;
        this.keyAlias = keyAlias;
        this.storePassword = storePassword;
    }


    public static class Builder {
        private File originApk;
        private File outApkDir;
        private Channel originChannel;
        private List<Channel> channelList;
        private File signFile;
        private String keyPassword;
        private String keyAlias;
        private String storePassword;

        public Builder setOriginApk(File originApk) {
            this.originApk = originApk;
            return this;
        }

        public Builder setOutApkDir(File outApkDir) {
            this.outApkDir = outApkDir;
            return this;
        }

        public Builder setOriginChannel(Channel originChannel) {
            this.originChannel = originChannel;
            return this;
        }

        public Builder setChannelList(List<Channel> channelList) {
            this.channelList = channelList;
            return this;
        }

        public Builder setSignFile(File signFile) {
            this.signFile = signFile;
            return this;
        }

        public Builder setKeyPassword(String keyPassword) {
            this.keyPassword = keyPassword;
            return this;
        }

        public Builder setKeyAlias(String keyAlias) {
            this.keyAlias = keyAlias;
            return this;
        }

        public Builder setStorePassword(String storePassword) {
            this.storePassword = storePassword;
            return this;
        }

        public InputParam create() {
            return new InputParam(
                    originApk,
                    outApkDir,
                    originChannel,
                    channelList,
                    signFile,
                    keyPassword,
                    keyAlias,
                    storePassword
            );
        }
    }

    @Override
    public String toString() {
        return "InputParam{" +
                "originApk=" + originApk +
                ", originChannel=" + originChannel +
                ", channelList=" + channelList +
                ", signFile=" + signFile +
                ", storePassword='" + storePassword + '\'' +
                ", keyAlias='" + keyAlias + '\'' +
                ", keyPassword='" + keyPassword + '\'' +
                '}';
    }
}
