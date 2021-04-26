package com.youga.apk;

import java.util.HashMap;
import java.util.Map;

public class Channel {

    public String name;
    public Map<String, String> manifestPlaceholders;

    public Channel(String name, Map<String, String> manifestPlaceholders) {
        this.name = name;
        this.manifestPlaceholders = manifestPlaceholders;
    }

    public static Channel create(String name, Map<String, Object> manifestPlaceholders) {
        Map<String, String> mp = new HashMap<>();
        for (String key : manifestPlaceholders.keySet()) {
            mp.put(key, manifestPlaceholders.get(key).toString());
        }
        return new Channel(name, mp);
    }

    @Override
    public String toString() {
        return "Channel{" +
                "name='" + name + '\'' +
                ", manifestPlaceholders=" + manifestPlaceholders +
                '}';
    }
}
