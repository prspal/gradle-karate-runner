package com.github.prspal;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.File;

public class KarateTask extends DefaultTask{
    String configDir;
    String clean;
    String featurePath;
    String dryRun;
    String name;
    String tags;
    String format;
    String threads;
    String help;

    @Option(option = "karate-help",
            description = "Get Karate help")
    public void setHelp(String help) {
        this.help = help;
    }

    @Option(option = "karate-clean",
            description = "Clean Karate target directory")
    public void setClean(String clean) {
        this.clean = clean;
    }

    @Option(option = "threads",
            description = "Number of threads to run tests under. Defaults to 1.")
    public void setThreads(String threads) {
        this.threads = threads;
    }

    @Option(option = "tags",
            description = "Only run scenarios tagged with tags matching TAG_EXPRESSION.")

    public void setTags(String tags) {
        this.tags = tags;
    }


    @Option(option = "format",
            description = "Only run scenarios tagged with tags matching TAG_EXPRESSION.")

    public void setFormat(String format) {
        this.format = format;
    }

    @Option(option = "name",
            description = "Only run scenarios whose names match REGEXP.")
    public void setName(String name) {
        this.name = name;
    }

    @Option(option = "karate-dry-run",
            description = "Skip execution of glue code.")
    public void setDryRun(String dryRun) {
        this.dryRun = dryRun;
    }

    @Option(option = "featurePath",
            description = "The feature or features to execute. Defaults to src/test/resources")
    public void setFeaturePath(String featurePath) {
        this.featurePath = featurePath;
    }

    @Option(option = "configDir",
            description = "The Directory of execution. Defaults to '.'")
    public void setConfigDir(String configDir) {
        this.configDir = configDir;
    }

    public KarateTask() {
        setGroup("Karate");
        setDescription("Execute Karate-JVM from Gradle");
    }
    @TaskAction
    public void runKarate() {
        KarateExtension extension = getProject().getExtensions().findByType(KarateExtension.class);
        if (extension == null) {
            extension = new KarateExtension();
        }

        try {
            CommandLineBuilder cliBuilder = new CommandLineBuilder();
            File projectDir = getProject().getProjectDir();
            String[] command = cliBuilder.buildCommand(extension, getClasspath(), this, projectDir);
            debugCommand(command);
            execute(command);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void debugCommand(String[] command) {
        Logger logger = getLogger();

        if (logger.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            for (String s : command) {
                builder.append(s);
                builder.append(" ");
            }

            logger.debug("Karate command:");
            logger.debug(builder.toString());
        }
    }

    private void execute(String[] command) {
        int exitValue;

        try {
            Process process = new ProcessBuilder()
                    .command(command)
                    .start();

            StreamConsumer stdOut = new StreamConsumer(process.getInputStream(), System.out);
            new Thread(stdOut).start();

            StreamConsumer stdErr = new StreamConsumer(process.getErrorStream(), System.err);
            new Thread(stdErr).start();

            exitValue = process.waitFor();

            stdOut.stopProcessing();
            stdErr.stopProcessing();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (exitValue != 0) {
            throw new RuntimeException("The execution failed");
        }
    }

    private String getClasspath() {
        SourceSetContainer sourceSets = getProject().getConvention().getPlugin(JavaPluginConvention.class).getSourceSets();

        for (SourceSet sourceSet : sourceSets) {
            if ("test".equals(sourceSet.getName())) {
                return sourceSet.getRuntimeClasspath().getAsPath();
            }
        }

        throw new RuntimeException("The test classpath was not found");
    }
}

