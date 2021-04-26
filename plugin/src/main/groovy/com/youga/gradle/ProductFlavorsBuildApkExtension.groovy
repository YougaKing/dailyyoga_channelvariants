package com.youga.gradle


class ProductFlavorsBuildApkExtension {

    String originFlavor
    Iterable<String> excludeFlavor
    boolean andResGuard

    String getOriginFlavor() {
        return originFlavor
    }

    Iterable<String> getExcludeFlavor() {
        return excludeFlavor
    }

    boolean getAndResGuard() {
        return andResGuard
    }


    @Override
    public String toString() {
        return "ProductFlavorsBuildApkExtension{" +
                "originFlavor='" + originFlavor + '\'' +
                ", excludeFlavor=" + excludeFlavor +
                ", andResGuard=" + andResGuard +
                '}';
    }
}