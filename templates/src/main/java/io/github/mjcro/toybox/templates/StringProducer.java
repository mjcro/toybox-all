package io.github.mjcro.toybox.templates;

import java.util.Optional;

/**
 * Defines string producers that can be used in string templates.
 * <p>
 * Standard usecase:
 * - Mutable class
 * - Having fields with {@link Databind} annotation.
 * - Implements {@link StringProducer} to generate string using data from fields.
 */
public interface StringProducer {
    /**
     * Produces string result and writes it into given string builds.
     *
     * @param sb String builder to write data into.
     * @throws Exception Any exception.
     */
    void produce(StringBuilder sb) throws Exception;

    /**
     * Produces and returns string result.
     *
     * @return Produced result.
     * @throws Exception Any exception.
     */
    default String produceString() throws Exception {
        StringBuilder sb = new StringBuilder();
        produce(sb);
        return sb.toString();
    }

    /**
     * Defines initial string that can be read during
     * component initialization. This string will be
     * displayed on template output and may contain
     * help information or hints.
     *
     * @return Initial string.
     */
    default Optional<String> getInitialString() {
        return Optional.empty();
    }
}
