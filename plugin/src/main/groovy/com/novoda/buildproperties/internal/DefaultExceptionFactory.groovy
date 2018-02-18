package com.novoda.buildproperties.internal

import com.novoda.buildproperties.ExceptionFactory

class DefaultExceptionFactory extends ExceptionFactory {

    private final String propertiesSetName
    private final ConsoleRenderer consoleRenderer

    DefaultExceptionFactory(String propertiesSetName, ConsoleRenderer consoleRenderer = new ConsoleRenderer()) {
        this.propertiesSetName = propertiesSetName
        this.consoleRenderer = consoleRenderer
    }

    @Override
    Exception fileNotFound(File file) {
        String message = "Unable to create properties set 'buildProperties.$propertiesSetName': ${consoleRenderer.asClickableFileUrl(file)} does not exist."
        BuildPropertiesException.from(message, format(additionalMessage))
    }

    @Override
    Exception propertyNotFound(String key) {
        String message = "Unable to find value for key '$key' in properties set 'buildProperties.$propertiesSetName'."
        BuildPropertiesException.from(message, format(additionalMessage))
    }

    private String format(String additionalMessage) {
        String prefix = "* buildProperties.$propertiesSetName: "
        String indent = prefix.inject('', { acc, val -> acc + ' ' })
        additionalMessage ? "$prefix${additionalMessage.replace('\n', "\n$indent")}" : ''
    }
}
