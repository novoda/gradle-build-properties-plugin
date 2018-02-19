package com.novoda.buildproperties

import com.novoda.buildproperties.internal.DefaultEntriesFactory
import com.novoda.buildproperties.internal.DefaultExceptionFactory
import org.gradle.api.logging.Logger
import org.junit.Before
import org.junit.Test

import static com.novoda.buildproperties.test.ExtendedTruth.assertThat
import static org.mockito.Mockito.mock

class EntriesChainTest {

    private static final Logger LOGGER = mock(Logger)
    private static final ExceptionFactory DEFAULT_EXCEPTION_FACTORY = new DefaultExceptionFactory('default')
    private static final DEFAULT_ENTRIES_FACTORY = new DefaultEntriesFactory(LOGGER, DEFAULT_EXCEPTION_FACTORY)
    private static final ExceptionFactory FALLBACK_EXCEPTION_FACTORY = new DefaultExceptionFactory('fallback')
    private static final FALLBACK_ENTRIES_FACTORY = new DefaultEntriesFactory(LOGGER, FALLBACK_EXCEPTION_FACTORY)
    private static final Map<String, Object> DEFAULT_MAP = [a: 'value_a', b: 'value_b', c: 'value_c', d: 'value_d']
    private static final Map<String, Object> FALLBACK_MAP = [d: 'none', e: 'value_e', f: 'value_f']
    private EntriesChain chain

    @Before
    void setUp() {
        chain = new EntriesChain(DEFAULT_ENTRIES_FACTORY, DEFAULT_MAP)
    }

    @Test
    void shouldContainAllValuesFromGivenMap() {
        def entries = chain

        assertThat(entries['a']).hasValue('value_a')
        assertThat(entries['b']).hasValue('value_b')
        assertThat(entries['c']).hasValue('value_c')
        assertThat(entries['d']).hasValue('value_d')
    }

    @Test
    void shouldContainAllKeysFromGivenMap() {
        def entries = chain

        assertThat(Collections.list(entries.keys)).containsExactly('a', 'b', 'c', 'd')
    }

    @Test
    void shouldIncludeValuesFromFallbackMap() {
        def entries = chain.or(FALLBACK_MAP)

        assertThat(entries['e']).hasValue('value_e')
        assertThat(entries['f']).hasValue('value_f')
    }

    @Test
    void shouldContainAllKeysFromBothMaps() {
        def entries = chain.or(FALLBACK_MAP)

        assertThat(Collections.list(entries.keys)).containsExactly('a', 'b', 'c', 'd', 'e', 'f')
    }

    @Test
    void shouldThrowExceptionWhenKeyNotFoundInAnyEntries() {
        try {
            def entries = chain.or(FALLBACK_MAP)
            entries['x'].string
        } catch (Exception e) {
            def propertyNotFound = DEFAULT_EXCEPTION_FACTORY.propertyNotFound('x')
            assertThat(e.message).isEqualTo(propertyNotFound.message)
        }
    }

    @Test
    void shouldMentionErrorMessageFromFallbackEntries() {
        def fallbackEntries = FALLBACK_ENTRIES_FACTORY.from(FALLBACK_MAP)
        try {
            def entries = chain.or(fallbackEntries)
            entries['x'].string
        } catch (Exception e) {
            assertThat(e.message).startsWith('Build properties evaluation failed:')
            assertThat(e.message).contains('Unable to find value for key \'x\' in properties set \'buildProperties.default\'.')
            assertThat(e.message).contains('Unable to find value for key \'x\' in properties set \'buildProperties.fallback\'.')
        }
    }
}
