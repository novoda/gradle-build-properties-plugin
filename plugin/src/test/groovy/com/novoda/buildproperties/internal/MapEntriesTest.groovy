package com.novoda.buildproperties.internal

import com.novoda.buildproperties.Entry
import org.junit.Test

import static com.novoda.buildproperties.test.ExtendedTruth.assertThat

class MapEntriesTest {

    @Test
    void shouldNotContainUndefinedKey() {
        def entries = givenMapEntries([:])

        assertThat(entries.contains('notThere')).isFalse()
    }

    @Test
    void shouldThrowExceptionWhenEvaluatingUndefinedKey() {
        def entries = givenMapEntries([:])

        Entry entry = entries['notThere']

        assertThat(entry).willThrow(Exception)
    }

    @Test
    void shouldContainDefinedKey() {
        def entries = givenMapEntries([foo: 'bar'])

        assertThat(entries.contains('foo')).isTrue()
    }

    @Test
    void shouldProvideValueForDefinedKey() {
        def entries = givenMapEntries([foo: 'bar'])

        Entry entry = entries['foo']

        assertThat(entry).hasValue('bar')
    }

    @Test
    void shouldSupportKeysListing() {
        def entries = givenMapEntries([foo: 'bar', x: 'y'])

        Enumeration<String> keys = entries.keys

        assertThat(Collections.list(keys)).containsAllOf('foo', 'x')
    }

    private static MapEntries givenMapEntries(Map<String, Object> properties) {
        new MapEntries(properties, new DefaultExceptionFactory('foo'), new AdditionalMessageProvider())
    }
}
