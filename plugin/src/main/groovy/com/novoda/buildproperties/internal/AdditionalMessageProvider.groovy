package com.novoda.buildproperties.internal

import javax.inject.Provider

class AdditionalMessageProvider implements Provider<String> {
    private String additionalMessage = ''

    void setAdditionalMessage(String value) {
        if (value == null) {
            additionalMessage = ''
        } else {
            additionalMessage = value
        }
    }

    String getAdditionalMessage() {
        additionalMessage
    }

    @Override
    String get() {
        additionalMessage
    }
}
