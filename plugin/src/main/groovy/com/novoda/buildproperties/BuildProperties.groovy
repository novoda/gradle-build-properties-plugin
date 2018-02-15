package com.novoda.buildproperties

import com.novoda.buildproperties.internal.DefaultExceptionFactory
import com.novoda.buildproperties.internal.ExceptionFactory
import com.novoda.buildproperties.internal.FilePropertiesEntries
import com.novoda.buildproperties.internal.MapEntries

class BuildProperties {

    private final String name
    private final ExceptionFactory exceptionFactory
    private Entries entries

    BuildProperties(String name) {
        this.name = name
        this.exceptionFactory = new DefaultExceptionFactory(name)
    }

    String getName() {
        name
    }

    Entries getEntries() {
        entries
    }

    Enumeration<String> getKeys() {
        entries.keys
    }

    ExceptionFactory getExceptionFactory() {
        exceptionFactory
    }

    Entry getAt(String key) {
        entries.getAt(key)
    }

    void setDescription(String description) {
        exceptionFactory.additionalMessage = description
    }

    void using(Map<String, Object> map) {
        using(new MapEntries(map, exceptionFactory))
    }

    void using(File file) {
        using(FilePropertiesEntries.create(file, exceptionFactory))
    }

    void using(Entries entries) {
        this.entries = entries
    }
}
