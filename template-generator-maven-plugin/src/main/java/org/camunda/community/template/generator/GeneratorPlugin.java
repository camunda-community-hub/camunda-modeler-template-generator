package org.camunda.community.template.generator;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

/** The main class of the modeler template generator */
@Mojo(
    name = "template-generator",
    defaultPhase = LifecyclePhase.COMPILE,
    requiresDependencyResolution = ResolutionScope.COMPILE)
public class GeneratorPlugin extends AbstractMojo {

  public static final String SCHEMA_BASE_URL =
      "https://unpkg.com/@camunda/element-templates-json-schema";

  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  private MavenProject project;

  @Parameter(property = "template-generator.schemaVersion", defaultValue = "null")
  private String schemaVersion;

  @Parameter(property = "template-generator.outputDir")
  private String outputDir;

  @Parameter(property = "template-generator.scanPackages", defaultValue = "*")
  private String scanPackages;

  @Parameter(property = "template-generator.skipValidation", defaultValue = "false")
  private boolean skipValidation;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    setClasspath();

    getLog().info("Schema version: " + schemaVersion);
    getLog().info("Output directory: " + outputDir);
    getLog().info("Scanned package: " + scanPackages);

    new Generator(getLog()).generate(schemaVersion, scanPackages, outputDir, skipValidation);
  }

  private void setClasspath() throws MojoExecutionException {
    final PluginDescriptor pluginDescriptor =
        (PluginDescriptor) getPluginContext().get("pluginDescriptor");
    final ClassRealm classRealm = pluginDescriptor.getClassRealm();
    try {
      project
          .getCompileClasspathElements()
          .forEach(
              c -> {
                try {
                  classRealm.addURL(new URL("file:///" + c));
                } catch (MalformedURLException e1) {
                  throw new IllegalArgumentException("Error create file URL: " + c, e1);
                }
              });
    } catch (DependencyResolutionRequiredException e1) {
      throw new MojoExecutionException("Dependency resolution failed", e1);
    }
  }
}
