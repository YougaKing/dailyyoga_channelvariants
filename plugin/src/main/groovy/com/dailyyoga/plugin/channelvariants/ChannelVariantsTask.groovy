package com.dailyyoga.plugin.channelvariants

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.api.ReadOnlySigningConfig
import com.android.builder.model.ProductFlavor
import com.dailyyoga.plugin.channelvariants.util.Logger
import com.youga.apk.Channel
import com.youga.apk.InputParam
import com.youga.manifest.Main
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class ChannelVariantsTask extends DefaultTask {

    ApplicationVariant variant
    ChannelVariantsConfiguration configuration

    ChannelVariantsTask() {
        group = 'channelVariants'
    }

    @TaskAction
    public void run() {
        def logLevel = extension.logLevel
        def logDir = extension.logDir ?: project.file("${project.buildDir}/outputs/logs")
        Logger.init(logLevel < 0 ? Logger.LEVEL_CONSOLE : logLevel, logDir, configuration.channel)

        File originApk = variant.outputs.first().outputFile
        Logger.info("originApk:" + originApk.absolutePath)

        List<Channel> channelList = new ArrayList<>()
        configuration.flavors.each { ProductFlavor flavor ->
            Channel channel = Channel.create(flavor.name, flavor.manifestPlaceholders)
            channelList.add(channel)
        }

        ReadOnlySigningConfig apkSigningConfig = variant.variantData.variantConfiguration.signingConfig

        InputParam.Builder builder = new InputParam.Builder()
                .setOriginApk(originApk)
                .setOriginChannel(configuration.channel)
                .setChannelList(channelList)
                .setSignFile(apkSigningConfig.storeFile)
                .setStorePassword(apkSigningConfig.storePassword)
                .setKeyAlias(apkSigningConfig.keyAlias)
                .setKeyPassword(apkSigningConfig.keyPassword)

        Main.gradleRun(builder.create())
    }


}