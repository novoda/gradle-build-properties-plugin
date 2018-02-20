package com.novoda.buildproperties

import com.novoda.buildproperties.internal.AdditionalMessageProvider

abstract class ExceptionFactory extends AdditionalMessageProvider {

    abstract Exception fileNotFound(File file)

    abstract Exception propertyNotFound(String key)
}
