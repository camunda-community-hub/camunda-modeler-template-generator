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
class GeneratorTest {

  List<ClassInfo> classInfosTemplate = new ArrayList<>();

  List<ClassInfo> classInfosTemplateParameters = new ArrayList<>();

  List<ClassInfo> classInfosTemplateMixed = new ArrayList<>();

  List<ClassInfo> classInfosTemplateMultiple = new ArrayList<>();

  List<ClassInfo> classInfosTemplateExternalTask = new ArrayList<>();

  List<ClassInfo> classInfosTemplatePattern = new ArrayList<>();

  ClassInfoList scan(String scanPackages) {
    ScanResult scanResult = new ClassGraph().acceptPackages(scanPackages).enableAllInfo().scan();

    return scanResult.getClassesWithMethodAnnotation(
        org.camunda.community.template.generator.Template.class.getName());
  }

  @BeforeEach
  void testSomething() {
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

    for (ClassInfo classInfo :
        scan("org.camunda.community.template.generator.test.templatepattern")) {
      classInfosTemplatePattern.clear();
      classInfosTemplatePattern.add(classInfo);
    }
  }

  @Test
  void testTemplate() {
    List<Template> templates = new ArrayList<>();

    Template template = new Template();
    template.setTemplateName("Example");
    template.setTemplateID("com.example.Example");
    template.setDescription("Example of a Template");
    template.setDocumentationRef("https://docs.camunda.io");
    template.setAppliesTo(new String[] {"bpmn:Task"});
    template.setEntriesVisible(false);

    Property templatePropertyExample = new Property();
    templatePropertyExample.setLabel("Example Additional Property");
    templatePropertyExample.setType(TemplateProperty.STRING);
    templatePropertyExample.setValue("example");
    templatePropertyExample.setDescription("Example of an additional property");
    templatePropertyExample.setDocumentationRef("https://docs.camunda.io");
    templatePropertyExample.setBinding(
        new Binding("camunda:inputParameter", "exampleAdditionalProperty"));
    templatePropertyExample.setEditable(false);
    Constraint constraint = new Constraint();
    constraint.setNotEmpty(true);
    templatePropertyExample.setConstraint(constraint);

    Property templateProperty = new Property();
    templateProperty.setType(TemplateProperty.HIDDEN);
    templateProperty.setValue(
        "org.camunda.community.template.generator.test.template.TestTemplate");
    templateProperty.setBinding(new Binding(TemplateProperty.PROPERTY, "camunda:class"));
    templateProperty.setEditable(false);

    Property templateFunctionProperty = new Property();
    templateFunctionProperty.setType(TemplateProperty.HIDDEN);
    templateFunctionProperty.setValue("exampleFunction");
    templateFunctionProperty.setBinding(
        new Binding("camunda:inputParameter", "exampleNameProperty"));
    templateFunctionProperty.setEditable(false);

    template.addTemplateProperty(templatePropertyExample);
    template.addTemplateProperty(templateFunctionProperty);
    template.addTemplateProperty(templateProperty);

    templates.add(template);

    List<Template> resultTemplates = GeneratorParser.processTemplates(classInfosTemplate.get(0));

    assertThat(resultTemplates).usingRecursiveComparison().isEqualTo(templates);
  }

  @Test
  void testTemplateParameters() {
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
    templatePropertyExample.setDocumentationRef("https://docs.camunda.io");
    templatePropertyExample.setBinding(new Binding("camunda:inputParameter", "test"));
    templatePropertyExample.setEditable(false);
    Constraint constraint = new Constraint();
    constraint.setNotEmpty(true);
    templatePropertyExample.setConstraint(constraint);

    properties.add(templatePropertyExample);

    List<Property> resultTemplates =
        GeneratorParser.processGlobalProperties(classInfosTemplateParameters.get(0));

    assertThat(resultTemplates).usingRecursiveComparison().isEqualTo(properties);
  }

