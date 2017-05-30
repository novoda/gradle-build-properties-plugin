package com.novoda.buildproperties

class EnvironmentPropertiesEntries extends Entries {

    @Override
    boolean contains(String key) {
        System.getenv().containsKey(key)
    }

    @Override
    protected Object getValueAt(String key) {
        String envValue = System.getenv(key)
        if (envValue != null) {
            return envValue
        }
        throw new IllegalArgumentException("No environment variable defined for key '$key'")
    }

    @Override
    Enumeration<String> getKeys() {
        Collections.enumeration(System.getenv().keySet())
    }

}
