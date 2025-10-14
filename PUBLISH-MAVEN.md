# Introduction

This project is published in Maven Central Repository using the Gradle plugin
[com.vanniktech.maven.publish](https://vanniktech.github.io/gradle-maven-publish-plugin/central/).

# Steps to publish (once configured) 

1. Update the publication version in [build.gradle.kts](lib/build.gradle.kts)
2. Check the content to publish by publishing locally: `./gradlew clean publishToMavenLocal`
3. Look at the local Maven package: `~/.m2/repository/fr/unistra/cds/ucidy/`
4. If OK, run `./gradlew clean publishToMavenCentral`
5. Check the validation status *and* the content on:
   https://central.sonatype.com/publishing/deployments
6. If OK, on the same page, push the _Publish_ button.
   Otherwise, push the _Drop_ button.
7. Wait for at most 30 minutes before the publication becomes visible.

# First configuration

1. Create a Maven Repository namespace (see https://central.sonatype.org/register/namespace/)
2. Create and publish a GPG key (see https://central.sonatype.org/publish/requirements/gpg/):
   ```bash
   gpg --gen-key
   gpg --keyserver keyserver.ubuntu.com --send-keys ...
   gpg --keyring secring.gpg --export-secret-keys > ~/.gnupg/secring.gpg
   ```
3. Add into [gradle.properties](gradle.properties):
   ```
   # Enable publication on Maven Central:
   mavenCentralPublishing=true
   
   # Enable publication signing for publication on Maven Central:
   signAllPublications=true
   ```
4. Create a Maven Repository User Token (see https://central.sonatype.com/usertoken)
5. Add and adapt into `~/.gradle/gradle.properties`:
   ```
   mavenCentralUsername=username
   mavenCentralPassword=the_password
   
   signing.keyId=12345678
   signing.password=some_password
   signing.secretKeyRingFile=/Users/yourusername/.gnupg/secring.gpg
   ```
6. Configure the project name in [settings.gradle.kts](settings.gradle.kts):
   ```kotlin
   rootProject.name = "ucidy"
   ```
7. Configure the Maven publication in [build.gradle.kts](lib/build.gradle.kts):
   ```kotlin
   plugins {
       // [...]
       id("com.vanniktech.maven.publish") version "0.34.0"
   }
   
   group   = "fr.unistra.cds"
   version = 1.4
   
   // [...]
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
   ```
   
# References

- [Official Maven Central Repository documentation](https://central.sonatype.org/register/central-portal/)
- [Vanniketch's Maven Publish Gradle Plugin](https://vanniktech.github.io/gradle-maven-publish-plugin/central/) _(community plugin chosen and used in this projet)_
  - [Youtube video on how to use this plugin](https://www.youtube.com/watch?v=nd2ULXyBaV8&t=239s) 
- [Gradle Cookbook for Maven publication](https://cookbook.gradle.org/integrations/maven-central/publishing/)
- [Alternative methods to publish on Maven Central](https://blog.jora.dev/en/posts/publish-to-maven-central-with-gradle-in-2025/)
- [The Maven Publish Gradle Plugin](https://docs.gradle.org/current/userguide/publishing_maven.html) _(official Gradle plugin but not used in this project)_