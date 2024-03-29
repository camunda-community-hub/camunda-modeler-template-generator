package org.camunda.community.template.generator.test.templateparameters;

import org.camunda.community.template.generator.Choice;
import org.camunda.community.template.generator.Template;
import org.camunda.community.template.generator.TemplateProperty;

public class TestTemplateParameters {

  @TemplateProperty(
      label = "Example Property", //
      description = "Example of a property", //
      documentationRef = "https://docs.camunda.io", //
      parameterType = TemplateProperty.INPUT, //
      type = TemplateProperty.DROPDOWN, //
      choices = {@Choice(name = "exampleChoice", value = "exampleChoiceValue")}, //
      value = "example", //
      notEmpty = true, //
      isEditable = false)
  String test = "test";

  @Template(
      name = "Example", //
      id = "com.example.Example", //
      description = "Example of a Template", //
      documentationRef = "https://docs.camunda.io", //
      appliesTo = {"bpmn:Task"}, //
      function = "exampleFunction", //
      functionNameProperty = "exampleNameProperty", //
      entriesVisible = false)
  public void testMethod() {}
}
