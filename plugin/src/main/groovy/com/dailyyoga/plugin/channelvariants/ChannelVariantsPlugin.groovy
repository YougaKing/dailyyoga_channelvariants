package com.dailyyoga.plugin.channelvariants

import com.android.build.api.artifact.ArtifactKind
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

        project.afterEvaluate {

            List<ProductFlavor> globalFlavors = Lists.newArrayList()
            android.productFlavors.all { ProductFlavor flavor ->
                globalFlavors.add(flavor)
            }

            ApplicationVariant fileVariant
            android.applicationVariants.each { ApplicationVariant variant ->
                if (variant.buildType.name.equalsIgnoreCase("Release")) {
                    fileVariant = variant
                }
                ReadOnlyProductFlavor flavor = variant.productFlavors.get(0)
                ChannelVariantsConfiguration configuration = ChannelVariantsConfiguration.create(extension, flavor.name, globalFlavors)
                if (configuration == null) return
                createChannelVariantsTask(variant, configuration)
            }

            if (fileVariant != null) {
                extension.fileMap.each { key, value ->
                    ChannelVariantsConfiguration configuration = ChannelVariantsConfiguration.createByFile(extension, key, globalFlavors)
                    Logger.error("configuration: ${configuration}")
                    if (configuration == null) return
                    createFileChannelVariantsTask(fileVariant, key, configuration)
                }
            }
        }
    }

    void createChannelVariantsTask(ApplicationVariant variant, ChannelVariantsConfiguration configuration) {
        def variantName = configuration.originChannel.name.capitalize()
        def channelVariantsTaskName = "channelVariants${variantName}" + variant.buildType.name

        Task channelVariantsTask = project.task(channelVariantsTaskName, type: ChannelVariantsTask) {
            setVariant(variant)
            setConfiguration(configuration)
        }

        channelVariantsTask.dependsOn variant.assembleProvider.get()
    }

    void createFileChannelVariantsTask(ApplicationVariant variant, File apkFile, ChannelVariantsConfiguration configuration) {
        def variantName = apkFile.name.replaceAll(".apk", "").replaceAll("_", "")
        def channelVariantsTaskName = "channelVariantsFile${variantName}"

        project.task(channelVariantsTaskName, type: FileChannelVariantsTask) {
            setVariant(variant)
            setConfiguration(configuration)
            setApkFile(apkFile)
        }
    }
}