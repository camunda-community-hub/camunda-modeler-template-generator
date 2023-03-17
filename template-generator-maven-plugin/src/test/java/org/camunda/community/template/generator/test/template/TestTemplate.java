package org.camunda.community.template.generator.test.template;

import org.camunda.community.template.generator.Template;
import org.camunda.community.template.generator.TemplateProperty;

public class TestTemplate {

  String test = "test";

  @Template(
      name = "Example", //
      id = "com.example.Example", //
      appliesTo = {"bpmn:Task"}, //
      function = "exampleFunction", //
      functionNameProperty = "exampleNameProperty", //
      entriesVisible = false, //
      templateProperties = {
        @TemplateProperty(
            label = "Example Additional Property", //
            description = "Example of an additional property", //
            parameterType = TemplateProperty.INPUT, //
            type = TemplateProperty.STRING, //
            value = "example", //
            bindingName = "exampleAdditionalProperty", //
            notEmpty = true, //
            isEditable = false)
      })
  public void testMethod() {}
}
