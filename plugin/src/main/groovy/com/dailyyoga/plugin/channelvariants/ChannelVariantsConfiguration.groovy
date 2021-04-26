package com.dailyyoga.plugin.channelvariants

import com.android.builder.model.ProductFlavor

class ChannelVariantsConfiguration {

    ChannelVariantsExtension extension
    String channel
    List<ProductFlavor> flavors

    ChannelVariantsConfiguration(ChannelVariantsExtension extension) {
        this.extension = extension
    }

    @Override
    public String toString() {
        return "ChannelVariantsConfiguration{" +
                "channel='" + channel + '\'' +
                ", flavors=" + flavors.size() +
                '}';
    }


}