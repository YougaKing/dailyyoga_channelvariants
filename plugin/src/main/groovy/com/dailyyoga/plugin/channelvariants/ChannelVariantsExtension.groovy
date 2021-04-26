package com.dailyyoga.plugin.channelvariants

import com.dailyyoga.plugin.channelvariants.util.Logger
import com.google.common.collect.Lists

class ChannelVariantsExtension {

    List<ChannelVariants> channelVariants = Lists.newArrayList()

    boolean enable = true
    int logLevel
    File logDir
    boolean andResGuard
    boolean isFastMode = true

    void config(String channel, boolean global, List<String> excludes) {
        ChannelVariants object = ChannelVariants.create(channel, global, excludes)
        Logger.error("object: ${object}")
        channelVariants.add(object)
    }

    void config(String channel, List<String> variants) {
        ChannelVariants object = ChannelVariants.create(channel, variants)
        Logger.error("object: ${object}")
        channelVariants.add(object)
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