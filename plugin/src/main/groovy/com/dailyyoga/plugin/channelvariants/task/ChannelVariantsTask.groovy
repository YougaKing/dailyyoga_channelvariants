package com.dailyyoga.plugin.channelvariants.task

import com.android.build.gradle.api.ApplicationVariant

class ChannelVariantsTask extends GeneralTask {

    ApplicationVariant variant

    @Override
    File outApkDir() {
        if (configuration.extension.apkDir != null) {
            return new File((configuration.extension.apkDir.absolutePath + "/" + variant.buildType.name))
        } else {
            return super.outApkDir()
        }
    }

    @Override
    File originApkFile() {
        return variant.outputs.first().outputFile
    }

}