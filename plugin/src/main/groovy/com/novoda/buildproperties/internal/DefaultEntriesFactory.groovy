package com.novoda.buildproperties.internal

import com.novoda.buildproperties.BuildProperties
import com.novoda.buildproperties.Entries
import com.novoda.buildproperties.EntriesChain
import com.novoda.buildproperties.ExceptionFactory
import org.gradle.api.GradleException
import org.gradle.api.Project
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
            case Project:
                return ProjectPropertiesEntries.from(source as Project, exceptionFactory)
            default:
                throw new GradleException("Unsupported type of source (${source.class})")
        }
    }

    private Entries newFilePropertiesEntries(File file) {
        def entries = FilePropertiesEntries.create(file, exceptionFactory)
        if (!entries.contains('include')) {
            return entries
        }
        def includeFile = new File(file.parentFile, entries['include'].string)
        if (!includeFile.exists()) {
            return entries
        }
        logger.warn("""/!\\ WARNING /!\\ Detected use of 'include' property to model inheritance between properties files. 
                       |                 This feature is deprecated and will be removed in an upcoming release, please use or() operator instead.
                       |                 For instance you could do:
                       |                 
                       |                    buildProperties {
                       |                        using(file('$file.name')).or(file('$includeFile.name'))
                       |                    }
                       |
                       |""".stripMargin())
        new EntriesChain(this, entries).or(from(includeFile))
    }

    void setAdditionalMessage(String value) {
        exceptionFactory.additionalMessage = value
    }
}
