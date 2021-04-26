package com.dailyyoga.plugin.channelvariants

import com.android.build.gradle.internal.api.ReadOnlyProductFlavor
import com.android.builder.model.ProductFlavor
import com.google.common.collect.Lists

class ChannelVariantsExtension {

    static final String GLOBAL = "*"

    boolean enable = true
    int logLevel
    File logDir
    boolean andResGuard
    boolean isFastMode = true
    Map<String, List<String>> channelMap = new HashMap<>()

    void configGlobal(String channel, String... channels) {
        List<String> channelList = Lists.newArrayList()
        channelList.add(GLOBAL)
        channelList.addAll(channels)
        channelMap.put(channel, channelList)
    }

    void config(String channel, String... channels) {
        List<String> channelList = Lists.newArrayList()
        channelList.addAll(channels)
        channelMap.put(channel, channelList)
    }

    ChannelVariantsConfiguration getConfiguration(String flavorName,
                                                  List<ProductFlavor> globalFlavors) {
        List<String> channels = channelMap.get(flavorName)
        if (channels == null || channels.isEmpty()) return

        ChannelVariantsConfiguration configuration = new ChannelVariantsConfiguration(this)

        List<ProductFlavor> flavors = Lists.newArrayList()
        if (channels.contains(GLOBAL)) {
            globalFlavors.each { ProductFlavor flavor ->
                if (!channels.contains(flavor.name)) {
                    flavors.add(flavor)
                }
                if (flavorName.equalsIgnoreCase(flavor.name)) {
                    configuration.flavor = flavor
                }
            }
        } else {
            globalFlavors.each { ProductFlavor flavor ->
                if (channels.contains(flavor.name)) {
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
}