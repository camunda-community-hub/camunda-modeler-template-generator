package org.camunda.community.template.generator.test.templateexternaltask;

import org.camunda.community.template.generator.Template;
import org.camunda.community.template.generator.TemplateProperty;

public class TestTemplateExternalTask {

  String test = "test";

  @Template(
      name = "External Task Example", //
      id = "com.example.ExternalTaskExample", //
      description = "Example of a Template", //
      documentationRef = "https://docs.camunda.io", //
      appliesTo = {"bpmn:Task"}, //
      entriesVisible = false, //
      templateProperties = {
        @TemplateProperty(
            label = "External Task Example Additional Property", //
            description = "Example of an additional property", //
            documentationRef = "https://docs.camunda.io", //
            parameterType = TemplateProperty.INPUT, //
            type = TemplateProperty.STRING, //
            value = "example", //
            bindingName = "exampleAdditionalProperty", //
            notEmpty = true, //
            isEditable = false), //
        @TemplateProperty(
            type = TemplateProperty.HIDDEN, //
            value = "external", //
            isEditable = false, //
            parameterType = TemplateProperty.PROPERTY, //
            bindingName = "type"), //
        @TemplateProperty(
            type = TemplateProperty.STRING, //
            label = "External Topic", //
            notEmpty = true, //
            parameterType = TemplateProperty.PROPERTY, //
            bindingName = "topic"), //
        @TemplateProperty(
            type = TemplateProperty.STRING, //
            label = "External Task Priority", //
            parameterType = TemplateProperty.PROPERTY, //
            bindingName = "taskPriority")
      })
  public void testMethod() {}
}
