package com.github.prspal;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

class CommandLineBuilder {

    String[] buildCommand(KarateExtension extension, String classpath, KarateTask commandLineOptions, File projectDir) {
        String main = extension.main;

        List<String> command = new ArrayList<>();
        command.add("java");
        addSystemProperties(command);
        addClasspath(command, classpath, extension, commandLineOptions);

        command.add(main);

        addHelp(command, extension, commandLineOptions);
        addThreads(command, extension, commandLineOptions);
        addClean(command, extension, commandLineOptions);
        addFormat(command, extension, commandLineOptions);
        addTags(command, extension, commandLineOptions);
        addName(command, extension, commandLineOptions);
        addDryRun(command, extension, commandLineOptions);

        addConfigDir(command, extension, commandLineOptions, projectDir);
        addOutputPath(command, extension, commandLineOptions, projectDir);
        addWorkdir(command, extension, commandLineOptions, projectDir);
        // must be last
        addFeaturePath(command, extension, commandLineOptions, projectDir);

        return command.toArray(new String[0]);
    }

    private void addClasspath(List<String> command, String classpath, KarateExtension extension, KarateTask commandLineOption) {
        boolean doShorten;
        if (commandLineOption.shorten) {
            doShorten = true;
        } else {
            doShorten = extension.shorten;
        }
        if (doShorten) {
            String argFile = Paths.get(commandLineOption.getTemporaryDir().getAbsolutePath(),"cucumber_runner_argFile").toString();
            try (PrintWriter writer = new PrintWriter(argFile) ){
                writer.println("-classpath\n" + classpath);
                command.add("@" + argFile);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(
                        String.format("Cannot write temporary file on %s,\nexception message:\n %s", argFile, e.getMessage()));
            }
        } else {
            command.add("-cp");
            command.add(classpath);
        }
    }

    private void addSystemProperties(List<String> command) {
        Properties props = System.getProperties();

        for (Object key : props.keySet()) {
            String keyValue = (String) key;
            String value = props.getProperty(keyValue);

            String systemProperty = "-D" + keyValue + "=" + value;
            command.add(systemProperty);
        }
    }

    private void addHelp(List<String> command, KarateExtension extension, KarateTask commandLineOption) {
        if ((commandLineOption.help != null) ||  !extension.help.isEmpty()) {
            command.add("--help");
        }
    }


    private void addClean(List<String> command, KarateExtension extension, KarateTask commandLineOption) {
        if ((commandLineOption.clean != null) || extension.clean){
            command.add("--clean");
        }
    }


    private void addThreads(List<String> command, KarateExtension extension, KarateTask commandLineOption) {
        String threads = commandLineOption.threads;
        if (null == threads && extension.threads < 1 ) {
            return;
        }
        else{
            threads = String.valueOf(extension.threads);
        }

        command.add("--threads");
        command.add(threads);
    }


    private void addTags(List<String> command, KarateExtension extension, KarateTask commandLineOption) {
        String tags = commandLineOption.tags;
        if (null == tags && extension.tags.isEmpty()){
            return;
        }
        else if (!extension.tags.isEmpty()){
            tags = extension.tags;
        }

        //Group Ignored  & Selected tags separately
        List<String> ignored = Arrays.stream(tags.split(",")).map(String::trim).filter(trim -> trim.startsWith("~")).collect(Collectors.toList());
        List<String> selected = Arrays.stream(tags.split(",")).map(String::trim).filter(trim -> !trim.startsWith("~")).collect(Collectors.toList());

        ignored.forEach(tag -> {
            command.add("--tags");
            command.add(tag);
        });

        if(!selected.isEmpty()) {
            command.add("--tags");
            command.add(String.join(",", selected));
        }

    }

    private void addFormat(List<String> command, KarateExtension extension, KarateTask commandLineOption) {
        String format = commandLineOption.format;
        if (null == format && extension.format.isEmpty()){
            return;
        }
        else if(!extension.format.isEmpty()){
            format = extension.format;
        }

        command.add("--format");
        command.add(format);
    }

    private void addName(List<String> command, KarateExtension extension, KarateTask commandLineOption) {
        String name = commandLineOption.name;
        if (null == name && extension.name.isEmpty()){
            return;
        }
        else if(!extension.name.isEmpty()){
            name = extension.name;
        }
        command.add("--name");
        command.add(name);
    }

    private void addDryRun(List<String> command, KarateExtension extension, KarateTask commandLineOption) {
        String dryRun = commandLineOption.dryRun;
        if (null == dryRun && !extension.dryRun){
            return;
        }
        else if(extension.dryRun){
            dryRun = String.valueOf(extension.dryRun);
        }
        command.add("--dryrun");
        command.add(String.valueOf(Boolean.parseBoolean(dryRun)));
    }


    private void addFeaturePath(List<String> command, KarateExtension extension, KarateTask commandLineOption, File projectDir) {
        String featurePath = commandLineOption.featurePath;
        if (null == featurePath ){
            featurePath = extension.featurePath;
        }

        boolean absolutePath = new File(featurePath).isAbsolute();
        if (!absolutePath) {
            String root = getAbsoluteRoot(projectDir);
            featurePath = root + featurePath;
        }

        command.add(featurePath);
    }

    private void addOutputPath(List<String> command, KarateExtension extension, KarateTask commandLineOption, File projectDir) {
        String outputPath = commandLineOption.outputPath;
        if (null == outputPath && extension.outputPath.isEmpty()){
            return;
        }
        else {
            outputPath = extension.outputPath;
        }

        boolean absolutePath = new File(outputPath).isAbsolute();
        if (!absolutePath) {
            String root = getAbsoluteRoot(projectDir);
            outputPath = root + outputPath;
        }

        command.add("--output");
        command.add(outputPath);
    }

    private void addConfigDir(List<String> command, KarateExtension extension, KarateTask commandLineOption, File projectDir) {
        String configDir = commandLineOption.configDir;
        if (null == configDir && extension.configDir.isEmpty()){
            return;
        }
        else {
            configDir = extension.configDir;
        }

        boolean absolutePath = new File(configDir).isAbsolute();
        if (!absolutePath) {
            String root = getAbsoluteRoot(projectDir);
            configDir = root + configDir;
        }

        command.add("--configdir");
        command.add(configDir);
    }

    private void addWorkdir(List<String> command, KarateExtension extension, KarateTask commandLineOption, File projectDir) {
        String workdir = commandLineOption.workdir;
        if (null == workdir && extension.workdir.isEmpty()){
            return;
        }
        else {
            workdir = extension.configDir;
        }

        boolean absolutePath = new File(workdir).isAbsolute();
        if (!absolutePath) {
            String root = getAbsoluteRoot(projectDir);
            workdir = root + workdir;
        }

        command.add("--workdir");
        command.add(workdir);
    }

    private String getAbsoluteRoot(File projectDir) {
        return projectDir.getAbsolutePath() + File.separator;
    }
}