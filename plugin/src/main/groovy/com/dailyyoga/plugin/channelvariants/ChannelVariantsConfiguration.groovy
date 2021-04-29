package com.dailyyoga.plugin.channelvariants

import com.android.builder.model.ProductFlavor
import com.dailyyoga.plugin.channelvariants.ChannelVariantsExtension.ExcludeInclude
import com.dailyyoga.plugin.channelvariants.apk.Channel
import com.dailyyoga.plugin.channelvariants.util.Logger
import com.google.common.collect.Lists

class ChannelVariantsConfiguration {

    ChannelVariantsExtension extension
    Channel originChannel
    List<Channel> channelList = Lists.newArrayList()

    ChannelVariantsConfiguration(ChannelVariantsExtension extension) {
        this.extension = extension
    }

    boolean available() {
        return !channelList.isEmpty()
    }

    static ChannelVariantsConfiguration create(ChannelVariantsExtension extension,
                                               String flavorName,
                                               List<ProductFlavor> globalFlavors) {
        ExcludeInclude excludeInclude = extension.channelMap.get(flavorName)
        if (excludeInclude == null) return

        ChannelVariantsConfiguration configuration = new ChannelVariantsConfiguration(extension)
        dispose(configuration, excludeInclude, flavorName, globalFlavors)

        return configuration.available() ? configuration : null
    }

    static ChannelVariantsConfiguration createByFile(ChannelVariantsExtension extension,
                                                     String apkFilePath,
                                                     List<ProductFlavor> globalFlavors) {
        ExcludeInclude excludeInclude = extension.fileMap.get(apkFilePath)
        if (excludeInclude == null) return

        ChannelVariantsConfiguration configuration = new ChannelVariantsConfiguration(extension)
        dispose(configuration, excludeInclude, "", globalFlavors)

        return configuration.available() ? configuration : null
    }

    static void dispose(ChannelVariantsConfiguration configuration,
                        ExcludeInclude excludeInclude,
                        String flavorName,
                        List<ProductFlavor> globalFlavors) {
        globalFlavors.each { ProductFlavor flavor ->
            if (flavorName.equalsIgnoreCase(flavor.name)) {
                configuration.originChannel = transform(flavor)
            } else {
                if (excludeInclude.global) {
                    if (!excludeInclude.isExclude(flavor.name)) {
                        configuration.channelList.add(transform(flavor))
                    }
                } else {
                    if (excludeInclude.isInclude(flavor.name)) {
                        configuration.channelList.add(transform(flavor))
                    }
                }
            }
        }
    }

    static Channel transform(ProductFlavor flavor) {
        return Channel.create(flavor.name, flavor.manifestPlaceholders)
    }

    @Override
    public String toString() {
        return "ChannelVariantsConfiguration{" +
                "extension=" + extension +
                ", originChannel=" + originChannel +
                ", channelList=" + channelList +
                '}';
    }
}