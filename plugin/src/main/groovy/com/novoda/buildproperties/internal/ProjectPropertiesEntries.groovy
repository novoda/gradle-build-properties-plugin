package com.novoda.buildproperties.internal

import com.novoda.buildproperties.Entries
import com.novoda.buildproperties.Entry
import com.novoda.buildproperties.ExceptionFactory
import org.gradle.api.Project

class ProjectPropertiesEntries extends Entries {

    private final Project project
    private final ExceptionFactory exceptionFactory

    static ProjectPropertiesEntries from(Project project, ExceptionFactory exceptionFactory) {
        return new ProjectPropertiesEntries(project, exceptionFactory)
    }

    private ProjectPropertiesEntries(Project project, ExceptionFactory exceptionFactory) {
        this.project = project
        this.exceptionFactory = exceptionFactory
    }

    @Override
    boolean contains(String key) {
        return project.hasProperty(key)
    }

    @Override
    Entry getAt(String key) {
        return new Entry(key, {
            if (project.hasProperty(key)) {
                return project.property(key)
            }
            throw exceptionFactory.propertyNotFound(key)
        })
    }


    @Override
    Enumeration<String> getKeys() {
        return Collections.enumeration(project.properties.keySet())
    }
}
