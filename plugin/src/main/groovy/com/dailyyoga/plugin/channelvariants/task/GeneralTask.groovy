package com.dailyyoga.plugin.channelvariants.task

import com.android.builder.model.SigningConfig
import com.dailyyoga.plugin.channelvariants.ChannelVariantsConfiguration
import com.dailyyoga.plugin.channelvariants.Main
import com.dailyyoga.plugin.channelvariants.apk.InputParam
import com.dailyyoga.plugin.channelvariants.util.Logger
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class GeneralTask extends DefaultTask {

    ChannelVariantsConfiguration configuration

    GeneralTask() {
        group = 'channelVariants'
    }

    File outApkDir() {
        return configuration.extension.apkDir
    }

    abstract File originApkFile()

    @TaskAction
    public void run() {
        def start = System.currentTimeMillis()

        def logLevel = configuration.extension.logLevel
        def logDir = configuration.extension.logDir ?: project.file("${project.buildDir}/outputs/logs")
        Logger.init(logLevel < 0 ? Logger.LEVEL_CONSOLE : logLevel, logDir, getName())


        File outApkDir = outApkDir()
        Logger.info("outApkDir:" + (outApkDir == null ? "null" : outApkDir.absolutePath))
        File originApkFile = originApkFile()
        Logger.info("originApkFile:" + originApkFile.absolutePath)


        SigningConfig apkSigningConfig = getSigningConfig()

        InputParam.Builder builder = new InputParam.Builder()
                .setOriginApk(originApk)
                .setOutApkDir(outApkDir)
                .setOriginChannel(configuration.originChannel)
                .setChannelList(configuration.channelList)
                .setSignFile(apkSigningConfig.storeFile)
                .setStorePassword(apkSigningConfig.storePassword)
                .setKeyAlias(apkSigningConfig.keyAlias)
                .setKeyPassword(apkSigningConfig.keyPassword)

        Main.gradleRun(builder.create())

        Logger.debug(getName() + " end, " +
                "sign apks:${channelList.size()}, " +
                "time use:${System.currentTimeMillis() - start} ms")

        Logger.close()
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