package com.novoda.buildproperties.internal

import com.novoda.buildproperties.Entries

import javax.inject.Provider

class MapEntries extends Entries {
    private final Map<String, Object> map
    private final ExceptionFactory exceptionFactory

    MapEntries(Map<String, Object> map,
               ExceptionFactory exceptionFactory,
               Provider<String> additionalMessageProvider) {
        super(additionalMessageProvider)
        this.exceptionFactory = exceptionFactory
        this.map = Collections.unmodifiableMap(map)
    }

    @Override
    boolean contains(String key) {
        map.containsKey(key)
    }

    @Override
    protected Object getValueAt(String key, String additionalMessage) {
        Object value = map.get(key)
        if (value != null) {
            return value
        }
        throw exceptionFactory.propertyNotFound(key)
    }

    @Override
    Enumeration<String> getKeys() {
        Collections.enumeration(map.keySet())
    }
}
