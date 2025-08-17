package org.camunda.community.template.generator.objectmodel;

/** Pattern for a constraint */
public class Pattern {

  private String value;
  private String message;

  public Pattern() {}

  public Pattern(String value, String message) {
    this.value = value;
    this.message = message;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
