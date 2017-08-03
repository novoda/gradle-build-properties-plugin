package com.novoda.buildproperties.internal

interface ExceptionFactory {

    Exception fileNotFound(File file, String additionalMessage)

    Exception propertyNotFound(String key, String additionalMessage)
}
