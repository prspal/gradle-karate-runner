/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package gradle.karate.runner;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 * A simple unit test for the 'gradle.karate.runner.greeting' plugin.
 */
public class GradleKarateRunnerPluginTest {
    @Test
    public void pluginRegistersATask() {
        Project project = ProjectBuilder.builder().build();
        // Java plugin required for IMPLEMENTATION task in the custom plugin
        project.getPluginManager().apply("java");
        project.getPluginManager().apply("com.github.prspal.karate-runner");

        // Verify the result
        Task karate = project.getTasks().findByName("karate");
        Assert.assertNotNull(karate);
    }


}

