package org.arc.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@Documented
public @interface Wire {

    /**
     * If true, also inject inherited fields.
     */
    boolean injectInherited() default false;

    /**
     * Throws a {@link NullPointerException} if field can't be injected.
     */
    boolean failOnNull() default true;

    String name() default "";

}
