package com.dailyyoga.plugin.channelvariants

import com.android.builder.model.ProductFlavor
import com.google.common.collect.Lists

class ChannelVariantsExtension {

    static final String SEPARATOR = "|"
    static final String GLOBAL = "*"
    static final String EXCLUDE = "!"

    boolean enable = true
    int logLevel
    File logDir
    boolean andResGuard
    File apkDir
    Map<String, ExcludeInclude> channelMap = new HashMap<>()
    boolean global

    // "*|!vivo|!huawei*|!oppo|!xiaomi"
    // "huawei*"
    void config(String channel, String pattern) {
        ExcludeInclude excludeInclude = new ExcludeInclude()
        String[] arrays = pattern.split(SEPARATOR)
        arrays.each {
            if (it == GLOBAL) {
                global = true
            } else if (it.startsWith(EXCLUDE)) {
                excludeInclude.excludes.add(it)
            } else {
                excludeInclude.includes.add(it)
            }
        }
        channelMap.put(channel, excludeInclude)
    }

    ChannelVariantsConfiguration getConfiguration(String flavorName,
                                                  List<ProductFlavor> globalFlavors) {
        ExcludeInclude excludeInclude = channelMap.get(flavorName)
        if (excludeInclude == null) return

        ChannelVariantsConfiguration configuration = new ChannelVariantsConfiguration(this)

        List<ProductFlavor> flavors = Lists.newArrayList()
        if (global) {
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
                if (excludeInclude.includes.contains(flavor.name)) {
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
        List<String> excludes = Lists.newArrayList()
        List<String> includes = Lists.newArrayList()

        boolean isExclude(String flavorName) {
            boolean exclude = false
            excludes.each {
                if (it.endsWith(GLOBAL)) {
                    def temp = it.replace(GLOBAL, "")
                    if (flavorName.startsWith(temp)) {
                        exclude = true
                    }
                } else if (it == flavorName) {
                    exclude = true
                }
            }
            return exclude
        }
    }
}