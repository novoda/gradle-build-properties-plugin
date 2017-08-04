package com.novoda.buildproperties

import com.novoda.buildproperties.internal.AdditionalMessageProvider
import com.novoda.buildproperties.internal.DefaultExceptionFactory
import com.novoda.buildproperties.internal.ExceptionFactory
import com.novoda.buildproperties.internal.FilePropertiesEntries
import com.novoda.buildproperties.internal.MapEntries

class BuildProperties {

    private final String name
    private final ExceptionFactory exceptionFactory
    private final AdditionalMessageProvider additionalMessageProvider
    private Entries entries

    BuildProperties(String name) {
        this.name = name
        this.exceptionFactory = new DefaultExceptionFactory(name)
        this.additionalMessageProvider = new AdditionalMessageProvider()
    }

    String getName() {
        name
    }

    void from(Map<String, Object> map) {
        entries(new MapEntries(map, exceptionFactory, additionalMessageProvider))
    }

    void from(File file) {
        entries(FilePropertiesEntries.create(name ?: file.name, file, exceptionFactory, additionalMessageProvider))
    }

    void entries(Entries entries) {
        this.entries = entries
    }

    Enumeration<String> getKeys() {
        entries.keys
    }

    Entry getAt(String key) {
        entries.getAt(key)
    }

    void setDescription(String description) {
        additionalMessageProvider.setAdditionalMessage(description)
    }
}
