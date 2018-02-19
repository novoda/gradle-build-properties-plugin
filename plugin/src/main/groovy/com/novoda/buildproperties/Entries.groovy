package com.novoda.buildproperties

abstract class Entries {

    abstract boolean contains(String key)

    abstract Entry getAt(String key)

    abstract Enumeration<String> getKeys()

    final Map<String, Entry> asMap() {
        Map<String, Entry> entryMap = keys.toList().collectEntries {
            [(it): this[it]]
        }
        Collections.unmodifiableMap(entryMap)
    }
}
