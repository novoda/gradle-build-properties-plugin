package com.novoda.buildproperties.internal

import com.novoda.buildproperties.Entries
import com.novoda.buildproperties.Entry

import javax.inject.Provider

abstract class AbstractEntries implements Entries {

    private final Provider<String> additionalMessageProvider

    AbstractEntries(Provider<String> additionalMessageProvider) {
        this.additionalMessageProvider = additionalMessageProvider
    }

    abstract boolean contains(String key)

    protected abstract Object getValueAt(String key, String additionalMessage)

    Entry getAt(String key) {
        new Entry(key, {
            getValueAt(key, additionalMessage)
        })
    }

    abstract Enumeration<String> getKeys()

    final String getAdditionalMessage() {
        additionalMessageProvider.get()
    }
}
