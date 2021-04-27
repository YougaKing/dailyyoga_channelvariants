package com.dailyyoga.plugin.channelvariants

import com.android.builder.model.ProductFlavor
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

    // "*|!vivo|!huawei*|!oppo|!xiaomi"
    // "huawei*"
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

    ChannelVariantsConfiguration getConfiguration(String flavorName,
                                                  List<ProductFlavor> globalFlavors) {
        ExcludeInclude excludeInclude = channelMap.get(flavorName)
        if (excludeInclude == null) return

        ChannelVariantsConfiguration configuration = new ChannelVariantsConfiguration(this)

        List<ProductFlavor> flavors = Lists.newArrayList()
        if (excludeInclude.global) {
            globalFlavors.each { ProductFlavor flavor ->
                if (!excludeInclude.isExclude(flavor.name)) {
                    flavors.add(flavor)
                }
                if (flavorName.equalsIgnoreCase(flavor.name)) {
                    configuration.flavor = flavor
                }
            }
        } else {
            globalFlavors.each { ProductFlavor flavor ->
                if (excludeInclude.isInclude(flavor.name)) {
                    flavors.add(flavor)
                }
                if (flavorName.equalsIgnoreCase(flavor.name)) {
                    configuration.flavor = flavor
                }
            }
        }
        configuration.flavors = flavors
        return configuration
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
                    Logger.error("it: ${it}" + ", flavorName: ${flavorName}")
                    def temp = it.replace(GLOBAL, "")
                    if (flavorName.toLowerCase().startsWith(temp.toUpperCase())) {
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
                    if (flavorName.toLowerCase().startsWith(temp.toUpperCase())) {
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