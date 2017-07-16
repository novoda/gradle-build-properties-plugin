package com.novoda.buildproperties.internal

class PropertyNotFoundException extends IllegalArgumentException {

    PropertyNotFoundException(String key) {
        super("No property defined with key '$key'")
        //super("No value defined for property '$key' in properties file $file.absolutePath")
    }
}
