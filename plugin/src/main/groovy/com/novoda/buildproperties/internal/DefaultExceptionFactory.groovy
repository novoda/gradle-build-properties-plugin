package com.novoda.buildproperties.internal

import com.novoda.buildproperties.CompositeException

class DefaultExceptionFactory implements ExceptionFactory {

    private final String propertiesSetName
    private final ConsoleRenderer consoleRenderer
    private String additionalMessage

    DefaultExceptionFactory(String propertiesSetName, ConsoleRenderer consoleRenderer = new ConsoleRenderer()) {
        this.propertiesSetName = propertiesSetName
        this.consoleRenderer = consoleRenderer
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
        additionalMessage ? "\n${consoleRenderer.indent(additionalMessage)}\n" : '\n'
    }
}
