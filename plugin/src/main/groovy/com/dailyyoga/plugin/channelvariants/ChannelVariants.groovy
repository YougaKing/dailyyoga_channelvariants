package com.dailyyoga.plugin.channelvariants

import com.android.build.gradle.api.ApplicationVariant
import com.google.common.collect.Lists

class ChannelVariants {

    String channel
    ApplicationVariant variant
    List<String> channels

    static ChannelVariantsBuilder createBuilder(String channel,
                                                boolean exclude,
                                                String... channels) {
        ChannelVariantsBuilder builder = new ChannelVariantsBuilder(channel)
        builder.channels.addAll(channels)
        builder.exclude = exclude
        return builder
    }

    static ChannelVariants create(List<String> globalChannels,
                                  ChannelVariantsBuilder builder,
                                  ApplicationVariant variant) {
        ChannelVariants channelVariants = new ChannelVariants()
        channelVariants.channel = builder.channel
        channelVariants.variant = variant

        List<String> channels = new ArrayList<>(globalChannels)
        if (builder.exclude) {
            builder.channels.each {
                channels.remove(it)
            }
            channelVariants.channels = channels
        } else {
            channelVariants.channels = builder.channels
        }
        return channelVariants
    }

    @Override
    public String toString() {
        return "ChannelVariants{" +
                "channel='" + channel + '\'' +
                ", channels=" + channels +
                '}';
    }


    static class ChannelVariantsBuilder {
        List<String> channels = Lists.newArrayList()
        String channel
        boolean exclude

        ChannelVariantsBuilder(String channel) {
            this.channel = channel
        }
    }
}