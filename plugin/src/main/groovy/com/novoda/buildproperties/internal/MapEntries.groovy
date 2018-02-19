package com.novoda.buildproperties.internal

import com.novoda.buildproperties.Entries
import com.novoda.buildproperties.Entry
import com.novoda.buildproperties.ExceptionFactory

class MapEntries extends Entries {
    private final Map<String, Object> map
    private final ExceptionFactory exceptionFactory

    MapEntries(Map<String, Object> map,
               ExceptionFactory exceptionFactory) {
        this.exceptionFactory = exceptionFactory
        this.map = Collections.unmodifiableMap(map)
    }

    @Override
    boolean contains(String key) {
        map.containsKey(key)
    }

    @Override
    Entry getAt(String key) {
        new Entry(key, {
            Object value = map.get(key)
            if (value != null) {
                return value
            }
            throw exceptionFactory.propertyNotFound(key)
        })
    }

    @Override
    Enumeration<String> getKeys() {
        Collections.enumeration(map.keySet())
    }
}
