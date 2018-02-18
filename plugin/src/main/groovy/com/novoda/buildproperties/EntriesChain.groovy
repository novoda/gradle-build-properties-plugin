package com.novoda.buildproperties

class EntriesChain implements Entries {

    private final Entries.Factory entriesFactory
    private final List<Entries> chain = []

    EntriesChain(Entries.Factory entriesFactory, def source) {
        this.entriesFactory = entriesFactory
        chain.add(entriesFactory.from(source))
    }

    EntriesChain or(def source) {
        chain.add(entriesFactory.from(source))
        return this
    }

    @Override
    boolean contains(String key) {
        chain.find { it.contains(key) } != null
    }

    @Override
    Entry getAt(String key) {
        chain.inject((Entry) null) { Entry result, Entries entries ->
            return result == null ? entries[key] : result.or(entries[key])
        }
    }

    @Override
    Enumeration<String> getKeys() {
        Set<String> allKeys = chain.collect { it.keys.toList() }.flatten().toSet()
        Collections.<String> enumeration(allKeys)
    }
}
