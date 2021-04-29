package com.dailyyoga.plugin.channelvariants

import com.dailyyoga.plugin.channelvariants.signer.SigningConfigProperties
import com.dailyyoga.plugin.channelvariants.util.Logger
import com.google.common.collect.Lists

class ChannelVariantsExtension {

    static final String SEPARATOR = "\\|"
    static final String GLOBAL = "*"
    static final String EXCLUDE = "!"

    boolean enable = true
    int logLevel
    File logDir
    boolean andResGuard
    File apkDir
    Map<String, ExcludeInclude> channelMap = new HashMap<>()
    Map<File, ExcludeInclude> fileMap = new HashMap<>()
    SigningConfigProperties signingConfig

    // ("dailyYoga", "*|!vivo|!huawei*|!oppo|!xiaomi")
    // ("huaWei", "huawei*")
    void config(String channel, String pattern) {
        ExcludeInclude excludeInclude = createExcludeInclude(pattern)
        channelMap.put(channel, excludeInclude)
        Logger.error("channel: ${channel}" + ", excludeInclude: ${excludeInclude}")
    }

    void signingConfig(File storeFile, String storePassword, String keyAlias, String keyPassword) {
        signingConfig = new SigningConfigProperties()
        signingConfig.setStoreFile(storeFile)
        signingConfig.setStorePassword(storePassword)
        signingConfig.setKeyAlias(keyAlias)
        signingConfig.setKeyPassword(keyPassword)
    }

    //("/Users/youga/Downloads/baiDu_100003_release_8.10.0.0_20210427_signed.apk", "...)
    void config(File file, String pattern) {
        if (!file.exists()) return
        if (signingConfig == null) return
        ExcludeInclude excludeInclude = createExcludeInclude(pattern)
        fileMap.put(file, excludeInclude)
    }

    static ExcludeInclude createExcludeInclude(String pattern) {
        ExcludeInclude excludeInclude = new ExcludeInclude()
        String[] arrays = pattern.split(SEPARATOR)
        arrays.each {
            if (it == GLOBAL) {
                excludeInclude.global = true
            } else if (it.startsWith(EXCLUDE)) {
                excludeInclude.excludes.add(it.replaceAll(EXCLUDE, ""))
            } else {
                excludeInclude.includes.add(it)
            }
        }
        return excludeInclude
    }

    @Override
    public String toString() {
        return "ChannelVariantsExtension{" +
                "enable=" + enable +
                ", logLevel=" + logLevel +
                ", logDir=" + logDir +
                ", andResGuard=" + andResGuard +
                ", \napkDir=" + apkDir +
                ", \nchannelMap=" + channelMap +
                '}';
    }


    static class ExcludeInclude {
        boolean global
        List<String> excludes = Lists.newArrayList()
        List<String> includes = Lists.newArrayList()

        boolean isExclude(String flavorName) {
            boolean exclude = false
            excludes.each {
                if (it.endsWith(GLOBAL)) {
                    def temp = it.replace(GLOBAL, "")
                    if (flavorName.toLowerCase().startsWith(temp.toLowerCase())) {
                        exclude = true
                    }
                } else if (it.equalsIgnoreCase(flavorName)) {
                    exclude = true
                }
            }
            return exclude
        }

        boolean isInclude(String flavorName) {
            boolean include = false
            includes.each {
                if (it.endsWith(GLOBAL)) {
                    def temp = it.replace(GLOBAL, "")
                    if (flavorName.toLowerCase().startsWith(temp.toLowerCase())) {
                        include = true
                    }
                } else if (it.equalsIgnoreCase(flavorName)) {
                    include = true
                }
            }
            return include
        }

        @Override
        public String toString() {
            return "\tExcludeInclude{" +
                    "global=" + global +
                    ", excludes=" + excludes +
                    ", includes=" + includes +
                    '}';
        }
    }
}