package com.dailyyoga.plugin.channelvariants

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.api.ReadOnlyProductFlavor
import com.android.builder.model.ProductFlavor
import com.dailyyoga.plugin.channelvariants.util.Logger
import com.dailyyoga.plugin.channelvariants.util.Utils
import com.google.common.collect.Lists
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.Task
import com.dailyyoga.plugin.channelvariants.task.ChannelVariantsTask
import com.dailyyoga.plugin.channelvariants.task.FileChannelVariantsTask

class ChannelVariantsPlugin implements Plugin<Project> {

    static final String RES_GUARD_TASK_PREFIX = "resguard"
    static final String ASSEMBLE_TASK_PREFIX = "assemble"

    Project project
    AppExtension android
    ChannelVariantsExtension extension

    @Override
    void apply(Project project) {

        this.project = project
        this.android = project.android

        if (!Utils.hasAndroidPlugin(project)) {
            throw new ProjectConfigurationException("plugin 'com.android.application' must be apply", null)
        }
        this.extension = project.extensions.create("channelVariants", ChannelVariantsExtension)

        if (!extension.enable) {
            Logger.error("------------您已关闭了多渠道打包插件--------------")
            return
        }

        if (extension.andResGuard && !Utils.hasAndResGuardPlugin(project)) {
            project.logger.error "is not Support AndResGuard , please check apply plugin: 'AndResGuard' "
            return
        }

        extension.fileMap.each { key, value ->
            ChannelVariantsConfiguration configuration = ChannelVariantsConfiguration.create(extension, key, value)
            if (configuration == null) return
            createFileChannelVariantsTask(key, configuration)
        }

        project.afterEvaluate {

            List<ProductFlavor> globalFlavors = Lists.newArrayList()
            android.productFlavors.all { ProductFlavor flavor ->
                globalFlavors.add(flavor)
            }

            android.applicationVariants.each { ApplicationVariant variant ->
                ReadOnlyProductFlavor flavor = variant.productFlavors.get(0)
                ChannelVariantsConfiguration configuration = ChannelVariantsConfiguration.create(extension, flavor.name, globalFlavors)
                if (configuration == null) return
                createChannelVariantsTask(variant, configuration)
            }
        }
    }

    void createChannelVariantsTask(ApplicationVariant variant, ChannelVariantsConfiguration configuration) {
        def variantName = configuration.originChannel.capitalize()
        def channelVariantsTaskName = "channelVariants${variantName}"

        Task channelVariantsTask = project.task(channelVariantsTaskName, type: ChannelVariantsTask) {
            setVariant(variant)
            setConfiguration(configuration)
        }

        channelVariantsTask.dependsOn variant.assembleProvider.get()
    }

    void createFileChannelVariantsTask(String filePath, ChannelVariantsConfiguration configuration) {
        def variantName = configuration.originChannel.capitalize()
        def channelVariantsTaskName = "channelVariants${variantName}"

        project.task(channelVariantsTaskName, type: FileChannelVariantsTask) {
            setFilePath(filePath)
            setConfiguration(configuration)
        }
    }
}