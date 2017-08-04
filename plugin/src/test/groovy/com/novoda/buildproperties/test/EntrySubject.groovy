package com.novoda.buildproperties.test

import com.google.common.truth.FailureStrategy
import com.google.common.truth.Subject
import com.google.common.truth.SubjectFactory
import com.google.common.truth.Truth
import com.novoda.buildproperties.internal.BuildPropertiesException
import com.novoda.buildproperties.Entry

import javax.annotation.Nullable

final class EntrySubject extends Subject<EntrySubject, Entry> {

    private static final SubjectFactory<EntrySubject, Entry> FACTORY = new SubjectFactory<EntrySubject, Entry>() {
        @Override
        EntrySubject getSubject(FailureStrategy fs, Entry that) {
            new EntrySubject(fs, that)
        }
    }

    static EntrySubject assertThat(Entry entry) {
        Truth.assertAbout(FACTORY).that(entry)
    }

    private EntrySubject(FailureStrategy failureStrategy, @Nullable Entry subject) {
        super(failureStrategy, subject)
    }

    void willThrow(Class<? extends Throwable> throwableClass) {
        try {
            entryValue
            fail('throws', throwableClass)
        } catch (Throwable throwable) {
            check().that(throwable).isInstanceOf(throwableClass)
        }
    }

    private String getEntryValue() {
        actual().string
    }

    void willThrow(BuildPropertiesException compositeException) {
        try {
            entryValue
            fail('throws', compositeException)
        } catch (BuildPropertiesException thrown) {
            check().that(thrown.message).isEqualTo(compositeException.message)
        }
    }

    void hasValue(def expected) {
        check().that(entryValue).isEqualTo(expected)
    }

}
