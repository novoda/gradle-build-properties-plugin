package com.novoda.buildproperties.test

import com.google.common.annotations.GwtIncompatible
import com.google.common.base.Optional
import com.google.common.collect.*
import com.google.common.truth.*
import com.google.common.util.concurrent.AtomicLongMap
import com.novoda.buildproperties.CompositeException
import com.novoda.buildproperties.Entry

import javax.annotation.Nullable

/**
 * This utility class serves as static extension of {@link Truth},
 * including the custom {@link Subject} implementations defined in
 * {@code com.novoda.buildproperties.test}.
 */
final class ExtendedTruth {

    static EntrySubject assertThat(Entry entry) {
        EntrySubject.assertThat(entry)
    }

    static CompositeExceptionSubject assertThat(CompositeException exception) {
        CompositeExceptionSubject.assertThat(exception)
    }

    static <T extends Comparable<?>> ComparableSubject<?, T> assertThat(@Nullable T target) {
        return Truth.assertThat(target);
    }

    static BigDecimalSubject assertThat(@Nullable BigDecimal target) {
        return Truth.assertThat(target);
    }

    static Subject<DefaultSubject, Object> assertThat(@Nullable Object target) {
        return Truth.assertThat(target);
    }

    @GwtIncompatible("ClassSubject.java")
    static ClassSubject assertThat(@Nullable Class<?> target) {
        return Truth.assertThat(target);
    }

    static ThrowableSubject assertThat(@Nullable Throwable target) {
        return Truth.assertThat(target);
    }

    static LongSubject assertThat(@Nullable Long target) {
        return Truth.assertThat(target);
    }

    static DoubleSubject assertThat(@Nullable Double target) {
        return Truth.assertThat(target);
    }

    static FloatSubject assertThat(@Nullable Float target) {
        return Truth.assertThat(target);
    }

    static IntegerSubject assertThat(@Nullable Integer target) {
        return Truth.assertThat(target);
    }

    static BooleanSubject assertThat(@Nullable Boolean target) {
        return Truth.assertThat(target);
    }

    static StringSubject assertThat(@Nullable String target) {
        return Truth.assertThat(target);
    }

    static IterableSubject assertThat(@Nullable Iterable<?> target) {
        return Truth.assertThat(target);
    }

    static <T> ObjectArraySubject<T> assertThat(@Nullable T[] target) {
        return Truth.assertThat(target);
    }

    static PrimitiveBooleanArraySubject assertThat(@Nullable boolean[] target) {
        return Truth.assertThat(target);
    }

    static PrimitiveShortArraySubject assertThat(@Nullable short[] target) {
        return Truth.assertThat(target);
    }

    static PrimitiveIntArraySubject assertThat(@Nullable int[] target) {
        return Truth.assertThat(target);
    }

    static PrimitiveLongArraySubject assertThat(@Nullable long[] target) {
        return Truth.assertThat(target);
    }

    static PrimitiveByteArraySubject assertThat(@Nullable byte[] target) {
        return Truth.assertThat(target);
    }

    static PrimitiveCharArraySubject assertThat(@Nullable char[] target) {
        return Truth.assertThat(target);
    }

    static PrimitiveFloatArraySubject assertThat(@Nullable float[] target) {
        return Truth.assertThat(target);
    }

    static PrimitiveDoubleArraySubject assertThat(@Nullable double[] target) {
        return Truth.assertThat(target);
    }

    static GuavaOptionalSubject assertThat(@Nullable Optional<?> target) {
        return Truth.assertThat(target);
    }

    static MapSubject assertThat(@Nullable Map<?, ?> target) {
        return Truth.assertThat(target);
    }

    static MultimapSubject assertThat(@Nullable Multimap<?, ?> target) {
        return Truth.assertThat(target);
    }

    static ListMultimapSubject assertThat(@Nullable ListMultimap<?, ?> target) {
        return Truth.assertThat(target);
    }

    static SetMultimapSubject assertThat(@Nullable SetMultimap<?, ?> target) {
        return Truth.assertThat(target);
    }

    static MultisetSubject assertThat(@Nullable Multiset<?> target) {
        return Truth.assertThat(target);
    }

    static TableSubject assertThat(@Nullable Table<?, ?, ?> target) {
        return Truth.assertThat(target);
    }

    static AtomicLongMapSubject assertThat(@Nullable AtomicLongMap<?> target) {
        return Truth.assertThat(target);
    }
}
