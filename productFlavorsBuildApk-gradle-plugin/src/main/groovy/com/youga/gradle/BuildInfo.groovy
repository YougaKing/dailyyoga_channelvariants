package com.youga.gradle

class BuildInfo {

    String flavorName

    Map<String, Object> manifestPlaceholders

    BuildInfo(String flavorName, Map<String, Object> manifestPlaceholders) {
        this.flavorName = flavorName
        this.manifestPlaceholders = manifestPlaceholders
    }

    String getFlavorName() {
        return flavorName
    }

    Map<String, Object> getManifestPlaceholders() {
        return manifestPlaceholders
    }


    @Override
    public String toString() {
        return "BuildInfo{" +
                "flavorName='" + flavorName + '\'' +
                ", manifestPlaceholders=" + manifestPlaceholders +
                '}';
    }
}