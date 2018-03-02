# gradle-build-properties-plugin
[![](https://ci.novoda.com/buildStatus/icon?job=gradle-build-properties-plugin)](https://ci.novoda.com/job/gradle-build-properties-plugin/lastSuccessfulBuild/console) [![](https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg)](LICENSE.txt) [![Bintray](https://api.bintray.com/packages/novoda/maven/gradle-build-properties-plugin/images/download.svg) ](https://bintray.com/novoda/maven/gradle-build-properties-plugin/_latestVersion)

External properties support for your build scripts.

## Description

Gradle builds are highly configurable through various properties. Rather than hardcoding these
properties in your build scripts, for security reasons and in order to increase modularity, it's a
common practice to provide these properties from external sources. 

This plugin aims to provide a simple way to:
- consume properties from external and internal sources like cli, system properties, files etc.
- define a custom source for properties
- configure Android build with external properties 


## Adding to your project

The plugin is deployed to Bintray's JCenter. Ensure it's correctly defined
as a dependency for your build script:

```gradle
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath 'com.novoda:gradle-build-properties-plugin:0.3'
  }
}
```
Then apply the plugin in your build script via:
```gradle
apply plugin: 'com.novoda.build-properties'
```

## Simple usage
Add a `buildProperties` configuration to your build script listing
all the properties files you intend to reference later on:
```gradle
buildProperties {
    secrets {
        file project.file('secrets.properties')
    }
}
```
where `secrets.properties` is a properties file that can now be referenced
in the build script as `buildProperties.secrets`. Entries in such file can be
accessed via the `getAt` operator:
```gradle
Entry entry = buildProperties.secrets['aProperty']
```

The value of an `Entry` can be retrieved via one of its typed accessors:

- `boolean enabled = buildProperties.secrets['x'].boolean`
- `int count = buildProperties.secrets['x'].int`
- `double rate = buildProperties.secrets['x'].double`
- `String label = buildProperties.secrets['x'].string`

It is important to note that values are lazy loaded too (via the internal closure provided in `Entry`).
Trying to access the value of a specific property could generate an exception
if the key is missing in the provided properties file, eg:
```
FAILURE: Build failed with an exception.

* What went wrong:
A problem occurred configuring project ':app'.
> No value defined for property 'notThere' in 'secrets' properties (/Users/toto/novoda/spikes/BuildPropertiesPlugin/sample/properties/secrets.properties)

```

## Features

#### Fallback support
If a property cannot be found an exception is thrown, it's possible to provide a fallback
value for a given `Entry` via the `or()` operator, defined as:

| | Example |
|----|----|
|another `Entry` | `buildProperties.secrets['notThere'].or(buildProperties.secrets['fallback'])` |
|a `Closure` | `buildProperties.secrets['notThere'].or({ Math.random() })` |
|a value | `buildProperties.secrets['notThere'].or('fallback')` |

If the whole fallback chain evaluation fails then a `CompositeException` is thrown listing all
the causes in the chain, eg:

```
A problem occurred while evaluating entry:
- exception message 1
- exception message 2
- exception message 3

```

#### Properties inheritance
It might be useful to have properties files that can recursively include
other properties files (specified via an `include` property).
Inherited properties can be overridden by the including set, just redefine
the property in the file and its value will be used instead of the one
from the included set.

For example, given a generic properties file `config.properties`:

```properties
foo=bar
aKey=aValue
```

you can override values and add additional ones in another properties file `debug.properties`:

```properties
include=/path/to/config.properties
aNewKey=aNewValue
aKey=overriddenPreviousValue
```

Then in your `build.gradle`:

```gradle
buildProperties {
    secrets {
        file rootProject.file('debug.properties')
    }
}

...

android {
    ...

    defaultConfig {
        ...
        buildConfigString 'FOO', buildProperties.secrets['foo'] // bar
        buildConfigString 'A_KEY', buildProperties.secrets['aKey'] // overriddenPreviousValue
        buildConfigString 'A_NEW_KEY', buildProperties.secrets['aNewKey'] // aNewValue
        ...
    }
}
```

#### More on loading properties
If the specified file is not found an exception is thrown at build time as soon as one of its properties is evaluated.
You can specify a custom error message to provide the user with more information, eg:
```gradle
buildProperties {
    secrets {
        file rootProject.file('secrets.properties'), '''
           This file should contain the following properties:
           - fabricApiKey: API key for Fabric
           - googleMapsApiKey: API key for Google Maps
        '''
    }
}
```


## Android-specific features

When applying the `gradle-build-properties-plugin` to an Android project you get access to an
 additional set of powerful features.

#### 1. Store a property value into your `BuildConfig`
In any product flavor configuration (or `defaultConfig`) you can use
`buildConfigString` as follows:
```gradle
    defaultConfig {
        ...
        buildConfigString 'API_KEY', buildProperties.secrets['apiKey']
        ...
    }
```

#### 2. Store a property value as generated string resource
In any product flavor configuration (or `defaultConfig`) you can use
`resValueProperty` as follows:

```gradle
    defaultConfig {
        ...
        resValueProperty 'api_key', buildProperties.secrets['apiKey']
        ...
    }
```

#### 3. Typed `buildConfigField` / `resValue`
The plugin enhances the `buildConfigField` and `resValue` facilities to
enforce types. To generate a string field in your `BuildConfig` you used to write:
```gradle
buildConfigField 'String', 'LOL', '\"sometimes the picture take\"'
```
but now you can instead write:
```gradle
buildConfigString 'LOL', 'sometimes the picture take'
```
The full list of new typed facilities is as follows:

| | Example |
|----|----|
|`buildConfigBoolean` | `buildConfigBoolean 'TEST_BOOLEAN', false`|
|`buildConfigInt` | `buildConfigInt 'TEST_INT', 42`|
|`buildConfigLong` | `buildConfigLong 'TEST_LONG', System.currentTimeMillis()`|
|`buildConfigDouble` | `buildConfigDouble 'TEST_DOUBLE', Math.PI`|
|`buildConfigString` | `buildConfigString 'TEST_STRING', 'whateva'`|
|`resValueInt`| `resValueInt 'debug_test_int', 100`|
|`resValueBoolean` | `resValueBoolean 'debug_test_bool', true`|
|`resValueString` | `resValueString 'debug_test_string', 'dunno bro...'`|
