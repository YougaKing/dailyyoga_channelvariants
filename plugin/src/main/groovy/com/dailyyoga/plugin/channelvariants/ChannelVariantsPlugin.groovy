package com.dailyyoga.plugin.channelvariants

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.api.ReadOnlyProductFlavor
import com.android.builder.model.ProductFlavor
import com.dailyyoga.plugin.channelvariants.ChannelVariants.ChannelVariantsBuilder
import com.dailyyoga.plugin.channelvariants.util.Logger
import com.dailyyoga.plugin.channelvariants.util.Utils
import com.google.common.collect.Lists
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.Task

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

            List<String> globalChannels = Lists.newArrayList()
            android.productFlavors.all { ProductFlavor flavor ->
                Logger.error("flavor: ${flavor.name}")
                globalChannels.add(flavor.name)
            }

            android.productFlavors.all { ProductFlavor flavor ->

            }

            android.applicationVariants.each { ApplicationVariant variant ->

                ReadOnlyProductFlavor flavor = variant.productFlavors.get(0)
                ChannelVariantsBuilder builder = extension.getChannelVariantsBuilder(flavor.name)
                if (builder == null) return
                ChannelVariants channelVariants = ChannelVariants.create(globalChannels, builder, variant)
                Logger.error("channelVariants: ${channelVariants}")
                extension.channelVariants.add(channelVariants)
                createChannelVariantsTask(variant, channelVariants)
            }
        }
    }

    void createChannelVariantsTask(ApplicationVariant variant, ChannelVariants channelVariants) {
        def variantName = variant.name.capitalize()
        def channelVariantsTaskName = "channelVariants${variantName}"

        Logger.error("variantName: ${variantName} " + "channelVariantsTaskName: ${channelVariantsTaskName}")
        Task channelVariantsTask = project.task(channelVariantsTaskName, type: ChannelVariantsTask) {
            setVariant(variant)
            setChannelVariants(channelVariants)
        }

        channelVariantsTask.dependsOn variant.assembleProvider.get()
    }
}