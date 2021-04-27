package com.dailyyoga.plugin.channelvariants;

import com.dailyyoga.plugin.channelvariants.ChannelVariantsExtension.ExcludeInclude;

import org.junit.Test;

public class ChannelVariantsExtensionTest {


    @Test
    public void configTest() {
        ChannelVariantsExtension extension = new ChannelVariantsExtension();
        extension.config("dailyYoga", "*|!vivo|!huawei*|!oppo|!xiaomi");
        extension.config("huaWei", "huawei*");

        ExcludeInclude excludeInclude = extension.getChannelMap().get("dailyYoga");


        System.out.println("huaWeiInfo".toLowerCase().startsWith("huaWei".toLowerCase()));
        System.out.println(excludeInclude.isExclude("huaWei"));
        System.out.println(excludeInclude.isExclude("huaWeiInfo"));
        System.out.println(excludeInclude.isExclude("vivo"));
    }
}
