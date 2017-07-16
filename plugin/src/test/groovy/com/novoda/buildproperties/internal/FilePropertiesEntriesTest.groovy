package com.novoda.buildproperties.internal

import com.google.common.io.Resources
import com.novoda.buildproperties.Entry
import com.novoda.buildproperties.internal.FilePropertiesEntries
import org.gradle.api.GradleException
import org.junit.Before
import org.junit.Test

import static com.novoda.buildproperties.test.ExtendedTruth.assertThat
import static org.junit.Assert.fail

class FilePropertiesEntriesTest {

    private static final File PROPERTIES_FILE = new File(Resources.getResource('any.properties').toURI())

    private FilePropertiesEntries entries

    @Before
    void setUp() {
        entries = FilePropertiesEntries.create('any', PROPERTIES_FILE)
    }

    @Test
    void shouldNotAccessPropertyValueWhenGettingEntry() {
        try {
            Entry entry = entries['notThere']
        } catch (IllegalArgumentException ignored) {
            fail('Entry value should be evaluated lazily')
        }
    }

    @Test
    void shouldCheckPropertyExistenceInstantly() {
        assertThat(entries.contains('notThere')).isFalse()
        assertThat(entries.contains('aProperty')).isTrue()
    }

    @Test
    void shouldRetrieveValueWhenPropertyDefined() {
        def value = entries['aProperty'].string

        assertThat(value).isEqualTo('qwerty')
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenTryingToAccessValueOfUndefinedProperty() {
        try {
            entries['notThere'].string
            fail('IllegalArgumentException expected')
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).startsWith("No value defined for property 'notThere'")
        }
    }

    @Test
    void shouldConvertToTrueBooleanWhenPropertyDefinedAsTrue() {
        Boolean value = entries['positive'].boolean
        assertThat(value).isTrue()
    }

    @Test
    void shouldConvertToFalseWhenPropertyNotDefinedAsBoolean() {
        Boolean value = entries['string'].boolean
        assertThat(value).isFalse()
    }

    @Test
    void shouldThrowNumberFormatExceptionWhenPropertyNotDefinedAsInteger() {
        try {
            entries['string'].int
        } catch (NumberFormatException e) {
            assertThat(e.getMessage()).contains('"hello world"')
        }
    }

    @Test
    void shouldConvertToIntegerWhenPropertyDefinedAsInteger() {
        Integer value = entries['int'].int
        assertThat(value).isEqualTo(123456)
    }

    @Test
    void shouldThrowNumberFormatExceptionWhenPropertyNotDefinedAsDouble() {
        try {
            entries['string'].double
        } catch (NumberFormatException e) {
            assertThat(e.getMessage()).contains('"hello world"')
        }
    }

    @Test
    void shouldConvertToDoubleWhenPropertyDefinedAsDouble() {
        Double value = entries['double'].double
        assertThat(value).isEqualTo(0.001 as Double)
    }

    @Test
    void shouldEnumerateAllKeysInPropertiesFile() {
        assertThat(Collections.list(entries.keys)).containsExactly('aProperty', 'another_PROPERTY', 'api.key', 'negative', 'positive', 'int', 'double', 'string')
    }

    @Test
    void shouldRecursivelyIncludePropertiesFromSpecifiedFilesWhenIncludeProvided() {
        def moreEntries = FilePropertiesEntries.create('more', new File(Resources.getResource('more.properties').toURI()))
        def includingEntries = FilePropertiesEntries.create('including', new File(Resources.getResource('including.properties').toURI()))

        entries.keys.each { String key ->
            assertThat(moreEntries[key].string).isEqualTo(entries[key].string)
        }
        assertThat(moreEntries['foo'].string).isEqualTo(includingEntries['foo'].string)
        assertThat(moreEntries['a'].string).isEqualTo('android')
        assertThat(includingEntries['a'].string).isEqualTo('apple')
    }

    @Test
    void shouldThrowWhenAccessingPropertyFromNonExistentPropertiesFile() {
        entries = FilePropertiesEntries.create('notThere', new File('notThere.properties'))

        try {
            entries['any'].string
            fail('Gradle exception not thrown')
        } catch (GradleException e) {
            assertThat(e.getMessage()).endsWith('notThere.properties does not exist.')
        }
    }

    @Test
    void shouldProvideSpecifiedErrorMessageWhenAccessingPropertyFromNonExistentPropertiesFile() {
        def errorMessage = 'This file should contain the following properties:\n- foo\n- bar'
        entries = FilePropertiesEntries.create('notThere', new File('notThere.properties'), errorMessage)

        try {
            entries['any'].string
            fail('Gradle exception not thrown')
        } catch (GradleException e) {
            String message = e.getMessage()
            assertThat(message).contains('notThere.properties does not exist.')
            assertThat(message).endsWith(errorMessage)
        }
    }
}
