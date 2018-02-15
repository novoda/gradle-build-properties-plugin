package com.novoda.buildproperties

interface ExceptionFactory {

    Exception fileNotFound(File file)

    Exception propertyNotFound(String key)
}
