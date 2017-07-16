package com.novoda.buildproperties.internal

class DefaultExceptionFactory implements ExceptionFactory {

    final String additionalMessage
    final ConsoleRenderer consoleRenderer = new ConsoleRenderer()

    DefaultExceptionFactory(String additionalMessage = null) {
        this.additionalMessage = additionalMessage
    }

    @Override
    Exception fileNotFound(File file) {
        new FileNotFoundException("File ${consoleRenderer.asClickableFileUrl(file)} does not exist.${formattedAdditionalMessage()}")
    }

    @Override
    Exception propertyNotFound(String key) {
        new PropertyNotFoundException(key)
    }

    private String formattedAdditionalMessage() {
        additionalMessage ? "\n$additionalMessage" : ''
    }
}
