package com.novoda.buildproperties.internal

import org.gradle.internal.UncheckedException

class ConsoleRenderer {

    String asClickableFileUrl(File path) {
        try {
            (new URI("file", "", path.toURI().getPath(), null, null)).toString()
        } catch (URISyntaxException var3) {
            throw UncheckedException.throwAsUncheckedException(var3);
        }
    }
}
