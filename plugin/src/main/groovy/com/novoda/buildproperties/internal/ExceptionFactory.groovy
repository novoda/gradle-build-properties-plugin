package com.novoda.buildproperties.internal

interface ExceptionFactory {

    Exception fileNotFound(File file)

    Exception propertyNotFound(String key)
}
