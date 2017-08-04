package com.novoda.buildproperties.internal

class DefaultExceptionFactory implements ExceptionFactory {

    private final String propertiesSetName
    private final ConsoleRenderer consoleRenderer

    DefaultExceptionFactory(String propertiesSetName, ConsoleRenderer consoleRenderer = new ConsoleRenderer()) {
        this.propertiesSetName = propertiesSetName
        this.consoleRenderer = consoleRenderer
    }

    @Override
    Exception fileNotFound(File file, String additionalMessage) {
        BuildPropertiesException.from("Unable to create properties set 'buildProperties.$propertiesSetName': ${consoleRenderer.asClickableFileUrl(file)} does not exist.", format(additionalMessage))
    }

    @Override
    Exception propertyNotFound(String key, String additionalMessage) {
        BuildPropertiesException.from("Unable to find value for key '$key' in properties set 'buildProperties.$propertiesSetName'.", format(additionalMessage))
    }

    private String format(String additionalMessage) {
        String prefix = "* buildProperties.$propertiesSetName: "
        String indent = prefix.inject('', { acc, val -> acc + ' ' })
        additionalMessage ? "$prefix${additionalMessage.replace('\n', "\n$indent")}" : ''
    }
}
