package com.novoda.buildproperties

class MapEntries extends Entries {
    private final Map<String, Object> map

    MapEntries(Map<String, Object> map) {
        this.map = Collections.unmodifiableMap(map)
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
        throw new IllegalArgumentException("No value defined for key '$key'")
    }

    @Override
    Enumeration<String> getKeys() {
        Collections.enumeration(map.keySet())
    }
}
