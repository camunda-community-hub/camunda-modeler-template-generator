package org.camunda.community.template.generator.test.templatemixed;

import org.camunda.community.template.generator.Template;
import org.camunda.community.template.generator.TemplateProperty;

public class TestTemplateMixed {

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
      name = "Example", //
      id = "com.example.Example", //
      description = "Example of a Template", //
      documentationRef = "https://docs.camunda.io", //
      appliesTo = {"bpmn:Task"}, //
      function = "exampleFunction", //
      functionNameProperty = "exampleNameProperty", //
      entriesVisible = false, //
      templateProperties = {
        @TemplateProperty(
            label = "Example Additional Property", //
            description = "Example of an additional property", //
            documentationRef = "https://docs.camunda.io", //
            parameterType = TemplateProperty.INPUT, //
            type = TemplateProperty.STRING, //
            value = "example", //
            bindingName = "exampleAdditionalProperty", //
            notEmpty = true, //
            isEditable = false)
      })
  public void testMethod() {}
}
