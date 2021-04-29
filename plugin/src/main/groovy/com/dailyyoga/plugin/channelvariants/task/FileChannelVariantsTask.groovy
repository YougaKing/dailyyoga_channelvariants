package com.dailyyoga.plugin.channelvariants.task

class FileChannelVariantsTask extends GeneralTask {

    String filePath

    @Override
    File originApkFile() {
        return new File(filePath)
    }
}