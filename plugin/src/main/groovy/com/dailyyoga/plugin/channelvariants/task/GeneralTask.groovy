package com.dailyyoga.plugin.channelvariants.task

import com.android.build.gradle.api.ApplicationVariant
import com.dailyyoga.plugin.channelvariants.ChannelVariantsConfiguration
import com.dailyyoga.plugin.channelvariants.Main
import com.dailyyoga.plugin.channelvariants.apk.InputParam
import com.dailyyoga.plugin.channelvariants.signer.SigningConfigProperties
import com.dailyyoga.plugin.channelvariants.util.Logger
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class GeneralTask extends DefaultTask {

    ApplicationVariant variant
    ChannelVariantsConfiguration configuration

    GeneralTask() {
        group = 'channelVariants'
    }

    File outApkDir() {
        if (configuration.extension.apkDir != null) {
            return new File((configuration.extension.apkDir.absolutePath + "/" + variant.buildType.name))
        } else {
            return configuration.extension.apkDir
        }
    }

    abstract SigningConfigProperties signingConfig()

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


        SigningConfigProperties apkSigningConfig = signingConfig()

        InputParam.Builder builder = new InputParam.Builder()
                .setOriginApk(originApkFile)
                .setOutApkDir(outApkDir)
                .setOriginChannel(configuration.originChannel)
                .setChannelList(configuration.channelList)
                .setSignFile(apkSigningConfig.storeFile)
                .setStorePassword(apkSigningConfig.storePassword)
                .setKeyAlias(apkSigningConfig.keyAlias)
                .setKeyPassword(apkSigningConfig.keyPassword)

        Main.gradleRun(builder.create())

        Logger.debug(getName() + " end, " +
                "sign apks:${configuration.channelList.size()}, " +
                "time use:${System.currentTimeMillis() - start} ms")

        Logger.close()
    }


}