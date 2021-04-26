package com.dailyyoga.plugin.channelvariants


import com.google.common.collect.Lists
import com.dailyyoga.plugin.channelvariants.ChannelVariants.ChannelVariantsBuilder

class ChannelVariantsExtension {

    List<ChannelVariantsBuilder> channelVariantsBuilders = Lists.newArrayList()
    List<ChannelVariants> channelVariants = Lists.newArrayList()

    boolean enable = true
    int logLevel
    File logDir
    boolean andResGuard
    boolean isFastMode = true

    void configGlobal(String channel, String... variants) {
        ChannelVariantsBuilder builder = ChannelVariants.createBuilder(channel, true, variants)
        channelVariantsBuilders.add(builder)
    }

    void config(String channel, String... variants) {
        ChannelVariantsBuilder builder = ChannelVariants.createBuilder(channel, false, variants)
        channelVariantsBuilders.add(builder)
    }

    ChannelVariantsBuilder getChannelVariantsBuilder(String flavorName) {
        ChannelVariantsBuilder result = null
        channelVariantsBuilders.each { ChannelVariantsBuilder it ->
            if (it.channel.equalsIgnoreCase(flavorName)) {
                result = it
            }
        }
        return result
    }

    @Override
    public String toString() {
        return "ChannelVariantsExtension{" +
                "enable=" + enable +
                ", logLevel=" + logLevel +
                ", logDir=" + logDir +
                ", andResGuard=" + andResGuard +
                ", isFastMode=" + isFastMode +
                ", channelVariants=" + channelVariants +
                '}';
    }
}