package com.dailyyoga.plugin.channelvariants

import com.android.builder.model.ProductFlavor
import com.dailyyoga.plugin.channelvariants.apk.Channel
import com.google.common.collect.Lists
import com.dailyyoga.plugin.channelvariants.ChannelVariantsExtension.ExcludeInclude

class ChannelVariantsConfiguration {

    ChannelVariantsExtension extension
    String originChannel
    List<Channel> channelList = Lists.newArrayList()

    ChannelVariantsConfiguration(ChannelVariantsExtension extension) {
        this.extension = extension
    }

    static ChannelVariantsConfiguration create(ChannelVariantsExtension extension,
                                               String flavorName,
                                               List<ProductFlavor> globalFlavors) {
        ExcludeInclude excludeInclude = extension.channelMap.get(flavorName)
        if (excludeInclude == null) return

        ChannelVariantsConfiguration configuration = new ChannelVariantsConfiguration(extension)

        globalFlavors.each { ProductFlavor flavor ->
            if (flavorName.equalsIgnoreCase(flavor.name)) {
                configuration.originChannel = flavor.name
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
        return configuration
    }

    static ChannelVariantsConfiguration create(ChannelVariantsExtension extension,
                                               String filePath,
                                               List<String> channels) {

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