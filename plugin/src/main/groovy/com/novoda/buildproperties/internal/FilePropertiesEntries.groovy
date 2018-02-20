package com.novoda.buildproperties.internal

import com.novoda.buildproperties.Entries
import com.novoda.buildproperties.Entry
import com.novoda.buildproperties.ExceptionFactory

class FilePropertiesEntries extends Entries {

    private final Properties entries
    private final ExceptionFactory exceptionFactory

    static FilePropertiesEntries create(File file,
                                        ExceptionFactory exceptionFactory) {
        if (!file.exists()) {
            throw exceptionFactory.fileNotFound(file)
        }
        Properties properties = new Properties()
        properties.load(new FileInputStream(file))
        new FilePropertiesEntries(properties, exceptionFactory)
    }

    private FilePropertiesEntries(Properties entries, ExceptionFactory exceptionFactory) {
        this.entries = entries
        this.exceptionFactory = exceptionFactory
    }

    @Override
    boolean contains(String key) {
        entries[key] != null
    }

    @Override
    Entry getAt(String key) {
        return new Entry(key, {
            Object value = entries[key]
            if (value != null) {
                return value
            }
            throw exceptionFactory.propertyNotFound(key)
        })
    }

    @Override
    Enumeration<String> getKeys() {
        Collections.enumeration(entries.stringPropertyNames())
    }
}
