package com.novoda.buildproperties.internal

interface ExceptionFactory {

    void setAdditionalMessage(String additionalMessage)

    Exception fileNotFound(File file)

    Exception propertyNotFound(String key)
}
