package com.novoda.buildproperties.internal

import org.gradle.internal.UncheckedException

class ConsoleRenderer {

    private final String indentationPrefix

    ConsoleRenderer(String indentationPrefix = '    ') {
        this.indentationPrefix = indentationPrefix
    }

    String indent(String text) {
        "$indentationPrefix${text.replace('\n', "\n$indentationPrefix")}"
    }

    String asClickableFileUrl(File path) {
        try {
            (new URI("file", "", path.toURI().getPath(), null, null)).toString()
        } catch (URISyntaxException var3) {
            throw UncheckedException.throwAsUncheckedException(var3);
        }
    }
}
