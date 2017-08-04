package com.novoda.buildproperties

import javax.inject.Provider

abstract class Entries {

    private final Provider<String> additionalMessageProvider

    Entries(Provider<String> additionalMessageProvider) {
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
