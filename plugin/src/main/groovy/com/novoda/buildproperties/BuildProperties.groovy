package com.novoda.buildproperties

import com.novoda.buildproperties.internal.*

class BuildProperties {

    private final String name
    private final ExceptionFactory exceptionFactory
    private final AdditionalMessageProvider additionalMessageProvider
    private Entries entries
    private BuildProperties fallback

    BuildProperties(String name) {
        this.name = name
        this.exceptionFactory = new DefaultExceptionFactory(name)
        this.additionalMessageProvider = new AdditionalMessageProvider()
    }

    String getName() {
        name
    }

    void using(Map<String, Object> map) {
        entries(new MapEntries(map, exceptionFactory, additionalMessageProvider))
    }

    void using(File file) {
        entries(FilePropertiesEntries.create(name ?: file.name, file, exceptionFactory, additionalMessageProvider))
    }

    void entries(Entries entries) {
        this.entries = entries
    }

    Enumeration<String> getKeys() {
        if (fallback == null) {
            return entries.keys
        }
        HashSet<String> keys = new HashSet<>()
        def enumeration = entries.keys
        while (enumeration.hasMoreElements()) {
            keys.add(enumeration.nextElement())
        }
        enumeration = fallback.keys
        while (enumeration.hasMoreElements()) {
            keys.add(enumeration.nextElement())
        }
        return Collections.enumeration(keys)
    }

    Entry getAt(String key) {
        if (fallback == null) {
            return entries.getAt(key)
        }
        return entries.getAt(key).or(fallback.getAt(key))
    }

    void setDescription(String description) {
        additionalMessageProvider.setAdditionalMessage(description)
    }

    void fallback(BuildProperties properties) {
        this.fallback = properties
    }
}
