package com.novoda.buildproperties

import com.novoda.buildproperties.internal.BuildPropertiesException
import org.junit.Test

import static com.google.common.truth.Truth.assertThat
import static com.novoda.buildproperties.test.BuildPropertiesExceptionSubject.assertThat

class BuildPropertiesExceptionTest {

    private static final Exception EXCEPTION_1 = new RuntimeException("exception 1")
    private static final Exception EXCEPTION_2 = new RuntimeException("exception 2")
    private static final Exception EXCEPTION_3 = new RuntimeException("exception 3")

    @Test
    void shouldHaveNoCause() {
        BuildPropertiesException compositeException = BuildPropertiesException.from(EXCEPTION_1)

        Throwable cause = compositeException.cause

        assertThat(cause).isNull()
    }

    @Test
    void shouldContainWrappedException() {
        BuildPropertiesException compositeException = BuildPropertiesException.from(EXCEPTION_1)

        assertThat(compositeException).hasMessage(EXCEPTION_1.message)
    }

    @Test
    void shouldContainAddedException() {
        BuildPropertiesException compositeException = BuildPropertiesException.from(EXCEPTION_1).add(EXCEPTION_2)

        assertThat(compositeException).hasMessage(EXCEPTION_1.message, EXCEPTION_2.message)
    }

    @Test
    void shouldContainAddedCompositeExceptionMessage() {
        BuildPropertiesException innerException = BuildPropertiesException.from(EXCEPTION_1).add(EXCEPTION_2)
        BuildPropertiesException compositeException = innerException.add(EXCEPTION_3)

        assertThat(compositeException).hasMessage(EXCEPTION_1.message, EXCEPTION_2.message, EXCEPTION_3.message)
    }

    @Test
    void shouldNotContainDuplicateCompositeExceptionMessage() {
        BuildPropertiesException innerException = BuildPropertiesException.from(EXCEPTION_1).add(EXCEPTION_2)

        BuildPropertiesException compositeException = innerException.add(EXCEPTION_1)

        assertThat(compositeException).hasMessage(EXCEPTION_1.message, EXCEPTION_2.message)
    }
}
