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
        switch (source) {
            case BuildProperties:
                return source.entries
            case Entries:
                return source as Entries
            case { it instanceof Map<String, Object> }:
                return new MapEntries(source, exceptionFactory)
            case File:
                return FilePropertiesEntries.create(source, exceptionFactory)
            default:
                throw new GradleException("Unsupported type of source (${source.class})")
        }
    }

    void setAdditionalMessage(String value) {
        exceptionFactory.additionalMessage = value
    }
}
