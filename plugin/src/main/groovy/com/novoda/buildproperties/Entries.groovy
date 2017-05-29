package com.novoda.buildproperties

abstract class Entries {

    abstract boolean contains(String key)

    protected abstract Object getValueAt(String key)

    Entry getAt(String key) {
        new Entry(key, {
            getValueAt(key)
        })
    }

    abstract Enumeration<String> getKeys()
}
