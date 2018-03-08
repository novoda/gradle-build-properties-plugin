# gradle-build-properties-plugin
[![](https://ci.novoda.com/buildStatus/icon?job=gradle-build-properties-plugin)](https://ci.novoda.com/job/gradle-build-properties-plugin/lastSuccessfulBuild/console) [![](https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg)](LICENSE.txt) [![Bintray](https://api.bintray.com/packages/novoda/maven/gradle-build-properties-plugin/images/download.svg) ](https://bintray.com/novoda/maven/gradle-build-properties-plugin/_latestVersion)

External properties support for your gradle builds.

**Using the old `com.novoda:build-properties-plugin`?** You should [migrate away](docs/migrating-from-old-plugin.md)
from it as soon as possible.

## Description

Gradle builds are highly configurable through various properties. Rather than hardcoding these
properties in your build scripts, for security reasons and in order to increase modularity, it's a
common practice to provide these properties from external sources. 

This plugin aims to provide a simple way to:
- consume properties from external and internal sources like project properties, system properties, files etc.
- define a custom source for properties
- configure Android build with external properties 

## Adding to your project

Apply the plugin from jCenter as a classpath dependency:

```gradle
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath 'com.novoda:gradle-build-properties-plugin:0.4'
  }
}

apply plugin: 'com.novoda.build-properties'
```

or from the Gradle Plugins Repository:

```gradle
plugins {
    id 'com.novoda.build-properties' version '0.4'
}
```

## Simple usage
Add a `buildProperties` configuration to your build script listing
all the properties files you intend to reference later on:
```gradle
buildProperties {
    secrets {
        using project.file('secrets.properties')
    }
}
```
where `secrets.properties` is a properties file containing key/value pairs:
```gradle
secret_key=12345
foo=bar
```
that can now be referenced in the build script as `buildProperties.secrets`:
```gradle
boolean enabled = buildProperties.secrets['a'].boolean
int count = buildProperties.secrets['b'].int
double rate = buildProperties.secrets['c'].double
String label = buildProperties.secrets['d'].string
```

It is important to note that values are lazy loaded too. Trying to access the value of a specific property 
could generate an exception if the key is missing in the provided properties file, e.g.:
```
FAILURE: Build failed with an exception.

* What went wrong:
A problem occurred configuring project ':app'.
> No value defined for property 'notThere' in 'secrets' properties (/Users/toto/novoda/spikes/BuildPropertiesPlugin/sample/properties/secrets.properties)

```

## Advanced usage

For more advanced configurations, please refer to the [advanced usage](docs/advanced-usage.md).

The latest Groovydoc can be found [here](https://novoda.github.io/gradle-build-properties-plugin/docs/0.4/).
