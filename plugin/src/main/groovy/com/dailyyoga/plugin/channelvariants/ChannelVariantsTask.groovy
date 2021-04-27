package com.dailyyoga.plugin.channelvariants

import com.android.build.gradle.api.ApplicationVariant
import com.android.builder.model.ProductFlavor
import com.android.builder.model.SigningConfig
import com.dailyyoga.plugin.channelvariants.util.Logger
import com.dailyyoga.plugin.channelvariants.apk.Channel
import com.dailyyoga.plugin.channelvariants.apk.InputParam
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
        def logLevel = configuration.extension.logLevel
        def logDir = configuration.extension.logDir ?: project.file("${project.buildDir}/outputs/logs")
        Logger.init(logLevel < 0 ? Logger.LEVEL_CONSOLE : logLevel, logDir, configuration.flavor.name)

        File originApk = variant.outputs.first().outputFile
        Logger.info("originApk:" + originApk.absolutePath)

        Channel originChannel = Channel.create(configuration.flavor.name, configuration.flavor.manifestPlaceholders)

        List<Channel> channelList = new ArrayList<>()
        configuration.flavors.each { ProductFlavor flavor ->
            Channel channel = Channel.create(flavor.name, flavor.manifestPlaceholders)
            channelList.add(channel)
        }

        SigningConfig apkSigningConfig = getSigningConfig()

        InputParam.Builder builder = new InputParam.Builder()
                .setOriginApk(originApk)
                .setOutApkDir(configuration.extension.apkDir)
                .setOriginChannel(originChannel)
                .setChannelList(channelList)
                .setSignFile(apkSigningConfig.storeFile)
                .setStorePassword(apkSigningConfig.storePassword)
                .setKeyAlias(apkSigningConfig.keyAlias)
                .setKeyPassword(apkSigningConfig.keyPassword)

        Main.gradleRun(builder.create())
    }

    /**
     * get the SigningConfig
     * @return
     */
    SigningConfig getSigningConfig() {
        //return variant.buildType.signingConfig == null ? variant.mergedFlavor.signingConfig : variant.buildType.signingConfig
        SigningConfig config = null
        if (variant.hasProperty("signingConfig") && variant.signingConfig != null) {
            config = variant.signingConfig
        } else if (variant.hasProperty("variantData") &&
                variant.variantData.hasProperty("variantConfiguration") &&
                variant.variantData.variantConfiguration.hasProperty("signingConfig") &&
                variant.variantData.variantConfiguration.signingConfig != null) {
            config = variant.variantData.variantConfiguration.signingConfig
        } else if (variant.hasProperty("apkVariantData") &&
                variant.apkVariantData.hasProperty("variantConfiguration") &&
                variant.apkVariantData.variantConfiguration.hasProperty("signingConfig") &&
                variant.apkVariantData.variantConfiguration.signingConfig != null) {
            config = variant.apkVariantData.variantConfiguration.signingConfig
        }
        return config
    }

}