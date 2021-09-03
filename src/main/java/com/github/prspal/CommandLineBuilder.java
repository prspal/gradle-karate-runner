package com.github.prspal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

class CommandLineBuilder {

    String[] buildCommand(KarateExtension extension, String classpath, KarateTask commandLineOptions, File projectDir) {
        String main = extension.main;

        List<String> command = new ArrayList<>();
        command.add("java");
        addSystemProperties(command);
        command.add("-cp");
        command.add(classpath);
        command.add(main);

        addHelp(command, extension, commandLineOptions);
        addThreads(command, extension, commandLineOptions);
        addClean(command, extension, commandLineOptions);
        addFormat(command, extension, commandLineOptions);
        addConfigDir(command, extension, commandLineOptions, projectDir);
        addTags(command, extension, commandLineOptions);
        addName(command, extension, commandLineOptions);
        addDryRun(command, extension, commandLineOptions);

        // must be last
        addFeaturePath(command, extension, commandLineOptions, projectDir);

        return command.toArray(new String[0]);
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
        if (commandLineOption.help != null) {
            command.add("--help");
            return;
        }

        if (!extension.help.isEmpty()) {
            command.add("--help");
        }
    }


    private void addClean(List<String> command, KarateExtension extension, KarateTask commandLineOption) {
        if (commandLineOption.clean != null) {
            command.add("--clean");
            return;
        }

        if (!extension.clean.isEmpty()) {
            command.add("--clean");
        }
    }


    private void addThreads(List<String> command, KarateExtension extension, KarateTask commandLineOption) {
        if (commandLineOption.threads != null) {
            command.add("--threads");
            command.add(commandLineOption.threads);
            return;
        }

        if (!extension.threads.isEmpty()) {
            command.add("--threads");
            command.add(extension.threads);
        }
    }


    private void addTags(List<String> command, KarateExtension extension, KarateTask commandLineOption) {
        if (commandLineOption.tags != null) {
            command.add("--tags");
            command.add("'"+commandLineOption.tags +"'");
            return;
        }

        if (extension.tags.length > 0) {
            command.add("--tags");
            command.add("'"+String.join(",",extension.tags)+"'");
        }
    }

    private void addFormat(List<String> command, KarateExtension extension, KarateTask commandLineOption) {
        if (commandLineOption.format != null) {
            command.add("--format=");
            command.add(commandLineOption.tags);
            return;
        }

        if (extension.format.length > 0) {
            command.add("--tags=");
            command.add(String.join(",",extension.format));
        }
    }

    private void addName(List<String> command, KarateExtension extension, KarateTask commandLineOption) {
        if (commandLineOption.name != null) {
            command.add("--name");
            command.add(commandLineOption.name);
            return;
        }

        if (!extension.name.isEmpty()) {
            command.add("--name");
            command.add(extension.name);
        }
    }

    private void addDryRun(List<String> command, KarateExtension extension, KarateTask commandLineOption) {
        if (commandLineOption.dryRun != null) {
            command.add("--dry-run");
            command.add("true");
            return;
        }

        if (!extension.dryRun.isEmpty()) {
            command.add("--dry-run");
            command.add("true");
        }
    }


    private void addFeaturePath(List<String> command, KarateExtension extension, KarateTask commandLineOption, File projectDir) {
        String featurePath = commandLineOption.featurePath;
        if (featurePath != null) {
            boolean absolutePath = new File(featurePath).isAbsolute();
            if (!absolutePath) {
                String root = getAbsoluteRoot(projectDir);
                featurePath = root + featurePath;
            }

            command.add(featurePath);
            return;
        }

        String root = getAbsoluteRoot(projectDir);
        featurePath = root + extension.featurePath;
        command.add(featurePath);
    }

    private void addConfigDir(List<String> command, KarateExtension extension, KarateTask commandLineOption, File projectDir) {
        String configDir = commandLineOption.configDir;
        if (configDir != null) {
            boolean absolutePath = new File(configDir).isAbsolute();
            if (!absolutePath) {
                String root = getAbsoluteRoot(projectDir);
                configDir = root + configDir;
            }

            command.add(configDir);
            return;
        }

        String root = getAbsoluteRoot(projectDir);
        configDir = root + extension.configDir;
        command.add(configDir);
    }

    private String getAbsoluteRoot(File projectDir) {
        return projectDir.getAbsolutePath() + File.separator;
    }
}