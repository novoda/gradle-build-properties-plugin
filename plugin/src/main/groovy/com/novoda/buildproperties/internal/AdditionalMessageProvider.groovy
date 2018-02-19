package com.novoda.buildproperties.internal

class AdditionalMessageProvider {
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
}
