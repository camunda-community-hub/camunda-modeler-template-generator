package org.camunda.community.template.generator.objectmodel;

/** A constrained that is contained by a property */
public class Constraint {

  private Boolean notEmpty;
  private Pattern pattern;

  public Constraint() {
    super();
  }

  /**
   * @return notEmpty
   */
  public Boolean isNotEmpty() {
    return notEmpty;
  }

  /**
   * @param notEmpty
   */
  public void setNotEmpty(Boolean notEmpty) {
    this.notEmpty = notEmpty;
  }

  /**
   * @return pattern
   */
  public Pattern getPattern() {
    return pattern;
  }

  /**
   * @param pattern
   */
  public void setPattern(Pattern pattern) {
    this.pattern = pattern;
  }
}
