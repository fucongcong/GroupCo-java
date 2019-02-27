group 'groupco'
version '1.0'
subprojects {
    group '{{prefix}}-{{service}}'
    apply plugin: 'java'

    sourceCompatibility = 1.8

    repositories {
        mavenCentral()
    }

    dependencies {
        testCompile group: 'junit', name: 'junit', version: '4.12'
    }
}