package com.dailyyoga.plugin.channelvariants


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
                                                  List<String> globalChannels) {
        List<String> channelList = channelMap.get(flavorName)
        if (channelList == null || channelList.isEmpty()) return

        ChannelVariantsConfiguration configuration = new ChannelVariantsConfiguration(this)
        configuration.channel = flavorName

        if (channelList.contains(GLOBAL)) {
            List<String> channels = new ArrayList<>(globalChannels)
            channelList.each {
                channels.remove(it)
            }
            configuration.channels = channels
        } else {
            configuration.channels = channelList
        }
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