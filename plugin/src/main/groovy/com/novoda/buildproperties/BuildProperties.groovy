package com.novoda.buildproperties

import com.novoda.buildproperties.internal.FilePropertiesEntries
import com.novoda.buildproperties.internal.MapEntries

class BuildProperties {

    private final String name
    private Entries entries

    BuildProperties(String name) {
        this.name = name
    }

    String getName() {
        name
    }

    void from(Map<String, Object> map) {
        entries(new MapEntries(map))
    }

    void file(File file, String errorMessage = null) {
        entries(FilePropertiesEntries.create(name ?: file.name, file, errorMessage))
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
}
