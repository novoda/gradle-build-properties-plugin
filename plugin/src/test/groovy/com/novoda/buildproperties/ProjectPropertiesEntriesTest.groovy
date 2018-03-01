package com.novoda.buildproperties

import com.novoda.buildproperties.internal.DefaultExceptionFactory
import com.novoda.buildproperties.internal.ProjectPropertiesEntries
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static com.novoda.buildproperties.test.ExtendedTruth.assertThat
import static org.junit.Assert.fail

class ProjectPropertiesEntriesTest {

    @Rule
    public final TemporaryFolder temp = new TemporaryFolder()

    private Project project
    private Entries entries

    @Before
    public void setUp() {
        project = ProjectBuilder.builder()
                .withProjectDir(temp.newFolder())
                .build()

        entries = ProjectPropertiesEntries.from(project, new DefaultExceptionFactory('foo'))
    }

    @Test
    public void shouldThrowExceptionWhenPropertyIsNotSet() {
        try {
            entries['notThere'].string
            fail('Exception expected')
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("Unable to find value for key 'notThere'")
        }
    }

    @Test
    public void shouldResolveProjectPropertyThroughBuildProperties() {
        project.ext.key = 'value'

        assertThat(entries['key'].string).isEqualTo('value')
    }
}
