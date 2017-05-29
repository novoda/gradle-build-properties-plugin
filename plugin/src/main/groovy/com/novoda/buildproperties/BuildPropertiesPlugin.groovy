package com.novoda.buildproperties

import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Plugin
import org.gradle.api.Project

class BuildPropertiesPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def container = project.container(BuildProperties, new NamedDomainObjectFactory<BuildProperties>() {
            @Override
            BuildProperties create(String name) {
                return new BuildProperties(name, project)
            }
        })
        container.create('env').entries(new EnvironmentPropertiesEntries(project))
        project.extensions.add('buildProperties', container)

        project.plugins.withId('com.android.application') {
            amendAndroidExtension(project)
        }
        project.plugins.withId('com.android.library') {
            amendAndroidExtension(project)
        }
    }

    private static void amendAndroidExtension(Project project) {
        def android = project.extensions.findByName("android")
        android.defaultConfig.with {
            addBuildConfigSupportTo(it)
            addResValueSupportTo(it)
        }

        android.productFlavors.all {
            addBuildConfigSupportTo(it)
            addResValueSupportTo(it)
        }

        android.buildTypes.all {
            addBuildConfigSupportTo(it)
            addResValueSupportTo(it)
        }
    }

    private static void addBuildConfigSupportTo(target) {
        def buildConfigField = { String type, String name, Closure<String> value ->
            target.buildConfigField type, name, value()
        }

        target.ext.buildConfigBoolean = { String name, def value ->
            buildConfigField('boolean', name, {
                return Boolean.toString(value instanceof Entry ? value.boolean : value)
            })
        }
        target.ext.buildConfigInt = { String name, def value ->
            buildConfigField('int', name, {
                return Integer.toString(value instanceof Entry ? value.int : value)
            })
        }
        target.ext.buildConfigLong = { String name, def value ->
            buildConfigField('long', name, {
                return "${Long.toString(value instanceof Entry ? value.long : value)}L"
            })
        }
        target.ext.buildConfigDouble = { String name, def value ->
            buildConfigField('double', name, {
                return Double.toString(value instanceof Entry ? value.double : value)
            })
        }
        target.ext.buildConfigString = { String name, def value ->
            buildConfigField('String', name, {
                return "\"${value instanceof Entry ? value.string : value}\""
            })
        }
    }

    private static void addResValueSupportTo(target) {
        def resValue = { String type, String name, Closure<String> value ->
            target.resValue type, name, value()
        }
        target.ext.resValueBoolean = { String name, def value ->
            resValue('bool', name, {
                return Boolean.toString(value instanceof Entry ? value.boolean : value)
            })
        }
        target.ext.resValueInt = { String name, def value ->
            resValue('integer', name, {
                return Integer.toString(value instanceof Entry ? value.int : value)
            })
        }
        target.ext.resValueString = { String name, def value ->
            resValue('string', name, {
                return "\"${value instanceof Entry ? value.string : value}\""
            })
        }
    }
}
