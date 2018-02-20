package com.novoda.buildproperties.internal

import com.novoda.buildproperties.Entries
import com.novoda.buildproperties.Entry

class LazyEntries extends Entries {

    private final Closure<Entries> entriesClosure

    LazyEntries(Closure<Entries> entriesClosure) {
        this.entriesClosure = entriesClosure.memoize()
    }

    protected Entries getEntries() {
        entriesClosure.call()
    }

    @Override
    boolean contains(String key) {
        entries.contains(key)
    }

    @Override
    Entry getAt(String key) {
        new Entry(key, {
            entries.getAt(key).getValue()
        })
    }

    @Override
    Enumeration<String> getKeys() {
        entries.keys
    }
}
