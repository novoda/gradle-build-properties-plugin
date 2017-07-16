package com.novoda.buildproperties.internal

import com.novoda.buildproperties.Entries
import org.gradle.api.GradleException

class FilePropertiesEntries extends Entries {

    private final String name
    private final Closure<PropertiesProvider> providerClosure

    static FilePropertiesEntries create(String name, File file, String errorMessage = null) {
        new FilePropertiesEntries(name, {
            if (!file.exists()) {
                throw new GradleException("File $file.name does not exist.${errorMessage ? "\n$errorMessage" : ''}")
            }
            PropertiesProvider.create(file)
        })
    }

    private FilePropertiesEntries(String name, Closure<PropertiesProvider> providerClosure) {
        this.name = name
        this.providerClosure = providerClosure.memoize()
    }

    private PropertiesProvider getProvider() {
        providerClosure.call()
    }

    @Override
    boolean contains(String key) {
        provider.contains(key)
    }

    @Override
    protected Object getValueAt(String key) {
        provider.getValueAt(key)
    }

    @Override
    Enumeration<String> getKeys() {
        provider.keys
    }

    private static class PropertiesProvider {
        final File file
        final Properties properties
        final PropertiesProvider defaults
        final Set<String> keys

        static PropertiesProvider create(File file) {
            Properties properties = new Properties()
            properties.load(new FileInputStream(file))

            PropertiesProvider defaults = null
            String include = properties['include']
            if (include != null) {
                defaults = create(new File(file.parentFile, include))
            }
            new PropertiesProvider(file, properties, defaults)
        }

        private PropertiesProvider(File file, Properties properties, PropertiesProvider defaults) {
            this.file = file
            this.properties = properties
            this.defaults = defaults
            this.keys = new HashSet<>(properties.stringPropertyNames())
            if (defaults != null) {
                this.keys.addAll(defaults.keys)
            }
        }

        boolean contains(String key) {
            properties[key] != null || defaults?.contains(key)
        }

        Object getValueAt(String key) {
            Object value = properties[key]
            if (value != null) {
                return value
            }
            if (defaults?.contains(key)) {
                return defaults.getValueAt(key)
            }
            throw new IllegalArgumentException("No value defined for property '$key' in properties file $file.absolutePath")
        }

        Enumeration<String> getKeys() {
            Collections.enumeration(keys)
        }
    }

}
