package com.novoda.buildproperties

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.EnvironmentVariables
import org.junit.rules.TemporaryFolder

import static com.novoda.buildproperties.test.ExtendedTruth.assertThat
import static org.junit.Assert.fail

class BuildPropertiesTest {

    @Rule
    public final TemporaryFolder temp = new TemporaryFolder()

    @Rule
    public final EnvironmentVariables environment = new EnvironmentVariables()

    private Project project

    @Before
    public void setUp() {
        project = ProjectBuilder.builder()
                .withProjectDir(temp.newFolder())
                .build()
        project.apply plugin: BuildPropertiesPlugin
    }

    @Test
    public void shouldReturnMapValuesWhenEntriesFromMap() {
        project.buildProperties {
            map {
                from([a: 'value_a', b: 'value_b', c: 'value_c'])
            }
        }

        assertThat(project.buildProperties.map['a']).hasValue('value_a')
        assertThat(project.buildProperties.map['b']).hasValue('value_b')
        assertThat(project.buildProperties.map['c']).hasValue('value_c')
    }

    @Test
    public void shouldReturnMapKeysWhenEntriesFromMap() {
        project.buildProperties {
            map {
                from([a: 'value_a', b: 'value_b', c: 'value_c'])
            }
        }

        def keys = project.buildProperties.map.keys

        assertThat(Collections.list(keys)).containsExactly('b', 'a', 'c')
    }

    @Test
    public void shouldReturnEnvironmentVariablesValuesWhenEntriesFromEnvironmentVariables() {
        environment.set('X', 'value_x')
        environment.set('Y', 'value_y')
        environment.set('Z', 'value_z')

        project.buildProperties {
            env {
                from System.getenv()
            }
        }

        assertThat(project.buildProperties.env['X']).hasValue('value_x')
        assertThat(project.buildProperties.env['Y']).hasValue('value_y')
        assertThat(project.buildProperties.env['Z']).hasValue('value_z')
    }

    @Test
    public void shouldReturnEnvironmentVariablesKeysWhenEntriesFromEnvironmentVariables() {
        environment.set('X', 'value_x')
        environment.set('Y', 'value_y')
        environment.set('Z', 'value_z')

        project.buildProperties {
            env {
                from System.getenv()
            }
        }

        def keys = project.buildProperties.env.keys

        assertThat(Collections.list(keys)).containsAllOf('X', 'Y', 'Z')
    }

    @Test
    public void shouldReturnProjectPropertiesValuesWhenEntriesFromProjectProperties() {
        project.ext.x = 'value_x'
        project.ext.y = 'value_y'
        project.ext.z = 'value_z'

        project.buildProperties {
            prj {
                from project.properties
            }
        }

        assertThat(project.buildProperties.prj['x']).hasValue('value_x')
        assertThat(project.buildProperties.prj['y']).hasValue('value_y')
        assertThat(project.buildProperties.prj['z']).hasValue('value_z')
    }

    @Test
    public void shouldReturnProjectPropertiesKeysWhenEntriesFromProjectProperties() {
        project.ext.x = 'value_x'
        project.ext.y = 'value_y'
        project.ext.z = 'value_z'

        project.buildProperties {
            prj {
                from project.properties
            }
        }

        def keys = project.buildProperties.prj.keys

        assertThat(Collections.list(keys)).containsAllOf('x', 'y', 'z')
    }

    @Test
    public void shouldNotThrowExceptionWhenEntriesFromNonExistentPropertiesFile() {
        project.buildProperties {
            foo {
                file project.file('foo.properties')
            }
        }
    }

    @Test
    public void shouldThrowWhenAccessingPropertyFromNonExistentPropertiesFile() {
        project.buildProperties {
            foo {
                file project.file('foo.properties')
            }
        }

        try {
            project.buildProperties.foo['any'].string
            fail('Gradle exception not thrown')
        } catch (GradleException e) {
            assertThat(e.getMessage()).endsWith('foo.properties does not exist.')
        }
    }

    @Test
    public void shouldProvideSpecifiedErrorMessageWhenAccessingPropertyFromNonExistentPropertiesFile() {
        project.apply plugin: BuildPropertiesPlugin

        def errorMessage = 'This file should contain the following properties:\n- foo\n- bar'
        try {
            project.buildProperties {
                foo {
                    file project.file('foo.properties'), errorMessage
                }
            }
            project.buildProperties.foo['any'].string
            fail('Gradle exception not thrown')
        } catch (GradleException e) {
            String message = e.getMessage()
            assertThat(message).contains('foo.properties does not exist.')
            assertThat(message).endsWith(errorMessage)
        }
    }

    @Test
    public void shouldReturnPropertiesFileValuesWhenEntriesFromPropertiesFile() {
        File propertiesFile = newPropertiesFile('test.properties', 'd=value_d\ne=value_e\nf=value_f')

        project.buildProperties {
            test {
                file propertiesFile
            }
        }

        assertThat(project.buildProperties.test['d']).hasValue('value_d')
        assertThat(project.buildProperties.test['e']).hasValue('value_e')
        assertThat(project.buildProperties.test['f']).hasValue('value_f')
    }

    @Test
    public void shouldReturnPropertiesFileKeysWhenEntriesFromPropertiesFile() {
        File propertiesFile = newPropertiesFile('test.properties', 'd=value_d\ne=value_e\nf=value_f')

        project.buildProperties {
            test {
                file propertiesFile
            }
        }

        def keys = project.buildProperties.test.keys

        assertThat(Collections.list(keys)).containsExactly('d', 'e', 'f')
    }

    private File newPropertiesFile(String fileName, String fileContent) {
        File propertiesFile = temp.newFile(fileName)
        propertiesFile.text = fileContent
        propertiesFile
    }
}
