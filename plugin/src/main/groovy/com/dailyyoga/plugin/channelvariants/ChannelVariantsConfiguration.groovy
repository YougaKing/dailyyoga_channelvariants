package com.dailyyoga.plugin.channelvariants

import com.android.builder.model.ProductFlavor
import com.dailyyoga.plugin.channelvariants.apk.Channel
import com.dailyyoga.plugin.channelvariants.util.Logger
import com.google.common.collect.Lists
import com.dailyyoga.plugin.channelvariants.ChannelVariantsExtension.ExcludeInclude
import org.apache.http.util.TextUtils

class ChannelVariantsConfiguration {

    ChannelVariantsExtension extension
    String originChannel
    List<Channel> channelList = Lists.newArrayList()

    ChannelVariantsConfiguration(ChannelVariantsExtension extension) {
        this.extension = extension
    }


    boolean available() {
        return !TextUtils.isEmpty(originChannel) && !channelList.isEmpty()
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
        return configuration.available() ? configuration : null
    }

    static ChannelVariantsConfiguration createByFile(ChannelVariantsExtension extension,
                                                     String filePath,
                                                     List<String> channels) {
        File apkFile = new File(filePath)
        if (!apkFile.exists()) return null

        ChannelVariantsConfiguration configuration = new ChannelVariantsConfiguration(extension)
        configuration.originChannel = apkFile.name.substring(0, apkFile.name.indexOf("_"))

        channels.each {
            String[] arrays = it.split(":")
            String name = arrays[0]
            Map<String, Object> manifestPlaceholders = new HashMap<>()
            manifestPlaceholders.put("CHANNEL_NAME", name)
            manifestPlaceholders.put("CHANNEL_ID", arrays[1])

            Channel channel = Channel.create(name, manifestPlaceholders)
            configuration.channelList.add(channel)
        }

        return configuration.available() ? configuration : null
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