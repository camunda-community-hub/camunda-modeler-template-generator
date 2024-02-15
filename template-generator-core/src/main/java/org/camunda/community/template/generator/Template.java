package org.camunda.community.template.generator;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The Template annotation can be used to annotate methods. This annotation is absolutely necessary
 * because only classes that contain this annotation will be scanned. It represents a Template in
 * the Modeler and contains the corresponding properties which are read from fields of the same
 * class hierarchy annotated with TemplateProperty.
 */
@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface Template {

  public static final String SERVICE_TASK = "bpmn:ServiceTask";

  /**
   * @return name of the activity
   */
  public String name();

  /**
   * @return id of the activity
   */
  public String id();

  /**
   * @return description of the activity
   */
  public String description() default "";

  /**
   * @return documentationRef of the activity
   */
  public String documentationRef() default "";

  /**
   * @return types the activity should apply to
   */
  public String[] appliesTo();

  /**
   * @return function of the activity
   */
  public String function() default "";

  /**
   * @return function name of the activity
   */
  public String functionNameProperty() default "";

  /**
   * @return list of additional TemplateProperty annotations for the activity
   */
  public TemplateProperty[] templateProperties() default {};

  /**
   * @return flag to display entries
   */
  public boolean entriesVisible() default true;
}
