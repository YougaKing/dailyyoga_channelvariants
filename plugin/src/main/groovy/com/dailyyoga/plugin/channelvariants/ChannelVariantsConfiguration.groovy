package com.dailyyoga.plugin.channelvariants

import com.android.builder.model.ProductFlavor

class ChannelVariantsConfiguration {

    ChannelVariantsExtension extension
    ProductFlavor flavor
    List<ProductFlavor> flavors

    ChannelVariantsConfiguration(ChannelVariantsExtension extension) {
        this.extension = extension
    }

    @Override
    public String toString() {
        return "ChannelVariantsConfiguration{" +
                "flavor='" + flavor.name + '\'' +
                ", flavors=" + flavorsToString() +
                '}';
    }

    String flavorsToString() {
        StringBuilder builder = new StringBuilder()
        builder.append("[")
        flavors.each {
            builder.append("\'")
            builder.append(it.name)
            builder.append("\'")
            builder.append(",")
        }
        int index = builder.lastIndexOf(",")
        if (index > 0) {
            builder.deleteCharAt(builder.lastIndexOf(","))
        }
        builder.append("]")
        return builder
    }
}