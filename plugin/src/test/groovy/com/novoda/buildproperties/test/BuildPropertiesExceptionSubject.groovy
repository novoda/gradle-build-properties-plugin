package com.novoda.buildproperties.test

import com.google.common.truth.FailureStrategy
import com.google.common.truth.Subject
import com.google.common.truth.SubjectFactory
import com.google.common.truth.Truth
import com.novoda.buildproperties.internal.BuildPropertiesException

import javax.annotation.Nullable

final class BuildPropertiesExceptionSubject extends Subject<BuildPropertiesExceptionSubject, BuildPropertiesException> {

    private static final SubjectFactory<BuildPropertiesExceptionSubject, BuildPropertiesException> FACTORY =
            new SubjectFactory<BuildPropertiesExceptionSubject, BuildPropertiesException>() {
                @Override
                BuildPropertiesExceptionSubject getSubject(FailureStrategy fs, BuildPropertiesException that) {
                    new BuildPropertiesExceptionSubject(fs, that)
                }
            }

    static BuildPropertiesExceptionSubject assertThat(BuildPropertiesException compositeException) {
        Truth.assertAbout(FACTORY).that(compositeException)
    }

    private BuildPropertiesExceptionSubject(FailureStrategy failureStrategy,
                                            @Nullable BuildPropertiesException subject) {
        super(failureStrategy, subject)
    }

    void hasMessage(String... messages) {
        Truth.assertThat(actual().exceptions.collect { it.message }).containsExactly(messages)
    }
}
