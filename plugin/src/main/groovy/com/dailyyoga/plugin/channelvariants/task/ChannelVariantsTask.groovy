package com.dailyyoga.plugin.channelvariants.task

import com.android.builder.model.SigningConfig
import com.dailyyoga.plugin.channelvariants.signer.SigningConfigProperties

class ChannelVariantsTask extends GeneralTask {

    @Override
    File originApkFile() {
        return variant.outputs.first().outputFile
    }

    @Override
    SigningConfigProperties signingConfig() {
        SigningConfig signingConfig = getSigningConfig()
        SigningConfigProperties properties = new SigningConfigProperties()
        properties.setStoreFile(signingConfig.storeFile)
        properties.setStorePassword(signingConfig.storePassword)
        properties.setKeyAlias(signingConfig.keyAlias)
        properties.setKeyPassword(signingConfig.keyPassword)
        return properties
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