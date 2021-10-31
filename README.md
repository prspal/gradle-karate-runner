# Gradle Cucumber runner

A gradle plugin for running Karate-JVM, taking inspiration from [gradle-cucumber-runner](https://github.com/tsundberg/gradle-cucumber-runner), with a big thanks to @tsundberg

It trades Cucumber-JVM options with Karate-JVM command line implementation and forwards every call to `com.intuit.karate.Main`

## Usage

Add the plugin as well as dependencies for your project


```
plugins {
  id "com.github.prspal.karate-runner" version "0.0.8.2"
}
dependencies {
    //Add karate gradle dependency
    implementation 'com.intuit.karate:karate-core:1.1.0'
}
```

The complete, and updated, instructions are availabe at the
[plugin portal](https://plugins.gradle.org/plugin/com.github.prspal.karate-runner).

Then run Cucumber with the default settings

    ./gradlew karate

### Configuration

Karate doesn't need any glue path, you can specify the following options:

```
cucumber {
    threads = '4'
    tags = ''
    name = ''
    format = '[html,json,cucumber:json,junit:xml]'
    dryRun = false
    output = 'relate/absolute path to reports'
    help = ''

    featurePath = 'src/test/resources'
    main = 'com.intuit.karate.Main'
}
```

The only setting with default values are

```
    featurePath = 'src/test/resources'
    main = 'cucumber.api.cli.Main'
```

### Options

The options available can be listed with the command

    ./gradlew help --task karate


More complicated expressions should be quoted to be forwarded to Cucumber.

    ./gradlew cucumber --tags "~@notme, @runme" 
    ./gradlew cucumber --tags "~@dont,~@notme, @runme,@alsome" 

#### Tags

An important part of running Cucumber is to be able to partition the execution
using different tags.

Executing a single tag can be done like this:

    ./gradlew cucumber --tags @runme

(assuming that you have a tag for work in progress, `@runme`)

Karate supports multiple tags to be executed at the same time.

    ./gradlew cucumber --tags "~@notme, @runme

Doing so will forward the expression `~@wip` to the option `-tags`
and finally to Cucumber.


### Running features in parallel

Karate like Cucumber supports parallel execution if you specify the number of thread
to use. This can be done in two ways

* An option whn running from a command line, `./gradlew karate --threads 4`, this will run four parallel threads


### Getting help

    ./gradlew karate --karate-help=please

This will call Cucumber with the argument `--help`. Unfortunately, Gradle requires that each
command line option is followed by a value. It is `please` above. However, it could be anything.

### Trouble shooting

#### Java 8 is required

The plugin is build using Java 8 and gradle must be executed with Java 8 or newer.

#### The karate command executed

Execute Gradle with `debug` to see the command that will be executed.

The printout is verbose, search for `Karate command` to find the actual command executed.
