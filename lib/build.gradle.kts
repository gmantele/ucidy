plugins {
    `java-library`
}

version = 1.4

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

tasks.named<Javadoc>("javadoc") {
    source = sourceSets.main.get().allJava
    setDestinationDir(File(rootDir,"docs"))
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
