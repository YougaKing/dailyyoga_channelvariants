package com.dailyyoga.plugin.channelvariants


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
    Map<String, List<String>> fileMap = new HashMap<>()

    // ("dailyYoga", "*|!vivo|!huawei*|!oppo|!xiaomi")
    // ("huaWei", "huawei*")
    void config(String channel, String pattern) {
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
        channelMap.put(channel, excludeInclude)
        Logger.error("channel: ${channel}" + ", excludeInclude: ${excludeInclude}")
    }

    //("/Users/youga/Downloads/baiDu_100003_release_8.10.0.0_20210427_signed.apk", "Lenovo:100021|LittleChannel:100039")
    void configFile(String filePath, String pattern) {
        List<String> channels = Lists.newArrayList()
        String[] arrays = pattern.split(SEPARATOR)
        channels.addAll(arrays)
        fileMap.put(filePath, channels)
        Logger.error("filePath: ${filePath}" + ", channels: ${channels}")
    }

    @Override
    public String toString() {
        return "ChannelVariantsExtension{" +
                "enable=" + enable +
                ", logLevel=" + logLevel +
                ", logDir=" + logDir +
                ", andResGuard=" + andResGuard +
                ", isFastMode=" + isFastMode +
                ", channelMap=" + channelMap +
                ", fileMap=" + fileMap +
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
            return "ExcludeInclude{" +
                    "global=" + global +
                    ", excludes=" + excludes +
                    ", includes=" + includes +
                    '}';
        }
    }
}