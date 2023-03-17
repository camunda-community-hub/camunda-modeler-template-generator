package org.camunda.community.template.generator;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation should only be used in the choices field of the TemplateProperty annotation. It
 * represents one entry of a dropdown menu.
 */
@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface Choice {

  /**
   * @return name of the choice
   */
  public String name();

  /**
   * @return value of the choice
   */
  public String value();
}
