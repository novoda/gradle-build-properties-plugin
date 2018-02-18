package com.novoda.buildproperties.internal

import com.novoda.buildproperties.BuildProperties
import com.novoda.buildproperties.Entries
import com.novoda.buildproperties.ExceptionFactory
import org.gradle.api.GradleException

class DefaultEntriesFactory implements Entries.Factory {

    private final ExceptionFactory exceptionFactory

    DefaultEntriesFactory(ExceptionFactory exceptionFactory) {
        this.exceptionFactory = exceptionFactory
    }

    @Override
    Entries from(def source) {
        if (source instanceof BuildProperties) {
            return source.entries
        } else if (source instanceof Entries) {
            return source as Entries
        } else if (source instanceof Map<String, Object>) {
            return new MapEntries(source, exceptionFactory)
        } else if (source instanceof File) {
            return FilePropertiesEntries.create(source, exceptionFactory)
        } else {
            throw new GradleException("Unsupported type of source (${source.class})")
        }
    }

    void setAdditionalMessage(String value) {
        exceptionFactory.additionalMessage = value
    }
}
