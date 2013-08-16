package org.dynamicextensions.testrunner.osgi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Laurent Van der Linden
 *
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExportOsgiService {

    Class[] interfaces() default {};

    /**
     * The maximum version number in "&lt;major&gt;.&lt;minor&gt;.&lt;micro&gt;" format. (The micro version part is
     * optional.)
     *
     */
    ExportHeader[] headers() default {};

    static @interface ExportHeader {
        String key();
        String value();
    }
}
