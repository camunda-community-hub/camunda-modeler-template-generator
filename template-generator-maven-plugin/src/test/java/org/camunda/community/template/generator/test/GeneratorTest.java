package org.camunda.community.template.generator.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.community.template.generator.GeneratorParser.INPUT_PARAMETER;
import static org.camunda.community.template.generator.GeneratorPlugin.SCHEMA_BASE_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.networknt.schema.JsonSchema;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.camunda.community.template.generator.Generator;
import org.camunda.community.template.generator.GeneratorParser;
import org.camunda.community.template.generator.TemplateProperty;
import org.camunda.community.template.generator.objectmodel.Binding;
import org.camunda.community.template.generator.objectmodel.Choice;
import org.camunda.community.template.generator.objectmodel.Constraint;
import org.camunda.community.template.generator.objectmodel.Property;
import org.camunda.community.template.generator.objectmodel.Template;
import org.codehaus.plexus.util.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GeneratorTest {

  List<ClassInfo> classInfosTemplate = new ArrayList<>();

  List<ClassInfo> classInfosTemplateParameters = new ArrayList<>();

  List<ClassInfo> classInfosTemplateMixed = new ArrayList<>();

  List<ClassInfo> classInfosTemplateMultiple = new ArrayList<>();

  List<ClassInfo> classInfosTemplateExternalTask = new ArrayList<>();

  ClassInfoList scan(String scanPackages) {
    ScanResult scanResult = new ClassGraph().acceptPackages(scanPackages).enableAllInfo().scan();

    return scanResult.getClassesWithMethodAnnotation(
        org.camunda.community.template.generator.Template.class.getName());
  }

  @BeforeEach
  public void testSomething() {
    for (ClassInfo classInfo : scan("org.camunda.community.template.generator.test.template")) {
      classInfosTemplate.clear();
      classInfosTemplate.add(classInfo);
    }

    for (ClassInfo classInfo :
        scan("org.camunda.community.template.generator.test.templateparameters")) {
      classInfosTemplateParameters.clear();
      classInfosTemplateParameters.add(classInfo);
    }

    for (ClassInfo classInfo :
        scan("org.camunda.community.template.generator.test.templatemixed")) {
      classInfosTemplateMixed.clear();
      classInfosTemplateMixed.add(classInfo);
    }

    for (ClassInfo classInfo :
        scan("org.camunda.community.template.generator.test.templatemultiple")) {
      classInfosTemplateMultiple.clear();
      classInfosTemplateMultiple.add(classInfo);
    }

    for (ClassInfo classInfo :
        scan("org.camunda.community.template.generator.test.templateexternaltask")) {
      classInfosTemplateExternalTask.clear();
      classInfosTemplateExternalTask.add(classInfo);
    }
  }

  @Test
  public void testTemplate() {
    List<Template> templates = new ArrayList<>();

    Template template = new Template();
    template.setTemplateName("Example");
    template.setTemplateID("com.example.Example");
    template.setAppliesTo(new String[] {"bpmn:Task"});
    template.setEntriesVisible(false);

    Property templatePropertyExample = new Property();
    templatePropertyExample.setLabel("Example Additional Property");
    templatePropertyExample.setType(TemplateProperty.STRING);
    templatePropertyExample.setValue("example");
    templatePropertyExample.setDescription("Example of an additional property");
    templatePropertyExample.setBinding(
        new Binding("camunda:inputParameter", "exampleAdditionalProperty"));
    templatePropertyExample.setEditable(false);
    templatePropertyExample.setConstraint(new Constraint(true));

    Property templateProperty = new Property();
    templateProperty.setType(TemplateProperty.HIDDEN);
    templateProperty.setValue(
        "org.camunda.community.template.generator.test.template.TestTemplate");
    templateProperty.setBinding(new Binding(TemplateProperty.PROPERTY, "camunda:class"));

    Property templateFunctionProperty = new Property();
    templateFunctionProperty.setType(TemplateProperty.HIDDEN);
    templateFunctionProperty.setValue("exampleFunction");
    templateFunctionProperty.setBinding(
        new Binding("camunda:inputParameter", "exampleNameProperty"));

    template.addTemplateProperty(templatePropertyExample);
    template.addTemplateProperty(templateFunctionProperty);
    template.addTemplateProperty(templateProperty);

    templates.add(template);

    List<Template> resultTemplates = GeneratorParser.processTemplates(classInfosTemplate.get(0));

    assertThat(resultTemplates).usingRecursiveComparison().isEqualTo(templates);
  }

  @Test
  public void testTemplateParameters() {
    List<Property> properties = new ArrayList<>();

    Choice exampleChoice = new Choice("", "");
    exampleChoice.setName("exampleChoice");
    exampleChoice.setValue("exampleChoiceValue");

    Property templatePropertyExample = new Property();
    templatePropertyExample.setLabel("Example Property");
    templatePropertyExample.setType("Dropdown");
    templatePropertyExample.setChoices(new Choice[] {exampleChoice});
    templatePropertyExample.setValue("example");
    templatePropertyExample.setDescription("Example of a property");
    templatePropertyExample.setBinding(new Binding("camunda:inputParameter", "null"));
    templatePropertyExample.setEditable(false);
    templatePropertyExample.setConstraint(new Constraint(true));

    properties.add(templatePropertyExample);

    List<Property> resultTemplates =
        GeneratorParser.processGlobalProperties(classInfosTemplateParameters.get(0));

    assertThat(resultTemplates).usingRecursiveComparison().isEqualTo(properties);
  }

  @Test
  public void testTemplateMixed() {
    List<Template> templates = new ArrayList<>();

    Template template = new Template();
    template.setTemplateName("Example");
    template.setTemplateID("com.example.Example");
    template.setAppliesTo(new String[] {"bpmn:Task"});
    template.setEntriesVisible(false);

    Constraint constraint = new Constraint(false);
    constraint.setNotEmpty(true);

    Property templatePropertyExample = new Property();
    templatePropertyExample.setLabel("Example Property");
    templatePropertyExample.setType(TemplateProperty.STRING);
    templatePropertyExample.setValue("example");
    templatePropertyExample.setDescription("Example of a property");
    templatePropertyExample.setBinding(new Binding("camunda:inputParameter", "null"));
    templatePropertyExample.setEditable(false);
    templatePropertyExample.setConstraint(constraint);

    Property templateAdditionalPropertyExample = new Property();
    templateAdditionalPropertyExample.setLabel("Example Additional Property");
    templateAdditionalPropertyExample.setType(TemplateProperty.STRING);
    templateAdditionalPropertyExample.setValue("example");
    templateAdditionalPropertyExample.setDescription("Example of an additional property");
    templateAdditionalPropertyExample.setBinding(
        new Binding("camunda:inputParameter", "exampleAdditionalProperty"));
    templateAdditionalPropertyExample.setEditable(false);
    templateAdditionalPropertyExample.setConstraint(new Constraint(true));

    Property templateProperty = new Property();
    templateProperty.setType(TemplateProperty.HIDDEN);
    templateProperty.setValue(
        "org.camunda.community.template.generator.test.templatemixed.TestTemplateMixed");
    templateProperty.setBinding(new Binding(TemplateProperty.PROPERTY, "camunda:class"));

    Property templateFunctionProperty = new Property();
    templateFunctionProperty.setType(TemplateProperty.HIDDEN);
    templateFunctionProperty.setValue("exampleFunction");
    templateFunctionProperty.setBinding(
        new Binding("camunda:inputParameter", "exampleNameProperty"));

    template.addTemplateProperty(templateAdditionalPropertyExample);
    template.addTemplateProperty(templateFunctionProperty);
    template.addTemplateProperty(templatePropertyExample);
    template.addTemplateProperty(templateProperty);

    templates.add(template);

    List<Template> resultTemplates =
        GeneratorParser.processTemplates(classInfosTemplateMixed.get(0));

    assertThat(resultTemplates).usingRecursiveComparison().isEqualTo(templates);
  }

  @Test
  public void testTemplateMultiple() {
    List<Template> templates = new ArrayList<>();

    Template templateOne = new Template();
    templateOne.setTemplateName("ExampleOne");
    templateOne.setTemplateID("com.example.ExampleOne");
    templateOne.setAppliesTo(new String[] {"bpmn:Task"});
    templateOne.setEntriesVisible(false);

    Template templateTwo = new Template();
    templateTwo.setTemplateName("ExampleTwo");
    templateTwo.setTemplateID("com.example.ExampleTwo");
    templateTwo.setAppliesTo(new String[] {"bpmn:Task"});
    templateTwo.setEntriesVisible(false);

    Property templatePropertyExample = new Property();
    templatePropertyExample.setLabel("Example Property");
    templatePropertyExample.setType(TemplateProperty.STRING);
    templatePropertyExample.setValue("example");
    templatePropertyExample.setDescription("Example of a property");
    templatePropertyExample.setBinding(new Binding("camunda:inputParameter", "null"));
    templatePropertyExample.setEditable(false);
    templatePropertyExample.setConstraint(new Constraint(true));

    Property templateAdditionalPropertyExampleOne = new Property();
    templateAdditionalPropertyExampleOne.setLabel("Example Additional Property One");
    templateAdditionalPropertyExampleOne.setType(TemplateProperty.STRING);
    templateAdditionalPropertyExampleOne.setValue("exampleOne");
    templateAdditionalPropertyExampleOne.setDescription("Example of an additional property");
    templateAdditionalPropertyExampleOne.setBinding(
        new Binding("camunda:inputParameter", "exampleAdditionalPropertyOne"));
    templateAdditionalPropertyExampleOne.setEditable(false);
    templateAdditionalPropertyExampleOne.setConstraint(new Constraint(true));

    Binding templateAdditionalPropertyTwoBinding = new Binding();
    templateAdditionalPropertyTwoBinding.setType("camunda:outputParameter");
    templateAdditionalPropertyTwoBinding.setSource("${exampleAdditionalPropertyTwo}");

    Property templateAdditionalPropertyExampleTwo = new Property();
    templateAdditionalPropertyExampleTwo.setLabel("Example Additional Property Two");
    templateAdditionalPropertyExampleTwo.setType(TemplateProperty.STRING);
    templateAdditionalPropertyExampleTwo.setValue("exampleTwo");
    templateAdditionalPropertyExampleTwo.setDescription("Example of an additional property");
    templateAdditionalPropertyExampleTwo.setBinding(templateAdditionalPropertyTwoBinding);
    templateAdditionalPropertyExampleTwo.setEditable(true);
    templateAdditionalPropertyExampleTwo.setConstraint(new Constraint(false));

    Property templateProperty = new Property();
    templateProperty.setType(TemplateProperty.HIDDEN);
    templateProperty.setValue(
        "org.camunda.community.template.generator.test.templatemultiple.TestTemplateMultiple");
    templateProperty.setBinding(new Binding(TemplateProperty.PROPERTY, "camunda:class"));

    Property templateFunctionPropertyOne = new Property();
    templateFunctionPropertyOne.setType(TemplateProperty.HIDDEN);
    templateFunctionPropertyOne.setValue("exampleFunctionOne");
    templateFunctionPropertyOne.setBinding(
        new Binding("camunda:inputParameter", "exampleNamePropertyOne"));

    Property templateFunctionPropertyTwo = new Property();
    templateFunctionPropertyTwo.setType(TemplateProperty.HIDDEN);
    templateFunctionPropertyTwo.setValue("exampleFunctionTwo");
    templateFunctionPropertyTwo.setBinding(
        new Binding("camunda:inputParameter", "exampleNamePropertyTwo"));

    templateOne.addTemplateProperty(templateAdditionalPropertyExampleOne);
    templateOne.addTemplateProperty(templateFunctionPropertyOne);
    templateOne.addTemplateProperty(templatePropertyExample);
    templateOne.addTemplateProperty(templateProperty);

    templateTwo.addTemplateProperty(templateAdditionalPropertyExampleTwo);
    templateTwo.addTemplateProperty(templateFunctionPropertyTwo);
    templateTwo.addTemplateProperty(templatePropertyExample);
    templateTwo.addTemplateProperty(templateProperty);

    templates.add(templateOne);
    templates.add(templateTwo);

    List<Template> resultTemplates =
        GeneratorParser.processTemplates(classInfosTemplateMultiple.get(0));

    assertThat(resultTemplates).usingRecursiveComparison().isEqualTo(templates);
  }

  @Test
  public void testTemplateExternalTask() {
    List<Template> templates = new ArrayList<>();

    Template template = new Template();
    template.setTemplateName("External Task Example");
    template.setTemplateID("com.example.ExternalTaskExample");
    template.setAppliesTo(new String[] {"bpmn:Task"});
    template.setEntriesVisible(false);

    Property templatePropertyExample = new Property();
    templatePropertyExample.setLabel("External Task Example Additional Property");
    templatePropertyExample.setType(TemplateProperty.STRING);
    templatePropertyExample.setValue("example");
    templatePropertyExample.setDescription("Example of an additional property");
    templatePropertyExample.setBinding(
        new Binding("camunda:inputParameter", "exampleAdditionalProperty"));
    templatePropertyExample.setEditable(false);
    templatePropertyExample.setConstraint(new Constraint(true));

    Property externalTaskFunctionProperty = new Property();
    externalTaskFunctionProperty.setType(TemplateProperty.HIDDEN);
    externalTaskFunctionProperty.setValue("external");
    externalTaskFunctionProperty.setEditable(false);
    externalTaskFunctionProperty.setBinding(new Binding(TemplateProperty.PROPERTY, "camunda:type"));
    externalTaskFunctionProperty.setConstraint(new Constraint(false));

    Property externalTaskTopic = new Property();
    externalTaskTopic.setLabel("External Topic");
    externalTaskTopic.setType(TemplateProperty.STRING);
    externalTaskTopic.setEditable(true);
    externalTaskTopic.setConstraint(new Constraint(true));
    externalTaskTopic.setBinding(new Binding(TemplateProperty.PROPERTY, "camunda:topic"));

    Property externalTaskPriority = new Property();
    externalTaskPriority.setLabel("External Task Priority");
    externalTaskPriority.setType(TemplateProperty.STRING);
    externalTaskPriority.setEditable(true);
    externalTaskPriority.setConstraint(new Constraint(false));
    externalTaskPriority.setBinding(new Binding(TemplateProperty.PROPERTY, "camunda:taskPriority"));

    Property templateProperty = new Property();
    templateProperty.setType(TemplateProperty.HIDDEN);
    templateProperty.setValue(
        "org.camunda.community.template.generator.test.templateexternaltask.TestTemplateExternalTask");
    templateProperty.setBinding(new Binding(TemplateProperty.PROPERTY, "camunda:class"));

    template.addTemplateProperty(templatePropertyExample);
    template.addTemplateProperty(externalTaskFunctionProperty);
    template.addTemplateProperty(externalTaskTopic);
    template.addTemplateProperty(externalTaskPriority);
    template.addTemplateProperty(templateProperty);

    templates.add(template);

    List<Template> resultTemplates =
        GeneratorParser.processTemplates(classInfosTemplateExternalTask.get(0));

    assertThat(resultTemplates).usingRecursiveComparison().isEqualTo(templates);
  }

  @Test
  public void testTemplateJsonOutput() throws MojoExecutionException, IOException {
    new Generator()
        .generate(
            "0.12.0",
            "org.camunda.community.template.generator.test.template",
            "target/test-files/resources/actual",
            false);

    assertEquals(
        FileUtils.fileRead(new File("src/test/resources/test-expected/TestTemplateTemplates.json"))
            .replace("\n", "")
            .replace("\r", ""),
        FileUtils.fileRead(
                new File("target/test-files/resources/actual/TestTemplateTemplates.json"))
            .replace("\n", "")
            .replace("\r", ""),
        "Files are not equal!");
  }

  @Test
  public void testTemplateMixedJsonOutput() throws MojoExecutionException, IOException {
    new Generator()
        .generate(
            "0.12.0",
            "org.camunda.community.template.generator.test.templatemixed",
            "target/test-files/resources/actual",
            false);

    assertEquals(
        FileUtils.fileRead(
                new File("src/test/resources/test-expected/TestTemplateMixedTemplates.json"))
            .replace("\n", "")
            .replace("\r", ""),
        FileUtils.fileRead(
                new File("target/test-files/resources/actual/TestTemplateMixedTemplates.json"))
            .replace("\n", "")
            .replace("\r", ""),
        "Files are not equal!");
  }

  @Test
  public void testTemplateParametersJsonOutput() throws MojoExecutionException, IOException {
    new Generator()
        .generate(
            "0.12.0",
            "org.camunda.community.template.generator.test.templateparameters",
            "target/test-files/resources/actual",
            false);

    assertEquals(
        FileUtils.fileRead(
                new File("src/test/resources/test-expected/TestTemplateParametersTemplates.json"))
            .replace("\n", "")
            .replace("\r", ""),
        FileUtils.fileRead(
                new File("target/test-files/resources/actual/TestTemplateParametersTemplates.json"))
            .replace("\n", "")
            .replace("\r", ""),
        "Files are not equal!");
  }

  @Test
  public void testTemplateMultipleJsonOutput() throws MojoExecutionException, IOException {
    new Generator()
        .generate(
            "0.12.0",
            "org.camunda.community.template.generator.test.templatemultiple",
            "target/test-files/resources/actual",
            true);

    assertEquals(
        FileUtils.fileRead(
                new File("src/test/resources/test-expected/TestTemplateMultipleTemplates.json"))
            .replace("\n", "")
            .replace("\r", ""),
        FileUtils.fileRead(
                new File("target/test-files/resources/actual/TestTemplateMultipleTemplates.json"))
            .replace("\n", "")
            .replace("\r", ""),
        "Files are not equal!");
  }

  @Test
  public void testTemplateExternalTaskJsonOutput() throws MojoExecutionException, IOException {
    new Generator()
        .generate(
            "0.12.0",
            "org.camunda.community.template.generator.test.templateexternaltask",
            "target/test-files/resources/actual",
            false);

    assertEquals(
        FileUtils.fileRead(
                new File("src/test/resources/test-expected/TestTemplateExternalTaskTemplates.json"))
            .replace("\n", "")
            .replace("\r", ""),
        FileUtils.fileRead(
                new File(
                    "target/test-files/resources/actual/TestTemplateExternalTaskTemplates.json"))
            .replace("\n", "")
            .replace("\r", ""),
        "Files are not equal!");
  }

  @Test
  public void testMalformedJSON() throws Exception {
    JsonSchema schema =
        new Generator().downloadSchema(SCHEMA_BASE_URL + "@0.12.0" + "/resources/schema.json");

    Exception exception =
        assertThrows(
            MojoExecutionException.class,
            () -> {
              new Generator()
                  .validateJsonFile(
                      "src/test/resources/test-expected/TestMalformedJSON.json", schema);
            });

    String expectedMessage =
        "JSON validation failed! File: src/test/resources/test-expected/TestMalformedJSON.json";
    String actualMessage = exception.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

  @Test
  public void testMalformedSchemaURL() {
    Exception exception =
        assertThrows(
            MojoExecutionException.class,
            () -> {
              new Generator().downloadSchema("malformedTestURL");
            });

    String expectedMessage =
        "Failed to download schema: malformedTestURL (If you are offline consider to set skipValidation to true)";
    String actualMessage = exception.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

  @Test
  public void testBindingScriptFormat() {
    Binding expectedBinding = new Binding();
    expectedBinding.setType(INPUT_PARAMETER);
    expectedBinding.setName("exampleBinding");
    expectedBinding.setScriptFormat("exampleScriptFormat");

    Binding actualBinding =
        GeneratorParser.parseBinding(
            TemplateProperty.INPUT, "exampleBinding", "exampleScriptFormat");

    assertThat(actualBinding).usingRecursiveComparison().isEqualTo(expectedBinding);
  }

  @Test
  public void testDuplicateTemplateID() {
    Exception exception =
        assertThrows(
            MojoExecutionException.class,
            () -> {
              new Generator()
                  .generate(
                      "0.12.0",
                      "org.camunda.community.template.generator.test.duplicatetemplateid",
                      "target/test-files/resources/duplicateid",
                      false);
            });

    String expectedMessage = "Duplicate template id found: com.example.Example";
    String actualMessage = exception.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}
