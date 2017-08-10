package com.novoda.buildproperties.internal

class FilePropertiesEntries extends AbstractEntries {

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
            PropertiesProvider.create(file, exceptionFactory)
        }, additionalMessageProvider)
    }

    private FilePropertiesEntries(String name, Closure<PropertiesProvider> providerClosure, AdditionalMessageProvider additionalMessageProvider) {
        super(additionalMessageProvider)
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
    protected Object getValueAt(String key, String additionalMessage) {
        provider.getValueAt(key, additionalMessage)
    }

    @Override
    Enumeration<String> getKeys() {
        provider.keys
    }


    private static class PropertiesProvider {
        final File file
        final Properties properties
        final ExceptionFactory exceptionFactory
        final Set<String> keys

        static PropertiesProvider create(File file, ExceptionFactory exceptionFactory) {
            Properties properties = new Properties()
            properties.load(new FileInputStream(file))
            new PropertiesProvider(file, properties, exceptionFactory)
        }

        private PropertiesProvider(File file,
                                   Properties properties,
                                   ExceptionFactory exceptionFactory) {
            this.file = file
            this.properties = properties
            this.exceptionFactory = exceptionFactory
            this.keys = new HashSet<>(properties.stringPropertyNames())
        }

        boolean contains(String key) {
            properties[key] != null
        }

        Object getValueAt(String key, String additionalMessage) {
            Object value = properties[key]
            if (value != null) {
                return value
            }
            throw exceptionFactory.propertyNotFound(key, additionalMessage)
        }

        Enumeration<String> getKeys() {
            Collections.enumeration(keys)
        }
    }
}
