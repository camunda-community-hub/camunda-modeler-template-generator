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
import java.io.*;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.maven.plugin.MojoExecutionException;

public class Generator {

  Logger logger = Logger.getLogger(Generator.class.getName());

  public void generate(
      String schemaVersion, String scanPackages, String outputDir, boolean skipValidation)
      throws MojoExecutionException {
    // Set schema URL
    String schemaURL =
        SCHEMA_BASE_URL
            + (schemaVersion.equals("null") ? "" : "@" + schemaVersion)
            + "/resources/schema.json";

    logger.info("Scanning for annotations ...");

    ScanResult scanResult = new ClassGraph().acceptPackages(scanPackages).enableAllInfo().scan();
    ClassInfoList classInfoList =
        scanResult.getClassesWithMethodAnnotation(
            org.camunda.community.template.generator.Template.class.getName());

    Set<String> templateIDs = new HashSet<>();

    // Iterate through all classes containing a Template annotation
    for (ClassInfo classInfo : classInfoList) {
      if (classInfo.hasDeclaredMethodAnnotation(
          org.camunda.community.template.generator.Template.class.getName())) {
        // Set template file output path
        String filePath = outputDir + File.separator + classInfo.getSimpleName() + "Templates.json";

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

        // Serialize object model to JSON
        String resultJSON = (new GsonBuilder()).setPrettyPrinting().create().toJson(templates);
        writeJsonToFile(filePath, resultJSON);

        // Validate JSON file
        if (!skipValidation) {
          // Download schema for validation
          String schema = downloadSchema(schemaURL);

          // Validate file using schema
          validateJsonFile(filePath, schema);
        } else {
          logger.warning("Skipping JSON schema validation!");
        }
      }
    }
  }

  /**
   * Writes the JSON String to a specific file path.
   *
   * @param filePath The path where to save the specified JSON String
   * @param json The JSON String to write
   * @throws MojoExecutionException
   */
  private void writeJsonToFile(String filePath, String json) throws MojoExecutionException {
    File file = new File(filePath);
    file.getParentFile().mkdirs();

    try (FileWriter outputFile = new FileWriter(file)) {
      outputFile.write(json);
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
  public String downloadSchema(String schemaURL) throws MojoExecutionException {
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

    return schema.toString();
  }

  /**
   * Validates a JSON file against the provided schema
   *
   * @param filePath The file path to validate
   * @param schemaTemplate The schema template to use for validation
   * @throws MojoExecutionException
   */
  public void validateJsonFile(String filePath, String schemaTemplate)
      throws MojoExecutionException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

    try {
      File file = new File(filePath);
      JsonNode json = objectMapper.readTree(new FileInputStream(file));
      JsonSchema schema =
          schemaFactory.getSchema(new ByteArrayInputStream(schemaTemplate.getBytes()));
      Set<ValidationMessage> validationResult = schema.validate(json);

      // Print validation errors
      if (validationResult.isEmpty()) {
        logger.info(file.getName() + ": Validation successful");
      } else {
        validationResult.forEach(vm -> logger.warning(file.getName() + ": " + vm.getMessage()));
      }
    } catch (IOException e) {
      throw new MojoExecutionException("JSON validation failed! File: " + filePath, e);
    }
  }
}
