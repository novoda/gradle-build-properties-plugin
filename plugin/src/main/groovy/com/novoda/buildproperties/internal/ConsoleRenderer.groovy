package com.novoda.buildproperties.internal

import org.gradle.internal.UncheckedException

class ConsoleRenderer {

    private final String indentationPrefix

    ConsoleRenderer(String indentationPrefix = '    ') {
        this.indentationPrefix = indentationPrefix
    }

    String indent(String text, String prefix = indentationPrefix) {
        "$prefix${text.replace('\n', "\n${prefix.inject('', { acc, val -> acc + ' ' })}")}"
    }

    String asClickableFileUrl(File path) {
        try {
            (new URI("file", "", path.toURI().getPath(), null, null)).toString()
        } catch (URISyntaxException var3) {
            throw UncheckedException.throwAsUncheckedException(var3);
        }
    }
}
