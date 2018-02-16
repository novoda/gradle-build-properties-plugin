package com.novoda.buildproperties.internal

import com.novoda.buildproperties.Entries
import com.novoda.buildproperties.Entry
import com.novoda.buildproperties.ExceptionFactory

class FilePropertiesEntries implements Entries {

    private final Closure<PropertiesProvider> providerClosure

    static FilePropertiesEntries create(File file,
                                        ExceptionFactory exceptionFactory) {
        new FilePropertiesEntries({
            if (!file.exists()) {
                throw exceptionFactory.fileNotFound(file)
            }
            PropertiesProvider.create(file, exceptionFactory)
        })
    }

    private FilePropertiesEntries(Closure<PropertiesProvider> providerClosure) {
        super()
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
    Entry getAt(String key) {
        new Entry(key, {
            provider.getValueAt(key)
        })
    }

    @Override
    Enumeration<String> getKeys() {
        provider.keys
    }

    @Override
    Map<String, Entry> asMap() {
        return null
    }

    private static class PropertiesProvider {
        final File file
        final Properties properties
        final PropertiesProvider defaults
        final ExceptionFactory exceptionFactory
        final Set<String> keys

        static PropertiesProvider create(File file, ExceptionFactory exceptionFactory) {
            Properties properties = new Properties()
            properties.load(new FileInputStream(file))

            PropertiesProvider defaults = null
            String include = properties['include']
            if (include != null) {
                defaults = create(new File(file.parentFile, include), exceptionFactory)
            }
            new PropertiesProvider(file, properties, defaults, exceptionFactory)
        }

        private PropertiesProvider(File file,
                                   Properties properties,
                                   PropertiesProvider defaults,
                                   ExceptionFactory exceptionFactory) {
            this.file = file
            this.properties = properties
            this.defaults = defaults
            this.exceptionFactory = exceptionFactory
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
            throw exceptionFactory.propertyNotFound(key)
        }

        Enumeration<String> getKeys() {
            Collections.enumeration(keys)
        }
    }
}
