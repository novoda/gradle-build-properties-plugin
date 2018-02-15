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

    void using(Map<String, Object> map) {
        entries(new MapEntries(map, exceptionFactory))
    }

    void using(File file) {
        entries(FilePropertiesEntries.create(file, exceptionFactory))
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
        exceptionFactory.additionalMessage = description
    }
}
