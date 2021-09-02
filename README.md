# gradle-karate-runner
Gradle plugin providing taskrunner to KarateA gradle plugin for running Cucumber-JVM.

It utilises the Cucumber-JVM command line implementation and forwards every call to cucumber.api.cli.Main

Usage
Add the plugin to your project

plugins {
  id "com.github.prspal.karate-runner" version "0.0.1"
}
The complete, and updated, instructions are availabe at the plugin portal.

Then run Cucumber with the default settings

./gradlew karate

Using https://github.com/tsundberg/gradle-cucumber-runner as inspiration. 
