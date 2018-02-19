package com.novoda.buildproperties.internal

import com.google.common.io.Resources
import org.junit.Before
import org.junit.Test

import static com.novoda.buildproperties.test.ExtendedTruth.assertThat
import static org.junit.Assert.fail

class FilePropertiesEntriesTest {

    private static final File ANY_PROPERTIES = resourceFile('any.properties')
    private static final File NOT_THERE_PROPERTIES = new File('notThere.properties')
    private DefaultExceptionFactory exceptionFactory
    private AdditionalMessageProvider additionalMessageProvider

    private FilePropertiesEntries entries

    @Before
    void setUp() {
        additionalMessageProvider = new AdditionalMessageProvider()
        exceptionFactory = new DefaultExceptionFactory('foo')
        entries = FilePropertiesEntries.create(ANY_PROPERTIES, exceptionFactory)
    }

    @Test
    void shouldNotAccessPropertyValueWhenGettingEntry() {
        try {
            entries['notThere']
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
            assertThat(e.getMessage()).contains("Unable to find value for key 'notThere'")
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
        def moreEntries = FilePropertiesEntries.create(resourceFile('more.properties'), exceptionFactory)
        def includingEntries = FilePropertiesEntries.create(resourceFile('including.properties'), exceptionFactory)

        entries.keys.each { String key ->
            assertThat(moreEntries[key].string).isEqualTo(entries[key].string)
        }
        assertThat(moreEntries['foo'].string).isEqualTo(includingEntries['foo'].string)
        assertThat(moreEntries['a'].string).isEqualTo('android')
        assertThat(includingEntries['a'].string).isEqualTo('apple')
    }

    @Test
    void shouldThrowExceptionWhenCreatingEntriesFromNonExistentPropertiesFile() {
        try {
            entries = FilePropertiesEntries.create(NOT_THERE_PROPERTIES, exceptionFactory)
            fail('Exception not thrown')
        } catch (Exception e) {
            assertThat(e.getMessage()).contains('notThere.properties does not exist.')
        }
    }

    @Test
    void shouldProvideSpecifiedErrorMessageWhenCreatingEntriesFromNonExistentPropertiesFile() {
        def additionalMessage = 'This file should contain the following properties:\n- foo\n- bar'
        def consoleRenderer = new ConsoleRenderer()
        exceptionFactory.additionalMessage = additionalMessage

        try {
            entries = FilePropertiesEntries.create(NOT_THERE_PROPERTIES, exceptionFactory)
            fail('Exception not thrown')
        } catch (Exception e) {
            String message = e.getMessage()
            assertThat(message).contains('notThere.properties does not exist.')
            assertThat(message).contains(consoleRenderer.indent(additionalMessage, "* buildProperties.foo: "))
        }
    }

    @Test
    void shouldProvideEntriesAsMap() {
        def map = entries.asMap()

        assertThat(map).containsKey('aProperty')
        assertThat(map['aProperty']).hasValue('qwerty')
    }

    @Test
    void shouldIterateOverValues() {
        def values = entries.asMap().values()*.string

        assertThat(values).containsExactly(
                'qwerty',
                'asdfg',
                '?????',
                'false',
                'true',
                '123456',
                '0.001',
                'hello world'
        )
    }

    private static File resourceFile(String file) {
        new File(Resources.getResource(file).toURI())
    }
}
