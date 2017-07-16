package com.novoda.buildproperties.internal

import com.novoda.buildproperties.CompositeException

class DefaultExceptionFactory implements ExceptionFactory {

    final ConsoleRenderer consoleRenderer = new ConsoleRenderer()
    private final String propertiesSetName
    private String additionalMessage

    DefaultExceptionFactory(String propertiesSetName) {
        this.propertiesSetName = propertiesSetName
    }

    @Override
    void setAdditionalMessage(String additionalMessage) {
        this.additionalMessage = additionalMessage
    }

    @Override
    Exception fileNotFound(File file) {
        CompositeException.from("File ${consoleRenderer.asClickableFileUrl(file)} does not exist.${formattedAdditionalMessage()}")
    }

    @Override
    Exception propertyNotFound(String key) {
        CompositeException.from("No property defined with key '$key' in properties set '$propertiesSetName'.${formattedAdditionalMessage()}")
    }

    private String formattedAdditionalMessage() {
        additionalMessage ? "\n$additionalMessage" : ''
    }
}
