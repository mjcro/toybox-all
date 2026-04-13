package io.github.mjcro.toybox.api.dev;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Source-level marker annotation indicating that the annotated type or method
 * is the recommended approach for its use case.
 *
 * <p>This annotation is retained only in source code and has no runtime effect.</p>
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Recommended {
}
