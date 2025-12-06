import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.vanniktech.maven.publish.MavenPublishBaseExtension

plugins {
  id("org.jetbrains.kotlinx.kover") version "0.8.2"
  id("org.jetbrains.dokka") version "1.9.20"
  id("com.vanniktech.maven.publish") version "0.33.0"
}

buildscript {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
  }

  dependencies {
    classpath(libs.agp)
    classpath(libs.kotlin)
    classpath(libs.kotlin.serialization)
  }
}

allprojects {
  repositories {
    google()
    mavenCentral()
  }

  group = "tech.arnav"
  version = "2.0.2"

  apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
  apply(plugin = "org.jetbrains.kotlinx.kover")
  apply(plugin = "org.jetbrains.dokka")
  apply(plugin = "com.vanniktech.maven.publish")

  extensions.configure<PublishingExtension> {
    repositories {
      maven {
        val isSnapshot = version.toString().endsWith("SNAPSHOT")
        url = uri(
          if (!isSnapshot) "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2"
          else "https://s01.oss.sonatype.org/content/repositories/snapshots"
        )

        credentials {
          username = gradleLocalProperties(rootDir).getProperty("sonatypeUsername")
          password = gradleLocalProperties(rootDir).getProperty("sonatypePassword")
        }
      }
    }

    val javadocJar = tasks.register<Jar>("javadocJar") {
      dependsOn(tasks.dokkaHtml)
      archiveClassifier.set("javadoc")
      from("${layout.buildDirectory}/dokka")
    }

  }

  mavenPublishing {
    // Define your coordinates
    coordinates("tech.arnav", "kstore2", version.toString())

    pom {
      name.set("KStore2")
      description.set("A tiny Kotlin multiplatform library that assists in saving and restoring objects to and from disk using kotlinx.coroutines, kotlinx.serialisation and okio")
      inceptionYear.set("2025")
      url.set("https://github.com/championswimmer/kstore")
      licenses {
        license {
          name.set("The Apache License, Version 2.0")
          url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
          distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
        }
      }
      issueManagement {
        system = "Github"
        url = "https://github.com/xxfast/KStore/issues"
      }
      developers {
        developer {
          id.set("xxfast")
          name.set("Isuru Rajapakse")
          url.set("https://github.com/xxfast")
        }
        developer {
          id.set("championswimmer")
          name.set("Arnav Gupta")
          url.set("https://github.com/championswimmer")
        }
      }
      scm {
        url.set("https://github.com/championswimmer/kstore")
        connection.set("scm:git:git://github.com/championswimmer/kstore.git")
        developerConnection.set("scm:git:ssh://git@github.com/championswimmer/kstore.git")
      }
    }

    // Configure publishing to Maven Central via the new Portal
    publishToMavenCentral()

    // Enable GPG signing for all publications
    signAllPublications()
  }

}
