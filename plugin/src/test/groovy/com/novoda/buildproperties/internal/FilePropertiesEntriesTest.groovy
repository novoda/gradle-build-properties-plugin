package com.novoda.buildproperties.internal

import com.google.common.io.Resources
import com.novoda.buildproperties.Entry
import org.junit.Before
import org.junit.Test

import static com.novoda.buildproperties.test.ExtendedTruth.assertThat
import static org.junit.Assert.fail

class FilePropertiesEntriesTest {

    private static final File PROPERTIES_FILE = new File(Resources.getResource('any.properties').toURI())

    private ExceptionFactory exceptionFactory
    private AdditionalMessageProvider additionalMessageProvider
    
    private FilePropertiesEntries entries

    @Before
    void setUp() {
        additionalMessageProvider = new AdditionalMessageProvider()
        exceptionFactory = new DefaultExceptionFactory('foo')
        entries = FilePropertiesEntries.create('any', PROPERTIES_FILE, exceptionFactory, additionalMessageProvider)
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
    void shouldThrowExceptionWhenTryingToAccessValueOfUndefinedProperty() {
        try {
            entries['notThere'].string
            fail('Exception expected')
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("no value defined for key 'notThere'")
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
        def moreEntries = FilePropertiesEntries.create('more', new File(Resources.getResource('more.properties').toURI()), exceptionFactory, additionalMessageProvider)
        def includingEntries = FilePropertiesEntries.create('including', new File(Resources.getResource('including.properties').toURI()), exceptionFactory, additionalMessageProvider)

        entries.keys.each { String key ->
            assertThat(moreEntries[key].string).isEqualTo(entries[key].string)
        }
        assertThat(moreEntries['foo'].string).isEqualTo(includingEntries['foo'].string)
        assertThat(moreEntries['a'].string).isEqualTo('android')
        assertThat(includingEntries['a'].string).isEqualTo('apple')
    }

    @Test
    void shouldThrowExceptionWhenAccessingPropertyFromNonExistentPropertiesFile() {
        entries = FilePropertiesEntries.create('notThere', new File('notThere.properties'), exceptionFactory, additionalMessageProvider)

        try {
            entries['any'].string
            fail('Exception not thrown')
        } catch (Exception e) {
            assertThat(e.getMessage()).contains('the file does not exist.')
        }
    }

    @Test
    void shouldProvideSpecifiedErrorMessageWhenAccessingPropertyFromNonExistentPropertiesFile() {
        def additionalMessage = 'This file should contain the following properties:\n- foo\n- bar'
        def consoleRenderer = new ConsoleRenderer()
        additionalMessageProvider.additionalMessage = additionalMessage
        entries = FilePropertiesEntries.create('notThere', new File('notThere.properties'), exceptionFactory, additionalMessageProvider)

        try {
            entries['any'].string
            fail('Exception not thrown')
        } catch (Exception e) {
            String message = e.getMessage()
            assertThat(message).contains('the file does not exist.')
            assertThat(message).contains(consoleRenderer.indent(additionalMessage, "* buildProperties.foo: "))
        }
    }
}
