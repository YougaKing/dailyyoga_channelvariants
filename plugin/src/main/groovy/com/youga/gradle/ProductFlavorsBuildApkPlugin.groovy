package com.youga.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.dsl.ProductFlavor
import org.gradle.api.Plugin
import org.gradle.api.Project

class ProductFlavorsBuildApkPlugin implements Plugin<Project> {

    static final String CLASS_AND_RES_GUARD_PLUGIN = "com.tencent.gradle.AndResGuardPlugin"
    static final String TASK_PREFIX = "productFlavorsBuildApk"
    static final String RES_GUARD_TASK_PREFIX = "resguard"
    static final String ASSEMBLE_TASK_PREFIX = "assemble"

    AppExtension android
    ProductFlavorsBuildApkExtension configuration
    Project mProject

    @Override
    void apply(Project project) {

        mProject = project
        println "=================================="
        println "ProductFlavorsBuildApkPlugin start"
        println "=================================="

        mProject.extensions.create('productFlavorsBuildApk', ProductFlavorsBuildApkExtension)

        mProject.afterEvaluate {

            try {
                android = mProject.extensions.android

                configuration = mProject.productFlavorsBuildApk
                println configuration.toString()

                if (configuration.andResGuard && !isSupportAndResGuard()) {
                    mProject.logger.error "is not Support AndResGuard , please check apply plugin: 'AndResGuard' "
                    return
                }

                List<String> otherDimensionFlavorList = new ArrayList<>()

                android.productFlavors.all { ProductFlavor flavor ->
                    if (flavor.manifestPlaceholders.size() == 0) {
                        otherDimensionFlavorList.add(flavor.name.capitalize())
                    }
                }
                println "otherDimensionFlavorList:" + otherDimensionFlavorList

                if (otherDimensionFlavorList.size() == 0) {
                    createTaskOnBuildType("")
                } else {
                    otherDimensionFlavorList.each { String otherDimensionFlavor ->
                        createTaskOnBuildType(otherDimensionFlavor)
                    }
                }
            } catch (Exception e) {
                mProject.logger.error "ProductFlavorsBuildApkPlugin", e
            }
        }

        println "=================================="
        println "ProductFlavorsBuildApkPlugin   end"
        println "=================================="
    }

    void createTaskOnBuildType(String otherDimensionFlavor) {
        String taskName = otherDimensionFlavor + configuration.originFlavor.capitalize() + "Debug"
        dependsOnTask(taskName)

        taskName = otherDimensionFlavor + configuration.originFlavor.capitalize() + "Release"
        dependsOnTask(taskName)
    }

    void dependsOnTask(String taskName) {
        String resGuardTaskName = RES_GUARD_TASK_PREFIX + taskName
        String assembleTaskName = ASSEMBLE_TASK_PREFIX + taskName

        println "resGuardTaskName:" + resGuardTaskName + "-->assembleTaskName:" + assembleTaskName

        String buildTaskName = TASK_PREFIX + taskName

        if (mProject.tasks.findByPath(buildTaskName) == null) {

            ProductFlavorsBuildApkTask buildTask = mProject.task(buildTaskName, type: ProductFlavorsBuildApkTask)

            if (configuration.andResGuard) {
                buildTask.dependsOn resGuardTaskName
            } else {
                buildTask.dependsOn assembleTaskName
            }
        }
    }

    boolean isSupportAndResGuard() {
        try {
            Class.forName(CLASS_AND_RES_GUARD_PLUGIN)
            return true
        } catch (Exception e) {
            mProject.logger.error(e.getMessage())
            return false
        }
    }
}