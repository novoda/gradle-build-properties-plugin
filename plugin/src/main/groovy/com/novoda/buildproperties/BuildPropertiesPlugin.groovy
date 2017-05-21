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

        android.signingConfigs.all {
            addSigningConfigSupportTo(it)
        }
    }

    private static void addBuildConfigSupportTo(target) {
        def buildConfigField = { String type, String name, Closure<String> value ->
            target.buildConfigField type, name, value()
        }

        target.ext.buildConfigBoolean = { String name, def value ->
            buildConfigField('boolean', name, {
                if (value instanceof Entry) {
                    return value.boolean
                }
                return Boolean.toString(value)
            })
        }
        target.ext.buildConfigInt = { String name, def value ->
            buildConfigField('int', name, {
                if (value instanceof Entry) {
                    return value.int
                }
                return Integer.toString(value)
            })
        }
        target.ext.buildConfigLong = { String name, def value ->
            buildConfigField('long', name, {
                if (value instanceof Entry) {
                    return value.long
                }
                return "${Long.toString(value)}L"
            })
        }
        target.ext.buildConfigDouble = { String name, def value ->
            buildConfigField('double', name, {
                if (value instanceof Entry) {
                    return value.double
                }
                return Double.toString(value)
            })
        }
        target.ext.buildConfigString = { String name, def value ->
            buildConfigField('String', name, {
                if (value instanceof Entry) {
                    return "\"$value.string\""
                }
                return "\"$value\""
            })
        }
    }

    private static void addResValueSupportTo(target) {
        def resValue = { String type, String name, Closure<String> value ->
            target.resValue type, name, value()
        }
        target.ext.resValueBoolean = { String name, def value ->
            resValue('bool', name, {
                if (value instanceof Entry) {
                    return value.boolean
                }
                return Boolean.toString(value)
            })
        }
        target.ext.resValueInt = { String name, def value ->
            resValue('integer', name, {
                if (value instanceof Entry) {
                    return value.int
                }
                return Integer.toString(value)
            })
        }
        target.ext.resValueString = { String name, def value ->
            resValue('string', name, {
                if (value instanceof Entry) {
                    return "\"$value.string\""
                }
                return "\"$value\""
            })
        }
    }

    private static void addSigningConfigSupportTo(target) {
        target.ext.signingConfigProperties = { BuildProperties buildProperties ->
            target.storeFile new File(buildProperties.parentFile, buildProperties['storeFile'].string)
            target.storePassword buildProperties['storePassword'].string
            target.keyAlias buildProperties['keyAlias'].string
            target.keyPassword buildProperties['keyPassword'].string
        }
    }

}
