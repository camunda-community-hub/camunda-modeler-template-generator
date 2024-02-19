package org.camunda.community.template.generator.test.templatemultiple;

import org.camunda.community.template.generator.Template;
import org.camunda.community.template.generator.TemplateProperty;

public class TestTemplateMultiple {

  @TemplateProperty(
      label = "Example Property", //
      description = "Example of a property", //
      documentationRef = "https://docs.camunda.io", //
      parameterType = TemplateProperty.INPUT, //
      type = TemplateProperty.STRING, //
      value = "example", //
      notEmpty = true, //
      isEditable = false)
  String test = "test";

  @Template(
      name = "ExampleOne", //
      id = "com.example.ExampleOne", //
      description = "Example of a Template", //
      documentationRef = "https://docs.camunda.io", //
      appliesTo = {"bpmn:Task"}, //
      function = "exampleFunctionOne", //
      functionNameProperty = "exampleNamePropertyOne", //
      entriesVisible = false, //
      templateProperties = {
        @TemplateProperty(
            label = "Example Additional Property One", //
            description = "Example of an additional property", //
            documentationRef = "https://docs.camunda.io", //
            parameterType = TemplateProperty.INPUT, //
            type = TemplateProperty.STRING, //
            value = "exampleOne", //
            bindingName = "exampleAdditionalPropertyOne", //
            notEmpty = true, //
            isEditable = false)
      })
  public void testMethodOne() {}

  @Template(
      name = "ExampleTwo", //
      id = "com.example.ExampleTwo", //
      description = "Example of a Template", //
      documentationRef = "https://docs.camunda.io", //
      appliesTo = {"bpmn:Task"}, //
      function = "exampleFunctionTwo", //
      functionNameProperty = "exampleNamePropertyTwo", //
      entriesVisible = false, //
      templateProperties = {
        @TemplateProperty(
            label = "Example Additional Property Two", //
            description = "Example of an additional property", //
            documentationRef = "https://docs.camunda.io", //
            parameterType = TemplateProperty.OUTPUT, //
            type = TemplateProperty.STRING, //
            value = "exampleTwo", //
            bindingName = "exampleAdditionalPropertyTwo", //
            notEmpty = false, //
            isEditable = true)
      })
  public void testMethodTwo() {}
}
