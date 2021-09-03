package com.github.prspal;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class KarateRunner implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getExtensions().create("karate", KarateExtension.class);
        KarateTask karate = project.getTasks().create("karate", KarateTask.class);

        project.afterEvaluate(p -> karate.dependsOn(p.getTasks().getByName("testClasses")));
    }
}
