package org.camunda.community.template.generator;

import static org.camunda.community.template.generator.GeneratorPlugin.SCHEMA_BASE_URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.logging.console.ConsoleLogger;

/** */
public class Generator {

  private Log logger = null;

  /** Default constructor. */
  public Generator() {
    this(new DefaultLog(new ConsoleLogger()));
  }

  /**
   * Constructor for specific logging.
   *
   * @param log the logger from the Maven context
   */
  public Generator(Log log) {
    this.logger = log;
  }

  /**
   * Generate and validate the templates.
   *
   * @param schemaVersion Version of the schema for validation
   * @param scanPackages Packages that should be scanned for annotations
   * @param outputDir the folder for the generated template files
   * @param skipValidation flag to skip the json validation
   * @throws MojoExecutionException in case of an error during template generation
   */
  public void generate(
      String schemaVersion, String scanPackages, String outputDir, boolean skipValidation)
      throws MojoExecutionException {
    // Set schema URL
    String schemaURL =
        SCHEMA_BASE_URL
            + (schemaVersion.equals("null") ? "" : "@" + schemaVersion)
            + "/resources/schema.json";
    JsonSchema schema = null;
    Set<String> templateIDs = new HashSet<>();
    ClassInfoList classInfoList = scanPackages(scanPackages);

    if (!skipValidation) {
      // Download schema for validation
      schema = downloadSchema(schemaURL);
    }

    // Iterate through all classes containing a Template annotation
    for (ClassInfo classInfo : classInfoList) {
      if (classInfo.hasDeclaredMethodAnnotation(Template.class.getName())) {
        // Set template file output path
        String fileName = classInfo.getSimpleName() + "Templates.json";
        String filePath = outputDir + File.separator + fileName;

        // Parse templates of the current class
        List<org.camunda.community.template.generator.objectmodel.Template> templates =
            GeneratorParser.processTemplates(classInfo);
        for (org.camunda.community.template.generator.objectmodel.Template template : templates) {
          template.setSchemaURL(schemaURL);

          if (!templateIDs.contains(template.getTemplateID())) {
            templateIDs.add(template.getTemplateID());
          } else {
            throw new MojoExecutionException(
                "Duplicate template id found: " + template.getTemplateID());
          }
        }

        writeJsonToFile(filePath, templates);

        // Validate JSON file
        if (!skipValidation) {
          // Validate file using schema
          validateJsonFile(filePath, schema);
        } else {
          logger.warn("Skipping JSON schema validation!");
        }
      }
    }
  }

  /**
   * Scan for {@link org.camunda.community.template.generator.objectmodel.Template} annotation in
   * classpath
   *
   * @param scanPackages name of package that should be scanned
   * @return List of class infos
   */
  private ClassInfoList scanPackages(String scanPackages) {
    logger.info("Scanning for annotations ...");

    ScanResult scanResult = new ClassGraph().acceptPackages(scanPackages).enableAllInfo().scan();
    return scanResult.getClassesWithMethodAnnotation(Template.class.getName());
  }

  /**
   * Writes the JSON String to a specific file path.
   *
   * @param filePath The path where to save the specified JSON String
   * @param templates List of templates
   * @throws MojoExecutionException
   */
  private void writeJsonToFile(
      String filePath,
      List<org.camunda.community.template.generator.objectmodel.Template> templates)
      throws MojoExecutionException {
    // Serialize object model to JSON
    String resultJSON = (new GsonBuilder()).setPrettyPrinting().create().toJson(templates);

    File file = new File(filePath);
    file.getParentFile().mkdirs();

    logger.info(
        "Create template file '"
            + file.getName()
            + "' with "
            + templates.size()
            + " "
            + (templates.size() == 1 ? "entry" : "entries"));

    try (FileWriter outputFile = new FileWriter(file)) {
      outputFile.write(resultJSON);
    } catch (IOException e) {
      throw new MojoExecutionException("Failed to write output file " + filePath, e);
    }
  }

  /**
   * Downloads a schema from the provided URL
   *
   * @param schemaURL The URL from where to download the schema
   * @return The schema as String
   */
  public JsonSchema downloadSchema(String schemaURL) throws MojoExecutionException {
    StringBuilder schema = new StringBuilder();

    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(new URL(schemaURL).openStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        schema.append(line);
      }

      logger.info("Successfully downloaded schema from: " + schemaURL);
    } catch (IOException e) {
      throw new MojoExecutionException("Failed to download schema!", e);
    }

    JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
    return schemaFactory.getSchema(new ByteArrayInputStream(schema.toString().getBytes()));
  }

  /**
   * Validates a JSON file against the provided schema
   *
   * @param filePath The file path to validate
   * @param schema The JSON schema to use for validation
   * @throws MojoExecutionException
   */
  public void validateJsonFile(String filePath, JsonSchema schema) throws MojoExecutionException {
    ObjectMapper objectMapper = new ObjectMapper();

    try {
      File file = new File(filePath);
      JsonNode json = objectMapper.readTree(new FileInputStream(file));
      Set<ValidationMessage> validationResult = schema.validate(json);

      // Print validation errors
      if (validationResult.isEmpty()) {
        logger.info(file.getName() + ": Validation successful");
      } else {
        validationResult.forEach(vm -> logger.warn(file.getName() + ": " + vm.getMessage()));
      }
    } catch (IOException e) {
      throw new MojoExecutionException("JSON validation failed! File: " + filePath, e);
    }
  }
}
