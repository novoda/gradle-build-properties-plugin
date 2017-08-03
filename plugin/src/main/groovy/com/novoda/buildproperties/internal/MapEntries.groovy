package com.novoda.buildproperties.internal

import com.novoda.buildproperties.Entries

class MapEntries extends Entries {
    private final Map<String, Object> map
    private final ExceptionFactory exceptionFactory
    private final AdditionalMessageProvider additionalMessageProvider

    MapEntries(Map<String, Object> map,
               ExceptionFactory exceptionFactory,
               AdditionalMessageProvider additionalMessageProvider) {
        this.exceptionFactory = exceptionFactory
        this.map = Collections.unmodifiableMap(map)
        this.additionalMessageProvider = additionalMessageProvider
    }

    @Override
    boolean contains(String key) {
        map.containsKey(key)
    }

    @Override
    protected Object getValueAt(String key) {
        Object value = map.get(key)
        if (value != null) {
            return value
        }
        throw exceptionFactory.propertyNotFound(key, additionalMessageProvider.additionalMessage)
    }

    @Override
    Enumeration<String> getKeys() {
        Collections.enumeration(map.keySet())
    }
}
