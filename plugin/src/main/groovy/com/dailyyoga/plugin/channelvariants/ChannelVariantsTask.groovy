package com.dailyyoga.plugin.channelvariants

import com.android.build.gradle.api.ApplicationVariant
import com.dailyyoga.plugin.channelvariants.util.Logger
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class ChannelVariantsTask extends DefaultTask {

    ApplicationVariant variant
    ChannelVariants channelVariants

    ChannelVariantsTask() {
        group = 'channelVariants'
    }

    @TaskAction
    public void run() {
        def logLevel = extension.logLevel
        def logDir = extension.logDir ?: project.file("${project.buildDir}/outputs/logs")
        Logger.init(logLevel < 0 ? Logger.LEVEL_CONSOLE : logLevel, logDir, channelVariants.channel)


    }


}