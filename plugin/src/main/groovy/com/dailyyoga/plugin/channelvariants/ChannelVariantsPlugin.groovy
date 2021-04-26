package com.dailyyoga.plugin.channelvariants

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.dsl.ProductFlavor
import com.dailyyoga.plugin.channelvariants.util.Utils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.Task
import com.dailyyoga.plugin.channelvariants.util.Logger

class ChannelVariantsPlugin implements Plugin<Project> {

    Project project;
    AppExtension android

    @Override
    void apply(Project project) {

        this.project = project

        if (!Utils.hasAndroidPlugin(project)) {
            throw new ProjectConfigurationException("plugin 'com.android.application' must be apply", null)
        }

        ChannelVariantsExtension extension = project.extensions.create("channelVariants", ChannelVariantsExtension)
        Logger.error("extension: ${extension}")

        if (!extension.enable) {
            Logger.error("------------您已关闭了多渠道打包插件--------------")
            return
        }

        if (extension.channelVariants.isEmpty()) {
            Logger.error("------------未配置打包渠道、多渠道打包插件不可用--------------")
            return
        }

        project.afterEvaluate {

            if (extension.andResGuard && !Utils.hasAndResGuardPlugin(project)) {
                project.logger.error "is not Support AndResGuard , please check apply plugin: 'AndResGuard' "
                return
            }
            android = project.android
            android.applicationVariants.all { ApplicationVariant variant ->

                android.productFlavors.all { ProductFlavor flavor ->

                    extension.channelVariants.each { ChannelVariants channelVariants ->

                        Logger.error("channelVariants: ${channelVariants}")

                        if (!flavor.name.equalsIgnoreCase(channelVariants.channel)) return

                        createChannelVariantsTask(variant, channelVariants)
                    }

                }

            }
        }
    }

    void createChannelVariantsTask(ApplicationVariant variant, ChannelVariants channelVariants) {
        def variantName = variant.name.capitalize();
        Task channelVariantsTask = project.task("channelVariants${variantName}", type: ChannelVariantsTask) {
            setChannelVariants(channelVariants)
        }
        channelVariantsTask.dependsOn variant.assembleProvider.get()
    }
}