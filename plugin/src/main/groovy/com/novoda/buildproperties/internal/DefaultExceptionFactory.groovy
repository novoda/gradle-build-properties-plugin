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
        BuildPropertiesException.from("Properties set 'buildProperties.$propertiesSetName' is defined upon ${consoleRenderer.asClickableFileUrl(file)}, but the file does not exist.", format(additionalMessage))
    }

    @Override
    Exception propertyNotFound(String key, String additionalMessage) {
        BuildPropertiesException.from("Properties set 'buildProperties.$propertiesSetName' has no value defined for key '$key'.", format(additionalMessage))
    }

    private String format(String additionalMessage) {
        String prefix = "* buildProperties.$propertiesSetName: "
        String indent = prefix.inject('', { acc, val -> acc + ' ' })
        additionalMessage ? "$prefix${additionalMessage.replace('\n', "\n$indent")}" : ''
    }
}
