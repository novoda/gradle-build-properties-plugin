# Advanced configuration

The plugin supports a number of advanced behaviours. For example, it can be used to consume properties 
from various external sources, fallbacks can be configured in case a given source doesn't have a certain property and 
properties can be used in Android builds as `buildConfigField` and `resValue`.   

For most features the sample project, which can be found on the [github repo](https://github.com/novoda/gradle-build-properties-plugin/tree/master/sample),
provides fully working examples.  

## Table of contents
 * [Read properties from different sources](#reading-properties-from-different-sources) 
 * [Define custom property source](#define-custom-property-source)
 * [Fallback support](#fallback-support)
 * [Map properties to Android Gradle plugin](#map-properties-to-android-gradle-plugin)
 * [Define Android product flavors using properties](#define-android-product-flavors-using-properties)
 
## Reading properties from different sources

The plugin comes with built in support for various sources for properties, that can be configured within the 
`buildProperties` closure.

### Files

Properties can be consumed from one or multiple files:

```gradle
dev {
    using rootProject.file('dev.properties')
}

production {
    using rootProject.file('production.properties')
}
``` 

### Project properties

Properties can be consumed from the project properties: 

```gradle
cli {
    using project
}
```

Project properties can be either either passed via the command line or the build script itself:

`gradlew build -Papi_key=12345`

```gradle
ext {
    api_key=12345
}
```

### System properties

Properties can be consumed from the system properties:

```gradle
env {
    using System.getenv()
}
```

## Define custom property source

Besides the built in sources for properties, the plugin also provides an API to create custom sources.
Custom sources need to extend the `Entries` and can be provided via `BuildProperties.entries(Entries entries)`. For example, 
a custom implementation could consume properties from a web resource.

```gradle
api {
    using customApiEntries()
}
```
 

## Fallback support
If a property cannot be found an exception is thrown, it's possible to provide a fallback.

### Fallback Entry

A fallback value for a given `Entry` via the `or()` operator can be defined as:

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

### Fallback Entries

Besides providing a fallback `Entry`, also another `Entries` source can be declared as fallback: 

```gradle
files {
    using(file('$file.name')).or(file('$includeFile.name'))
}    
```

## Map properties to Android Gradle plugin

Using the plugin, properties can be mapped as `buildConfigField` and `resValue` to the Gradle Android plugin.
Besides that it enhances these facilities by enforcing types.

```gradle
buildProperties {
    api {
            file project.file('../properties/api_secrets.properties')
    }
}

android {
    ...

    defaultConfig {
        buildConfigString 'CLIENT_ID', buildProperties.api['api_client_id']
        buildConfigString 'CLIENT_SECRET', buildProperties.api['api_client_secret']
        ...
```

To generate a string field in your BuildConfig you used to write:

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


## Define Android product flavors using properties

Using the plugin properties can be also injected into other gradle plugin extensions. 

For example, Android `productFlavors` can be easily configured by first creating a method that configures a product flavor from 
given properties:

```gradle

productFlavors.all { flavor ->
    flavor.ext.from = { flavorProperties ->
        println ">>> Configuring '${flavor.name}' flavor using '${flavorProperties.name}' build properties"
        flavor.applicationId flavorProperties['applicationId'].string
        flavor.versionCode flavorProperties['versionCode'].int
        flavor.versionName flavorProperties['versionName'].string
        .
        .
        .
    }
}
```

And then using it to configure the `productFlavors`: 

```gradle

productFlavors {
    dev {
        from buildProperties.dev
    }
    production {
        from buildProperties.prod
    }
}
```
