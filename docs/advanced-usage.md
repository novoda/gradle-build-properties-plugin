# Advanced configuration

The plugin supports a number of advanced behaviours. For example, it can be used to consume properties 
from various external sources, fallbacks can be configured in case a given source doesn't have a certain property, 
properties can be used in Android builds as `buildConfigField` and `resValue`.    

## Table of contents
 * Different property sources 
 * Custom property source
 * Fallback/Chaining mechanism
 * Android build configuration variables
 * Android resource variables
 * Android property backed flavors

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
