# Migrating from the old plugin
This plugin used to be called `com.novoda:build-properties-plugin`. If you are using such an old version, you will
likely need to perform a migration since there have been several breaking changes in the intervening releases.

You'll also notice that [the versioning](https://github.com/novoda/gradle-build-properties-plugin/releases) has been
restarted from 0.1, so that the older plugin's version may actually look newer than the ones we have right now. For
example, you may be using `com.novoda:build-properties-plugin:1.2.1`, but that is older than
`com.novoda:gradle-build-properties-plugin:0.3`.

This guide will help you migrate off of the old plugin and onto the new one.

## Step 1: change the plugin ID
The plugin identifier has changed from `com.novoda:build-properties-plugin` to
`com.novoda:gradle-build-properties-plugin`. Check what the latest version is on the
[releases tab](https://github.com/novoda/gradle-build-properties-plugin/releases).

This is basically the same first step as if you were adding the plugin from scratch to your project.
The [readme](../README.md) contains more information on how to add the plugin to your Gradle build.

Don't forget to remove or update any leftover references to the old plugin name.

## Step 2: update the `buildProperties` closure
In the `buildProperties` closure you need to replace all the `file` references to `using`:

```gradle
buildProperties {
    // Old version:
    application.file rootProject.file('properties/application.properties')

    // Will become:
    application.using rootProject.file('properties/application.properties')
}
```

If you're using environmental variables as fallback via the built-in `env` property, you'll notice that it does not
exist anymore. To keep using it, you'll need to add it explicitly to your `buildProperties`:

```gradle
buildProperties {
    env.using System.getenv()
}
```

## Step 3: switch the `resValueProperty` usages to the typed alternatives
If you use the `resValueProperty` to set Android resource values, you'll notice it's not available anymore. Not to
despair, it's only been replaced by typed alternatives:

 Resource type | Replace `resValueProperty` with
 --- | ---
 `boolean` | `resValueBoolean`
 `int` | `resValueInt`
 `string` | `resValueString`

## Step 4: switch the `buildConfigProperty` usages to the typed alternatives
If you use the `buildConfigProperty` to set Android `BuildConfig` fields, you'll notice it's not available anymore.
Not to despair, it's only been replaced by typed alternatives:

 `BuildConfig` field type | Replace `buildConfigProperty` with
 --- | ---
 `boolean` | `buildConfigBoolean`
 `double` | `buildConfigDouble`
 `int` | `buildConfigInt`
 `long` | `buildConfigLong`
 `String` | `buildConfigString`

## Step 5: fix `signingConfig`s
While you could once do:

```gradle
signingConfigs {
    release {
        signingConfigProperties buildProperties.releaseSigningConfig
    }
}
```

Now that is not possible anymore. The feature has been dropped as it was not flexible enough. Luckily it is
rather easy to bring back. Firstly, add this helper to your `android` closure:

```gradle
signingConfigs.all { signingConfig ->
    signingConfig.ext.from = { buildProperties ->
        signingConfig.storeFile teamPropsFile(buildProperties['storeFile'].string)
        signingConfig.storePassword buildProperties['storePassword'].string
        signingConfig.keyAlias buildProperties['keyAlias'].string
        signingConfig.keyPassword buildProperties['keyPassword'].string
    }
}
```

Then edit your `signingConfigs` closure to use the `from` method instead:

```gradle
signingConfigs {
    release.from releaseSigningConfig
}
```
