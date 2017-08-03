package com.novoda.buildproperties.internal

import com.novoda.buildproperties.Entries

class FilePropertiesEntries extends Entries {

    private final String name
    private final Closure<PropertiesProvider> providerClosure

    static FilePropertiesEntries create(String name,
                                        File file,
                                        ExceptionFactory exceptionFactory,
                                        AdditionalMessageProvider additionalMessageProvider) {
        new FilePropertiesEntries(name, {
            if (!file.exists()) {
                throw exceptionFactory.fileNotFound(file, additionalMessageProvider.additionalMessage)
            }
            PropertiesProvider.create(file, exceptionFactory, additionalMessageProvider)
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
        final ExceptionFactory exceptionFactory
        final AdditionalMessageProvider additionalMessageProvider
        final Set<String> keys

        static PropertiesProvider create(File file, ExceptionFactory exceptionFactory, AdditionalMessageProvider additionalMessageProvider) {
            Properties properties = new Properties()
            properties.load(new FileInputStream(file))

            PropertiesProvider defaults = null
            String include = properties['include']
            if (include != null) {
                defaults = create(new File(file.parentFile, include), exceptionFactory, additionalMessageProvider)
            }
            new PropertiesProvider(file, properties, defaults, exceptionFactory, additionalMessageProvider)
        }

        private PropertiesProvider(File file,
                                   Properties properties,
                                   PropertiesProvider defaults,
                                   ExceptionFactory exceptionFactory,
                                   AdditionalMessageProvider additionalMessageProvider) {
            this.file = file
            this.properties = properties
            this.defaults = defaults
            this.exceptionFactory = exceptionFactory
            this.additionalMessageProvider = additionalMessageProvider
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
            throw exceptionFactory.propertyNotFound(key, additionalMessageProvider.additionalMessage)
        }

        Enumeration<String> getKeys() {
            Collections.enumeration(keys)
        }
    }
}
