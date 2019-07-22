package com.youga.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.dsl.CoreSigningConfig
import com.android.build.gradle.internal.dsl.ProductFlavor
import com.android.utils.FileUtils
import com.youga.apk.Channel
import com.youga.apk.InputParam
import com.youga.manifest.Main
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import static com.youga.gradle.ProductFlavorsBuildApkPlugin.TASK_PREFIX

class ProductFlavorsBuildApkTask extends DefaultTask {

    static final String AND_RES_GUARD_DIR_PREFIX = "AndResGuard_"
    static final String AND_RES_GUARD_FILE_SUFFIX = "_7zip_aligned_unsigned.apk"

    AppExtension android
    ProductFlavorsBuildApkExtension configuration
    File outputFile
    def buildInfoList = []
    CoreSigningConfig apkSigningConfig
    File parentDir

    ProductFlavorsBuildApkTask() {

        description = 'Multi-Product-Flavors-Build-Apk'
        group = 'productFlavorsBuildApk'
        android = project.extensions.android
        configuration = project.productFlavorsBuildApk

        android.productFlavors.all { ProductFlavor flavor ->

            String flavorName = flavor.name.capitalize()
            Map<String, Object> manifestPlaceholders = flavor.manifestPlaceholders

            if (manifestPlaceholders.size() > 0) {
                buildInfoList << new BuildInfo(
                        flavorName,
                        manifestPlaceholders
                )
            }
        }

        android.applicationVariants.all { ApplicationVariantImpl variant ->
            variant.outputs.each { ApkVariantOutputImpl output ->

                String variantName = this.name[TASK_PREFIX.length()..-1]

                if (variantName.equalsIgnoreCase(variant.buildType.name as String)
                        || isTargetFlavor(variantName, variant.productFlavors, variant.buildType.name)) {

                    try {
                        if (variant.metaClass.respondsTo(variant, "getPackageApplicationProvider")) {
                            outputFile = new File(variant.packageApplicationProvider.get().outputDirectory, output.outputFileName)
                        }
                    } catch (Exception ignore) {
                        // no-op
                    } finally {
                        outputFile = outputFile ?: output.outputFile
                    }

                    apkSigningConfig = variant.variantData.variantConfiguration.signingConfig
                }
            }
        }

        createExcludeFlavorTask(name.replaceAll(TASK_PREFIX, ""))

        println name
    }

    void createExcludeFlavorTask(String taskName) {
//        configuration.excludeFlavor.each { String flavor ->
//            String excludeTaskName = taskName.replaceAll(configuration.originFlavor, flavor.capitalize())
//
//            String resGuardTaskName = RES_GUARD_TASK_PREFIX + excludeTaskName
//            String assembleTaskName = ASSEMBLE_TASK_PREFIX + excludeTaskName
//
//            println "excludeResGuardTaskName:" + resGuardTaskName + "-->excludeAssembleTaskName:" + assembleTaskName
//
//            Task task
//            if (configuration.andResGuard) {
//                task = project.task(resGuardTaskName)
//            } else {
//                task = project.task(assembleTaskName)
//            }
//            println "task:" + task
//            task.doLast(new Action<Task>() {
//                @Override
//                void execute(Task t) {
//                    copyExcludeFlavorTaskApk(t.name, flavor)
//                }
//            })
//
//            task.mustRunAfter name
//        }
    }