  @Test
  void testTemplateMixed() {
    List<Template> templates = new ArrayList<>();

    Template template = new Template();
    template.setTemplateName("Example");
    template.setTemplateID("com.example.Example");
    template.setDescription("Example of a Template");
    template.setDocumentationRef("https://docs.camunda.io");
    template.setAppliesTo(new String[] {"bpmn:Task"});
    template.setEntriesVisible(false);

    Constraint constraint = new Constraint();
    constraint.setNotEmpty(true);

    Property templatePropertyExample = new Property();
    templatePropertyExample.setLabel("Example Property");
    templatePropertyExample.setType(TemplateProperty.STRING);
    templatePropertyExample.setValue("example");
    templatePropertyExample.setDescription("Example of a property");
    templatePropertyExample.setDocumentationRef("https://docs.camunda.io");
    templatePropertyExample.setBinding(new Binding("camunda:inputParameter", "test"));
    templatePropertyExample.setEditable(false);
    templatePropertyExample.setConstraint(constraint);

    Property templateAdditionalPropertyExample = new Property();
    templateAdditionalPropertyExample.setLabel("Example Additional Property");
    templateAdditionalPropertyExample.setType(TemplateProperty.STRING);
    templateAdditionalPropertyExample.setValue("example");
    templateAdditionalPropertyExample.setDescription("Example of an additional property");
    templateAdditionalPropertyExample.setDocumentationRef("https://docs.camunda.io");
    templateAdditionalPropertyExample.setBinding(
        new Binding("camunda:inputParameter", "exampleAdditionalProperty"));
    templateAdditionalPropertyExample.setEditable(false);
    Constraint constraint2 = new Constraint();
    constraint2.setNotEmpty(true);
    templateAdditionalPropertyExample.setConstraint(constraint2);

    Property templateProperty = new Property();
    templateProperty.setType(TemplateProperty.HIDDEN);
    templateProperty.setValue(
        "org.camunda.community.template.generator.test.templatemixed.TestTemplateMixed");
    templateProperty.setBinding(new Binding(TemplateProperty.PROPERTY, "camunda:class"));
    templateProperty.setEditable(false);

    Property templateFunctionProperty = new Property();
    templateFunctionProperty.setType(TemplateProperty.HIDDEN);
    templateFunctionProperty.setValue("exampleFunction");
    templateFunctionProperty.setBinding(
        new Binding("camunda:inputParameter", "exampleNameProperty"));
    templateFunctionProperty.setEditable(false);

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
  void testTemplateMultiple() {
    List<Template> templates = new ArrayList<>();

    Template templateOne = new Template();
    templateOne.setTemplateName("ExampleOne");
    templateOne.setTemplateID("com.example.ExampleOne");
    templateOne.setDescription("Example of a Template");
    templateOne.setDocumentationRef("https://docs.camunda.io");
    templateOne.setAppliesTo(new String[] {"bpmn:Task"});
    templateOne.setEntriesVisible(false);

    Template templateTwo = new Template();
    templateTwo.setTemplateName("ExampleTwo");
    templateTwo.setTemplateID("com.example.ExampleTwo");
    templateTwo.setDescription("Example of a Template");
    templateTwo.setDocumentationRef("https://docs.camunda.io");
    templateTwo.setAppliesTo(new String[] {"bpmn:Task"});
    templateTwo.setEntriesVisible(false);

    Property templatePropertyExample = new Property();
    templatePropertyExample.setLabel("Example Property");
    templatePropertyExample.setType(TemplateProperty.STRING);
    templatePropertyExample.setValue("example");
    templatePropertyExample.setDescription("Example of a property");
    templatePropertyExample.setDocumentationRef("https://docs.camunda.io");
    templatePropertyExample.setBinding(new Binding("camunda:inputParameter", "test"));
    templatePropertyExample.setEditable(false);
    Constraint constraint1 = new Constraint();
    constraint1.setNotEmpty(true);
    templatePropertyExample.setConstraint(constraint1);

    Property templateAdditionalPropertyExampleOne = new Property();
    templateAdditionalPropertyExampleOne.setLabel("Example Additional Property One");
    templateAdditionalPropertyExampleOne.setType(TemplateProperty.STRING);
    templateAdditionalPropertyExampleOne.setValue("exampleOne");
    templateAdditionalPropertyExampleOne.setDescription("Example of an additional property");
    templateAdditionalPropertyExampleOne.setDocumentationRef("https://docs.camunda.io");
    templateAdditionalPropertyExampleOne.setBinding(
        new Binding("camunda:inputParameter", "exampleAdditionalPropertyOne"));
    templateAdditionalPropertyExampleOne.setEditable(false);
    Constraint constraint2 = new Constraint();
    constraint2.setNotEmpty(true);
    templateAdditionalPropertyExampleOne.setConstraint(constraint2);

    Binding templateAdditionalPropertyTwoBinding = new Binding();
    templateAdditionalPropertyTwoBinding.setType("camunda:outputParameter");
    templateAdditionalPropertyTwoBinding.setSource("${exampleAdditionalPropertyTwo}");

    Property templateAdditionalPropertyExampleTwo = new Property();
    templateAdditionalPropertyExampleTwo.setLabel("Example Additional Property Two");
    templateAdditionalPropertyExampleTwo.setType(TemplateProperty.STRING);
    templateAdditionalPropertyExampleTwo.setValue("exampleTwo");
    templateAdditionalPropertyExampleTwo.setDescription("Example of an additional property");
    templateAdditionalPropertyExampleTwo.setDocumentationRef("https://docs.camunda.io");
    templateAdditionalPropertyExampleTwo.setBinding(templateAdditionalPropertyTwoBinding);
    templateAdditionalPropertyExampleTwo.setEditable(true);

    Property templateProperty = new Property();
    templateProperty.setType(TemplateProperty.HIDDEN);
    templateProperty.setValue(
        "org.camunda.community.template.generator.test.templatemultiple.TestTemplateMultiple");
    templateProperty.setBinding(new Binding(TemplateProperty.PROPERTY, "camunda:class"));
    templateProperty.setEditable(false);

    Property templateFunctionPropertyOne = new Property();
    templateFunctionPropertyOne.setType(TemplateProperty.HIDDEN);
    templateFunctionPropertyOne.setValue("exampleFunctionOne");
    templateFunctionPropertyOne.setBinding(
        new Binding("camunda:inputParameter", "exampleNamePropertyOne"));
    templateFunctionPropertyOne.setEditable(false);

    Property templateFunctionPropertyTwo = new Property();
    templateFunctionPropertyTwo.setType(TemplateProperty.HIDDEN);
    templateFunctionPropertyTwo.setValue("exampleFunctionTwo");
    templateFunctionPropertyTwo.setBinding(
        new Binding("camunda:inputParameter", "exampleNamePropertyTwo"));
    templateFunctionPropertyTwo.setEditable(false);

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
  void testTemplateExternalTask() {
    List<Template> templates = new ArrayList<>();

    Template template = new Template();
    template.setTemplateName("External Task Example");
    template.setTemplateID("com.example.ExternalTaskExample");
    template.setDescription("Example of a Template");
    template.setDocumentationRef("https://docs.camunda.io");
    template.setAppliesTo(new String[] {"bpmn:Task"});
    template.setEntriesVisible(false);

    Property templatePropertyExample = new Property();
    templatePropertyExample.setLabel("External Task Example Additional Property");
    templatePropertyExample.setType(TemplateProperty.STRING);
    templatePropertyExample.setValue("example");
    templatePropertyExample.setDescription("Example of an additional property");
    templatePropertyExample.setDocumentationRef("https://docs.camunda.io");
    templatePropertyExample.setBinding(
        new Binding("camunda:inputParameter", "exampleAdditionalProperty"));
    templatePropertyExample.setEditable(false);
    Constraint constraint1 = new Constraint();
    constraint1.setNotEmpty(true);
    templatePropertyExample.setConstraint(constraint1);

    Property externalTaskFunctionProperty = new Property();
    externalTaskFunctionProperty.setType(TemplateProperty.HIDDEN);
    externalTaskFunctionProperty.setValue("external");
    externalTaskFunctionProperty.setEditable(false);
    externalTaskFunctionProperty.setBinding(new Binding(TemplateProperty.PROPERTY, "camunda:type"));

    Property externalTaskTopic = new Property();
    externalTaskTopic.setLabel("External Topic");
    externalTaskTopic.setType(TemplateProperty.STRING);
    externalTaskTopic.setEditable(true);
    Constraint constraint2 = new Constraint();
    constraint2.setNotEmpty(true);
    externalTaskTopic.setConstraint(constraint2);
    externalTaskTopic.setBinding(new Binding(TemplateProperty.PROPERTY, "camunda:topic"));

    Property externalTaskPriority = new Property();
    externalTaskPriority.setLabel("External Task Priority");
    externalTaskPriority.setType(TemplateProperty.STRING);
    externalTaskPriority.setEditable(true);
    externalTaskPriority.setBinding(new Binding(TemplateProperty.PROPERTY, "camunda:taskPriority"));

    Property templateProperty = new Property();
    templateProperty.setType(TemplateProperty.HIDDEN);
    templateProperty.setValue(
        "org.camunda.community.template.generator.test.templateexternaltask.TestTemplateExternalTask");
    templateProperty.setBinding(new Binding(TemplateProperty.PROPERTY, "camunda:class"));
    templateProperty.setEditable(false);

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
  void testTemplatePattern() {
    List<Template> templates = new ArrayList<>();

    Template template = new Template();
    template.setTemplateName("Pattern Example");
    template.setTemplateID("com.example.PatternExample");
    template.setAppliesTo(new String[] {"bpmn:ServiceTask"});
    template.setEntriesVisible(true);

    Property prop1 = new Property();
    prop1.setLabel("Property with pattern");
    prop1.setType(TemplateProperty.STRING);
    prop1.setEditable(true);
    prop1.setBinding(new Binding(INPUT_PARAMETER, "propertyWithPattern"));
    Constraint constraint1 = new Constraint();
    constraint1.setPattern(
        new org.camunda.community.template.generator.objectmodel.Pattern(
            "^[a-zA-Z0-9]+$", "Only alphanumeric characters are allowed."));
    prop1.setConstraint(constraint1);

    Property prop2 = new Property();
    prop2.setLabel("Property with pattern and notEmpty");
    prop2.setType(TemplateProperty.STRING);
    prop2.setEditable(true);
    prop2.setBinding(new Binding(INPUT_PARAMETER, "propertyWithPatternAndNotEmpty"));
    Constraint constraint2 = new Constraint();
    constraint2.setNotEmpty(true);
    constraint2.setPattern(
        new org.camunda.community.template.generator.objectmodel.Pattern(
            "^\\d{4}$", "Must be a 4-digit number."));
    prop2.setConstraint(constraint2);

    Property implProperty = new Property();
    implProperty.setType(TemplateProperty.HIDDEN);
    implProperty.setValue(
        "org.camunda.community.template.generator.test.templatepattern.TestTemplatePattern");
    implProperty.setBinding(new Binding(TemplateProperty.PROPERTY, "camunda:class"));
    implProperty.setEditable(false);

    template.addTemplateProperty(prop1);
    template.addTemplateProperty(prop2);
    template.addTemplateProperty(implProperty);

    templates.add(template);

    List<Template> resultTemplates =
        GeneratorParser.processTemplates(classInfosTemplatePattern.get(0));

    assertThat(resultTemplates).usingRecursiveComparison().isEqualTo(templates);
  }

  @Test
  void testTemplateJsonOutput() throws MojoExecutionException, IOException {
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
  void testTemplateMixedJsonOutput() throws MojoExecutionException, IOException {
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
  void testTemplateParametersJsonOutput() throws MojoExecutionException, IOException {
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
  void testTemplateMultipleJsonOutput() throws MojoExecutionException, IOException {
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
  void testTemplateExternalTaskJsonOutput() throws MojoExecutionException, IOException {
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
  void testTemplatePatternJsonOutput() throws MojoExecutionException, IOException {
    new Generator()
        .generate(
            "0.12.0",
            "org.camunda.community.template.generator.test.templatepattern",
            "target/test-files/resources/actual",
            false);

    assertEquals(
        FileUtils.fileRead(
                new File("src/test/resources/test-expected/TestTemplatePatternTemplates.json"))
            .replace("\n", "")
            .replace("\r", ""),
        FileUtils.fileRead(
                new File("target/test-files/resources/actual/TestTemplatePatternTemplates.json"))
            .replace("\n", "")
            .replace("\r", ""),
        "Files are not equal!");
  }

  @Test
  void testMalformedJSON() throws Exception {
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
  void testMalformedSchemaURL() {
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
  void testBindingScriptFormat() {
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
  void testDuplicateTemplateID() {
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
