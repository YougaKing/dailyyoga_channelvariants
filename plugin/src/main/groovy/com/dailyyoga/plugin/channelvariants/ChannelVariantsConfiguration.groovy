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
                "flavor='" + flavor + '\'' +
                ", flavors=" + flavors +
                '}';
    }


}