    @TaskAction
    run() {
        if (outputFile == null) return

        parentDir = outputFile.parentFile
        println "parentDir:" + parentDir.absolutePath
        File compileApk = findCompileApk()
        println "compileApk:" + compileApk.absolutePath

        File originApkDir = new File(parentDir.absolutePath + "/" + TASK_PREFIX)
        originApkDir.mkdirs()
        println "originApkDir:" + originApkDir.absolutePath
        String originApkName = compileApk.name
        println "originApkName:" + originApkName
        File originApk = new File(originApkDir.absolutePath, originApkName)
        println "originApk:" + originApk.absolutePath

        FileUtils.copyFile(compileApk, originApk)

        List<Channel> channelList = new ArrayList<>()
        Channel originChannel = null

        buildInfoList.each { BuildInfo buildInfo ->
            String flavorName = buildInfo.flavorName
            Map<String, Object> manifestPlaceholders = buildInfo.manifestPlaceholders

            boolean isExclude = isExcludeFlavor(flavorName)
            println isExclude

            if (!isExclude) {
                println "flavorName:" + flavorName + "-->manifestPlaceholders:" + manifestPlaceholders
                channelList.add(Channel.create(flavorName, manifestPlaceholders))
            }
            if (flavorName.equalsIgnoreCase(configuration.originFlavor)) {
                originChannel = Channel.create(flavorName, manifestPlaceholders)
            }
        }

        if (originChannel == null) {
            project.logger.error "originFlavor cannot null"
            return
        }

        InputParam.Builder builder = new InputParam.Builder()
                .setOriginApk(originApk)
                .setOriginChannel(originChannel)
                .setChannelList(channelList)
                .setSignFile(apkSigningConfig.storeFile)
                .setStorePassword(apkSigningConfig.storePassword)
                .setKeyAlias(apkSigningConfig.keyAlias)
                .setKeyPassword(apkSigningConfig.keyPassword)

        Main.gradleRun(builder.create())
    }

    File findCompileApk() {
        if (configuration.andResGuard) {
            String outputFileName = outputFile.name.substring(0, outputFile.name.indexOf(".apk"))
            println "outputFileName:" + outputFileName

            File andResGuardApk = new File(parentDir.absolutePath + "/" + AND_RES_GUARD_DIR_PREFIX + outputFileName + "/" + outputFileName + AND_RES_GUARD_FILE_SUFFIX)
            println "andResGuardApk:" + andResGuardApk.absolutePath
            return andResGuardApk
        } else {
            return outputFile
        }
    }

    boolean isExcludeFlavor(String flavorName) {
        Iterator<String> iterator = configuration.excludeFlavor.iterator()
        while (iterator.hasNext()) {
            String excludeFlavor = iterator.next()
            if (flavorName.equalsIgnoreCase(excludeFlavor)) {
                println "excludeFlavor:" + excludeFlavor + "-->flavorName:" + flavorName
                return true
            }
        }
        return false
    }

    void copyExcludeFlavorTaskApk(String taskName, String excludeFlavor) {
//        println "excludeFlavorTaskName" + taskName
//        String replaceTaskName = name.replaceAll(TASK_PREFIX, "")
//
//        String flavorBuildType = ""
//        if (buildApkExtension.andResGuard) {
//            flavorAndBuildType = taskName.replaceAll(RES_GUARD_TASK_PREFIX, "")
//        } else {
//            flavorBuildType = taskName.replaceAll(ASSEMBLE_TASK_PREFIX, "")
//        }
//        String dirName = parentDir.absolutePath.replaceAll(flavorBuildType, replaceTaskName)
//        File excludeFlavorDir = new File(dirName)
//        println "excludeFlavorDir" + excludeFlavorDir.absolutePath
//
//        if (buildApkExtension.andResGuard) {
//            String prefixFileName = outputFile.name.substring(0, outputFile.name.indexOf("."))
//            String excludeFlavorFileName = prefixFileName.replaceAll(buildApkExtension.originFlavor, excludeFlavor.capitalize())
//
//            File excludeFlavorFile = new File(excludeFlavorDir.absolutePath + "/" + AND_RES_GUARD_DIR_PREFIX + excludeFlavorFileName + "/" + excludeFlavorFileName + AND_RES_GUARD_FILE_SUFFIX)
//            println "excludeFlavorFile" + excludeFlavorFile
//        } else {
//            String excludeFlavorFileName = outputFile.name.replaceAll(buildApkExtension.originFlavor, excludeFlavor.capitalize())
//            File excludeFlavorFile = new File(excludeFlavorDir, excludeFlavorFileName)
//            println "excludeFlavorFile" + excludeFlavorFile
//        }
    }


    static boolean isTargetFlavor(variantName, flavors, String buildType) {
        if (flavors.size() > 0) {
            String flavor = flavors.get(0).name
            return variantName.equalsIgnoreCase(flavor) || variantName.equalsIgnoreCase([flavors.collect {
                it.name
            }.join(""), buildType].join(""))
        }
        return false
    }
}