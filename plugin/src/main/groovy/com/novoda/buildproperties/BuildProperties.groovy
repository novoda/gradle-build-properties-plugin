package com.novoda.buildproperties

import com.novoda.buildproperties.internal.DefaultEntriesFactory
import com.novoda.buildproperties.internal.DefaultExceptionFactory
import org.gradle.api.Project
import org.gradle.api.logging.Logger

class BuildProperties {

    private final String name
    private final DefaultEntriesFactory factory
    private final Logger logger
    private EntriesChain chain

    BuildProperties(String name, Project project) {
        this.name = name
        this.logger = project.logger
        this.factory = new DefaultEntriesFactory(logger, new DefaultExceptionFactory(name))
    }

    String getName() {
        name
    }

    Entries getEntries() {
        chain
    }

    Enumeration<String> getKeys() {
        chain.keys
    }

    ExceptionFactory getExceptionFactory() {
        exceptionFactory
    }

    Entry getAt(String key) {
        chain.getAt(key)
    }

    Map<String, Entry> asMap() {
        entries.asMap()
    }

    boolean contains(String key) {
        chain.contains(key)
    }

    void setDescription(String description) {
        factory.additionalMessage = description
    }

    EntriesChain using(Map<String, Object> map) {
        newChain(map)
    }

    EntriesChain using(File file) {
        newChain(file)
    }

    EntriesChain using(Entries entries) {
        newChain(entries)
    }

    EntriesChain using(BuildProperties buildProperties) {
        newChain(buildProperties.entries)
    }

    EntriesChain using(Project project) {
        newChain(project)
    }

    EntriesChain file(File file, String description = '') {
        logger.warn("""/!\\ WARNING /!\\ Detected use of 'file' method to consume build properties from a file. 
                       |                 This api is deprecated and will be removed in an upcoming release, please use using() instead.
                       |                 For instance you could do:
                       |                 
                       |                    buildProperties {
                       |                        using secrets.properties
                       |                    }
                       |
                       |""".stripMargin())

        if (!description.isEmpty()) {
            setDescription description
        }
        newChain(file)
    }

    private EntriesChain newChain(def source) {
        chain = new EntriesChain(factory, source)
        return chain
    }
}
