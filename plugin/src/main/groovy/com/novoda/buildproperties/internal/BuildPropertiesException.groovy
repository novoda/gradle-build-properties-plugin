package com.novoda.buildproperties.internal

import static java.util.Collections.emptyList
import static java.util.Collections.singletonList

class BuildPropertiesException extends Exception {

    private static final BuildPropertiesException EMPTY = new BuildPropertiesException(emptyList(), emptyList())

    private final List<Throwable> exceptions
    private final List<String> additionalMessages

    static BuildPropertiesException from(String message, String additionalMessage = '') {
        from(new BuildPropertiesException(new Exception(message), additionalMessage))
    }

    static BuildPropertiesException from(Exception exception) {
        return EMPTY.add(exception)
    }

    private BuildPropertiesException(Throwable exception, String additionalMessage) {
        this.exceptions = singletonList(exception)
        this.additionalMessages = additionalMessage?.trim()?.length() > 0 ? singletonList(additionalMessage) : emptyList()
    }

    private BuildPropertiesException(List<Throwable> exceptions, List<String> additionalMessages) {
        this.exceptions = Collections.unmodifiableList(exceptions)
        this.additionalMessages = Collections.unmodifiableList(additionalMessages)
    }

    BuildPropertiesException add(Throwable throwable) {
        List<Throwable> newExceptions = new ArrayList<>(exceptions)
        List<String> newAdditionalMessages = new ArrayList<>(additionalMessages)
        if (throwable instanceof BuildPropertiesException) {
            def other = (BuildPropertiesException) throwable
            newExceptions.addAll(removeDuplicateExceptions(message, other.exceptions))
            newAdditionalMessages.addAll(other.additionalMessages - additionalMessages)
        } else {
            newExceptions.addAll(removeDuplicateExceptions(message, [throwable]))
        }
        return new BuildPropertiesException(newExceptions, newAdditionalMessages)
    }

    private static List<Throwable> removeDuplicateExceptions(String currentMessage, List<Throwable> throwables) {
        throwables.findAll { !currentMessage.contains(it.message) }
    }

    List<Throwable> getExceptions() {
        exceptions
    }

    @Override
    String getMessage() {
        return exceptions
                .collect { it.message }
                .inject('Build properties evaluation failed:', { acc, val -> acc + "\n- $val" })
                .plus(additionalMessage)
    }

    String getAdditionalMessage() {
        if (additionalMessages.isEmpty()) {
            return ''
        }
        return additionalMessages
                .collect { it }
                .inject('\n\nAdditional info:', { acc, val -> acc + "\n$val" })
    }
}
