package com.novoda.buildproperties.internal

class ExceptionFactory {

    final String additionalMessage
    final ConsoleRenderer consoleRenderer = new ConsoleRenderer()

    ExceptionFactory(String additionalMessage) {
        this.additionalMessage = additionalMessage
    }

    FileNotFoundException fileNotFound(File file) {
        new FileNotFoundException("File ${consoleRenderer.asClickableFileUrl(file)} does not exist.${formattedAdditionalMessage()}")
    }

    PropertyNotFoundException propertyNotFound(String key) {
        new PropertyNotFoundException(key)
    }

    private String formattedAdditionalMessage() {
        additionalMessage ? "\n$additionalMessage" : ''
    }
}
