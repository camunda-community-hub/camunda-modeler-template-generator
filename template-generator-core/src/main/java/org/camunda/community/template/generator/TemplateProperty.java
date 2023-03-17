package org.camunda.community.template.generator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation can be used to annotate fields and represents one property in a Template in the
 * Modeler.
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface TemplateProperty {

  public static final String STRING = "String";

  public static final String TEXT = "Text";

  public static final String DROPDOWN = "Dropdown";

  public static final String PROPERTY = "property";

  public static final String HIDDEN = "Hidden";

  public static final String INPUT = "input";

  public static final String OUTPUT = "output";

  /**
   * @return label of the property
   */
  public String label() default "";

  /**
   * @return type of the property
   */
  public String type();

  /**
   * @return value of the property
   */
  public String value() default "";

  /**
   * @return choices containing name and value per index
   */
  public Choice[] choices() default {};

  /**
   * @return description of the property
   */
  public String description() default "";

  /**
   * @return parameter type "input", "output" or "property"
   */
  public String parameterType() default INPUT;

  /**
   * @return binding name of the property
   */
  public String bindingName() default "";

  /**
   * @return scriptFormat of the property
   */
  public String scriptFormat() default "";

  /**
   * @return whether the property can be empty or not
   */
  public boolean notEmpty() default false;

  /**
   * @return whether the property should be editable by the user or not
   */
  public boolean isEditable() default true;
}
