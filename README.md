# gradle-karate-runner
Gradle plugin providing taskrunner to KarateA gradle plugin for running Karate tests.

It utilises the Karate-JVM command line implementation and forwards every call to **com.intuit.karate.Main**

Usage
Add the plugin to your project

plugins {
  id "com.github.prspal.karate-runner" version "0.0.2"
}
The complete, and updated, instructions are availabe at the plugin portal.

Then run Karate with the default settings

_./gradlew karate_

Using https://github.com/tsundberg/gradle-cucumber-runner as inspiration. 
