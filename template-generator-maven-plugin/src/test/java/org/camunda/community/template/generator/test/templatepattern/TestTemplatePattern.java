package org.camunda.community.template.generator.test.templatepattern;

import org.camunda.community.template.generator.Template;
import org.camunda.community.template.generator.TemplateProperty;

public class TestTemplatePattern {

  @TemplateProperty(
      label = "Property with pattern",
      type = TemplateProperty.STRING,
      pattern = "^[a-zA-Z0-9]+$",
      patternErrorMessage = "Only alphanumeric characters are allowed.")
  String propertyWithPattern = "prop1";

  @TemplateProperty(
      label = "Property with pattern and notEmpty",
      type = TemplateProperty.STRING,
      notEmpty = true,
      pattern = "^\\d{4}$",
      patternErrorMessage = "Must be a 4-digit number.")
  String propertyWithPatternAndNotEmpty = "prop2";

  @Template(
      name = "Pattern Example",
      id = "com.example.PatternExample",
      appliesTo = {"bpmn:ServiceTask"})
  public void testMethod() {}
}
