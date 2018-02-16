package com.novoda.buildproperties.internal

import com.novoda.buildproperties.Entries
import com.novoda.buildproperties.Entry
import com.novoda.buildproperties.ExceptionFactory

class MapEntries implements Entries {
    private final Map<String, Object> map
    private final ExceptionFactory exceptionFactory

    MapEntries(Map<String, Object> map,
               ExceptionFactory exceptionFactory) {
        super()
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

    @Override
    Map<String, Entry> asMap() {
        Map<String, Entry> entryMap = map.keySet().collectEntries {
            [(it): this[it]]
        }
        Collections.unmodifiableMap(entryMap)
    }
}
