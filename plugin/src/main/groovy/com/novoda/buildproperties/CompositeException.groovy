package com.novoda.buildproperties

import static java.util.Collections.emptyList
import static java.util.Collections.singletonList

class CompositeException extends Exception {

    public static final CompositeException EMPTY = new CompositeException(emptyList(), emptyList())

    private final List<Throwable> exceptions
    private final List<String> additionalMessages

    static CompositeException from(String message, String additionalMessage = '') {
        from(new CompositeException(new Exception(message), additionalMessage))
    }

    static CompositeException from(Throwable throwable) {
        return EMPTY.add(throwable)
    }

    private CompositeException(Throwable exception, String additionalMessage) {
        this.exceptions = singletonList(exception)
        this.additionalMessages = additionalMessage?.trim()?.length() > 0 ? singletonList(additionalMessage) : emptyList()
    }

    private CompositeException(List<Throwable> exceptions, List<String> additionalMessages) {
        this.exceptions = Collections.unmodifiableList(exceptions)
        this.additionalMessages = Collections.unmodifiableList(additionalMessages)
    }

    CompositeException add(Throwable throwable) {
        List<Throwable> newExceptions = new ArrayList<>(exceptions)
        List<String> newAdditionalMessages = new ArrayList<>(additionalMessages)
        if (throwable instanceof CompositeException) {
            newExceptions.addAll(((CompositeException) throwable).exceptions)
            newAdditionalMessages.addAll(((CompositeException) throwable).additionalMessages)
        } else {
            newExceptions.add(throwable)
        }
        return new CompositeException(newExceptions, newAdditionalMessages)
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
