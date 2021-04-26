package com.dailyyoga.plugin.channelvariants

class ChannelVariantsConfiguration {

    ChannelVariantsExtension extension
    String channel
    List<String> channels

    ChannelVariantsConfiguration(ChannelVariantsExtension extension) {
        this.extension = extension
    }

    @Override
    public String toString() {
        return "ChannelVariantsConfiguration{" +
                "channel='" + channel + '\'' +
                ", channels=" + channels +
                '}';
    }


}