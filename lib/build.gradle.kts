plugins {
    `java-library`
    id("com.vanniktech.maven.publish") version "0.34.0"
}

group   = "fr.unistra.cds"
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
        languageVersion = JavaLanguageVersion.of(11)
    }
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}


tasks.named<Javadoc>("javadoc") {
    source = sourceSets.main.get().allJava
    destinationDir = File(rootDir,"docs")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

mavenPublishing {
    coordinates(group.toString(), rootProject.name, version.toString())

    pom {
        name.set(rootProject.name)
        description.set("Lightweight Java library to validate UCDs (Unified Content Descriptors) standardized by the IVOA.")
        inceptionYear.set("2025")
        url.set("https://github.com/gmantele/ucidy/")
        licenses {
            license {
                name.set("GNU Lesser General Public License version 3")
                url.set("https://opensource.org/license/lgpl-3-0")
                distribution.set("https://www.gnu.org/licenses/lgpl-3.0.txt")
            }
        }
        developers {
            developer {
                id.set("gmantelet")
                name.set("Grégory Mantelet")
                email.set("gmantelet@astro.unistra.fr")
                url.set("https://github.com/gmantele/")
            }
        }
        scm {
            url.set("https://github.com/gmantele/ucidy/")
            connection.set("scm:git:git://github.com/gmantele/ucidy.git")
            developerConnection.set("scm:git:ssh://git@github.com:gmantele/ucidy.git")
        }
    }
}
