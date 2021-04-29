package com.dailyyoga.plugin.channelvariants.task

import com.dailyyoga.plugin.channelvariants.signer.SigningConfigProperties

class FileChannelVariantsTask extends GeneralTask {

    String filePath

    @Override
    SigningConfigProperties signingConfig() {
        return configuration.extension.signingConfig
    }

    @Override
    File originApkFile() {
        return new File(filePath)
    }
}