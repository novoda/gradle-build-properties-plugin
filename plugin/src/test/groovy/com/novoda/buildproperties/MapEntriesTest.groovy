package com.novoda.buildproperties

import org.junit.Test

import static com.novoda.buildproperties.test.ExtendedTruth.assertThat

class MapEntriesTest {

    @Test
    public void shouldNotContainUndefinedKey() {
        def entries = new MapEntries([:])

        assertThat(entries.contains('notThere')).isFalse()
    }

    @Test
    public void shouldThrowWhenEvaluatingUndefinedKey() {
        def entries = new MapEntries([:])

        Entry entry = entries['notThere']

        assertThat(entry).willThrow(IllegalArgumentException)
    }

    @Test
    public void shouldContainDefinedKey() {
        def entries = new MapEntries([foo: 'bar'])

        assertThat(entries.contains('foo')).isTrue()
    }

    @Test
    public void shouldProvideValueForDefinedKey() {
        def entries = new MapEntries([foo: 'bar'])

        Entry entry = entries['foo']

        assertThat(entry).hasValue('bar')
    }

    @Test
    public void shouldSupportKeysListing() {
        def entries = new MapEntries([foo: 'bar', x: 'y'])

        Enumeration<String> keys = entries.keys

        assertThat(Collections.list(keys)).containsAllOf('foo', 'x').inOrder()
    }
}
