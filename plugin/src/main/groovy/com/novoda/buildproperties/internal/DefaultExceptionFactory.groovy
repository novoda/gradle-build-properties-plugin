package com.novoda.buildproperties.internal

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
        BuildPropertiesException.from("Properties set 'buildProperties.$propertiesSetName' is defined upon ${consoleRenderer.asClickableFileUrl(file)}, but the file does not exist.", formattedAdditionalMessage())
    }

    @Override
    Exception propertyNotFound(String key) {
        BuildPropertiesException.from("Properties set 'buildProperties.$propertiesSetName' has no value defined for key '$key'.", formattedAdditionalMessage())
    }

    private String formattedAdditionalMessage() {
        String prefix = "* buildProperties.$propertiesSetName: "
        String indent = prefix.inject('', { acc, val -> acc + ' ' })
        additionalMessage ? "$prefix${additionalMessage.replace('\n', "\n$indent")}" : ''
    }
}
