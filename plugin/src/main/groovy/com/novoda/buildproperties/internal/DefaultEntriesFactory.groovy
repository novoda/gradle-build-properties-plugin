package com.novoda.buildproperties.internal

import com.novoda.buildproperties.BuildProperties
import com.novoda.buildproperties.Entries
import com.novoda.buildproperties.ExceptionFactory
import org.gradle.api.GradleException
import org.gradle.api.logging.Logger

class DefaultEntriesFactory implements Entries.Factory {

    private final Logger logger
    private final ExceptionFactory exceptionFactory

    DefaultEntriesFactory(Logger logger, ExceptionFactory exceptionFactory) {
        this.logger = logger
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
                return new MapEntries(source as Map<String, Object>, exceptionFactory)
            case File:
                return new LazyEntries({ newFilePropertiesEntries(source as File) })
            default:
                throw new GradleException("Unsupported type of source (${source.class})")
        }
    }

    private Entries newFilePropertiesEntries(File file) {
        FilePropertiesEntries.create(file, exceptionFactory)
    }

    void setAdditionalMessage(String value) {
        exceptionFactory.additionalMessage = value
    }
}
