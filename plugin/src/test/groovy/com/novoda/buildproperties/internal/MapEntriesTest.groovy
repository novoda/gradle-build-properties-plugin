package com.novoda.buildproperties.internal

import com.novoda.buildproperties.Entry
import com.novoda.buildproperties.internal.MapEntries
import org.junit.Test

import static com.novoda.buildproperties.test.ExtendedTruth.assertThat

class MapEntriesTest {

    @Test
    void shouldNotContainUndefinedKey() {
        def entries = new MapEntries([:])

        assertThat(entries.contains('notThere')).isFalse()
    }

    @Test
    void shouldThrowWhenEvaluatingUndefinedKey() {
        def entries = new MapEntries([:])

        Entry entry = entries['notThere']

        assertThat(entry).willThrow(IllegalArgumentException)
    }

    @Test
    void shouldContainDefinedKey() {
        def entries = new MapEntries([foo: 'bar'])

        assertThat(entries.contains('foo')).isTrue()
    }

    @Test
    void shouldProvideValueForDefinedKey() {
        def entries = new MapEntries([foo: 'bar'])

        Entry entry = entries['foo']

        assertThat(entry).hasValue('bar')
    }

    @Test
    void shouldSupportKeysListing() {
        def entries = new MapEntries([foo: 'bar', x: 'y'])

        Enumeration<String> keys = entries.keys

        assertThat(Collections.list(keys)).containsAllOf('foo', 'x')
    }
}
