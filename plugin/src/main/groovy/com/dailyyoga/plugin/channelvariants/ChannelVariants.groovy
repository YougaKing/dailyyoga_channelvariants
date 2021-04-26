package com.dailyyoga.plugin.channelvariants

class ChannelVariants {

    String channel
    List<String> variants

    boolean global
    List<String> excludes

    ChannelVariants(String channel) {
        this.channel = channel
    }

    static ChannelVariants create(String channel, boolean global, List<String> excludes) {
        ChannelVariants channelVariants = new ChannelVariants(channel)
        channelVariants.global = global
        channelVariants.excludes = excludes
        return channelVariants
    }

    static ChannelVariants create(String channel, List<String> variants) {
        ChannelVariants channelVariants = new ChannelVariants(channel)
        channelVariants.variants = variants
        return channelVariants
    }


    @Override
    public String toString() {
        return "ChannelVariants{" +
                "channel='" + channel + '\'' +
                ", variants=" + variants +
                ", global=" + global +
                ", excludes=" + excludes +
                '}';
    }
}