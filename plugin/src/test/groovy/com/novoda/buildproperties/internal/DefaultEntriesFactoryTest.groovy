package com.novoda.buildproperties.internal

import com.google.common.io.Resources
import com.novoda.buildproperties.Entries
import com.novoda.buildproperties.Entry
import com.novoda.buildproperties.ExceptionFactory
import org.gradle.api.logging.Logger
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

import static com.novoda.buildproperties.test.ExtendedTruth.assertThat
import static org.junit.Assert.fail

class DefaultEntriesFactoryTest {


    private static final File MORE_PROPERTIES = resourceFile('more.properties')
    public static final File NOT_THERE_PROPERTIES = new File('notThere.properties')

    @Mock private Logger logger
    private ExceptionFactory exceptionFactory
    private ConsoleRenderer consoleRenderer

    private DefaultEntriesFactory entriesFactory

    @Before
    void setUp() {
        consoleRenderer = new ConsoleRenderer()
        exceptionFactory = new DefaultExceptionFactory('foo', consoleRenderer)
        entriesFactory = new DefaultEntriesFactory(logger, exceptionFactory)
    }

    @Test
    void shouldNotThrowExceptionWhenCreatingEntriesFromNonExistentPropertiesFile() {
        Entries entries = entriesFactory.from(NOT_THERE_PROPERTIES)
    }

    @Test
    void shouldNotThrowExceptionWhenAccessingEntryFromNonExistentPropertiesFile() {
        Entries entries = entriesFactory.from(NOT_THERE_PROPERTIES)

        Entry entry = entries['x']
    }

    @Test
    void shouldThrowExceptionWhenEvaluatingEntryFromNonExistentPropertiesFile() {
        Entries entries = entriesFactory.from(NOT_THERE_PROPERTIES)

        try {
            entries['x'].string
            fail('Exception not thrown')
        } catch (Exception e) {
            assertThat(e.getMessage()).contains('notThere.properties does not exist.')
        }
    }

    @Test
    void shouldProvideSpecifiedErrorMessageWhenAccessingEntryFromNonExistentPropertiesFile() {
        def additionalMessage = 'This file should contain the following properties:\n- foo\n- bar'
        exceptionFactory.additionalMessage = additionalMessage
        Entries entries = entriesFactory.from(NOT_THERE_PROPERTIES)

        try {
            entries['x'].string
            fail('Exception not thrown')
        } catch (Exception e) {
            String message = e.getMessage()
            assertThat(message).contains('notThere.properties does not exist.')
            assertThat(message).contains(consoleRenderer.indent(additionalMessage, "* buildProperties.foo: "))
        }
    }

    @Test
    void shouldRecursivelyIncludeValuesFromSpecifiedFilesWhenIncludeProvided() {
        Entries entries = entriesFactory.from(MORE_PROPERTIES)

        def allValues = entries.asMap().values()*.string

        assertThat(allValues).containsExactly('qwerty', 'including.properties', 'android', '?????', 'false', 'hello world', '0.001', 'bar', 'asdfg', 'true', '123456')
    }

    @Test
    void shouldRecursivelyIncludeKeysFromSpecifiedFilesWhenIncludeProvided() {
        Entries entries = entriesFactory.from(MORE_PROPERTIES)

        def allKeys = entries.keys.toSet()

        assertThat(allKeys).containsExactly('aProperty', 'include', 'a', 'api.key', 'negative', 'string', 'double', 'foo', 'another_PROPERTY', 'positive', 'int')
    }

    private static File resourceFile(String file) {
        new File(Resources.getResource(file).toURI())
    }
}
