package com.novoda.buildproperties

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
    void setUp() {
        project = ProjectBuilder.builder()
                .withProjectDir(temp.newFolder())
                .build()
        project.apply plugin: BuildPropertiesPlugin
    }

    @Test
    void shouldReturnMapValuesWhenEntriesFromMap() {
        project.buildProperties {
            map {
                using([a: 'value_a', b: 'value_b', c: 'value_c'])
            }
        }

        assertThat(project.buildProperties.map['a']).hasValue('value_a')
        assertThat(project.buildProperties.map['b']).hasValue('value_b')
        assertThat(project.buildProperties.map['c']).hasValue('value_c')
    }

    @Test
    void shouldReturnEnvironmentVariablesValuesWhenEntriesFromEnvironmentVariables() {
        environment.set('X', 'value_x')
        environment.set('Y', 'value_y')
        environment.set('Z', 'value_z')

        project.buildProperties {
            env {
                using System.getenv()
            }
        }

        assertThat(project.buildProperties.env['X']).hasValue('value_x')
        assertThat(project.buildProperties.env['Y']).hasValue('value_y')
        assertThat(project.buildProperties.env['Z']).hasValue('value_z')
    }

    @Test
    void shouldReturnProjectPropertiesValuesWhenEntriesFromProjectProperties() {
        project.ext.x = 'value_x'
        project.ext.y = 'value_y'
        project.ext.z = 'value_z'

        project.buildProperties {
            prj {
                using project.properties
            }
        }

        assertThat(project.buildProperties.prj['x']).hasValue('value_x')
        assertThat(project.buildProperties.prj['y']).hasValue('value_y')
        assertThat(project.buildProperties.prj['z']).hasValue('value_z')
    }

    @Test
    void shouldNotThrowExceptionWhenEntriesFromNonExistentPropertiesFile() {
        project.buildProperties {
            foo {
                using project.file('foo.properties')
            }
        }
    }

    @Test
    void shouldThrowWhenAccessingPropertyFromNonExistentPropertiesFile() {
        project.buildProperties {
            foo {
                using project.file('foo.properties')
            }
        }

        try {
            project.buildProperties.foo['any'].string
            fail('Exception not thrown')
        } catch (Exception e) {
            assertThat(e.getMessage()).contains('foo.properties does not exist.')
        }
    }

    @Test
    void shouldProvideSpecifiedErrorMessageWhenAccessingPropertyFromNonExistentPropertiesFile() {
        project.apply plugin: BuildPropertiesPlugin

        def description = 'This file should contain the following properties:\n- foo\n- bar'
        try {
            project.buildProperties {
                foo {
                    using project.file('foo.properties')
                    setDescription(description)
                }
            }
            project.buildProperties.foo['any'].string
            fail('Exception not thrown')
        } catch (Exception e) {
            String message = e.getMessage()
            assertThat(message).contains('foo.properties does not exist.')
            assertThat(message).contains('* buildProperties.foo: This file should contain the following properties:\n' +
                    '                       - foo\n' +
                    '                       - bar')
        }
    }

    @Test
    void shouldReturnPropertiesFileValuesWhenEntriesFromPropertiesFile() {
        File propertiesFile = newPropertiesFile('test.properties', 'd=value_d\ne=value_e\nf=value_f')

        project.buildProperties {
            test {
                using propertiesFile
            }
        }

        assertThat(project.buildProperties.test['d']).hasValue('value_d')
        assertThat(project.buildProperties.test['e']).hasValue('value_e')
        assertThat(project.buildProperties.test['f']).hasValue('value_f')
    }

    @Test
    void shouldContainAllPropertiesFromFallbackSet() {
        File propertiesFile = newPropertiesFile('test.properties', 'd=value_d\ne=value_e\nf=value_f')

        project.buildProperties {
            map {
                using([a: 'value_a', b: 'value_b', c: 'value_c'])
            }
            other {
                using propertiesFile
                fallback map
            }
        }

        assertThat(project.buildProperties.other['a']).hasValue('value_a')
        assertThat(project.buildProperties.other['b']).hasValue('value_b')
        assertThat(project.buildProperties.other['c']).hasValue('value_c')
        assertThat(project.buildProperties.other['d']).hasValue('value_d')
        assertThat(project.buildProperties.other['e']).hasValue('value_e')
        assertThat(project.buildProperties.other['f']).hasValue('value_f')
    }

    @Test
    void shouldIgnorePropertyFromFallbackSetWhenAlreadyDefined() {
        File propertiesFile = newPropertiesFile('test.properties', 'd=value_d\ne=value_e\nf=value_f')

        project.buildProperties {
            map {
                using([a: 'value_a', b: 'value_b', c: 'value_c', e: 'random'])
            }
            other {
                using propertiesFile
                fallback map
            }
        }

        assertThat(project.buildProperties.other['a']).hasValue('value_a')
        assertThat(project.buildProperties.other['b']).hasValue('value_b')
        assertThat(project.buildProperties.other['c']).hasValue('value_c')
        assertThat(project.buildProperties.other['d']).hasValue('value_d')
        assertThat(project.buildProperties.other['e']).hasValue('value_e')
        assertThat(project.buildProperties.other['f']).hasValue('value_f')
    }

    @Test
    void shouldMentionFallbackSetWhenPropertyNotfound() {
        File propertiesFile = newPropertiesFile('test.properties', 'd=value_d\ne=value_e\nf=value_f')

        project.buildProperties {
            map {
                using([a: 'value_a', b: 'value_b', c: 'value_c', e: 'random'])
            }
            other {
                using propertiesFile
                fallback map
            }
        }

        try {
            project.buildProperties.other['notThere'].string
            fail('Exception not thrown')
        } catch (Exception e) {
            String message = e.getMessage()
            assertThat(message).contains('- Unable to find value for key \'notThere\' in properties set \'buildProperties.other\'')
            assertThat(message).contains('- Unable to find value for key \'notThere\' in properties set \'buildProperties.map\'')
        }
    }

    private File newPropertiesFile(String fileName, String fileContent) {
        File propertiesFile = temp.newFile(fileName)
        propertiesFile.text = fileContent
        propertiesFile
    }
}
