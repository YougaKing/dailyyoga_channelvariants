package com.dailyyoga.plugin.channelvariants.util

import org.gradle.api.Project

class Utils {

    static final String PLUGIN_APPLICATION = "com.android.application"
    static final String PLUGIN_ANDRESGUARD = "AndResGuard"


    static hasAndroidPlugin(Project project) {
        return project.plugins.hasPlugin(PLUGIN_APPLICATION);
    }

    static hasAndResGuardPlugin(Project project) {
        return project.plugins.hasPlugin(PLUGIN_ANDRESGUARD);
    }
}