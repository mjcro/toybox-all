package io.github.mjcro.toybox.templates;

import org.jspecify.annotations.NonNull;

import java.util.Optional;

/**
 * Produces string output from data fields annotated with {@link Databind}.
 * <p>
 * Typical usage: a mutable class with {@link Databind}-annotated fields
 * implements this interface to generate a string from those field values.
 */
public interface StringProducer {
    /**
     * Produces string output and appends it to the given builder.
     *
     * @param sb the string builder to write data into
     * @throws Exception on any error during production
     */
    void produce(@NonNull StringBuilder sb) throws Exception;

    /**
     * Produces and returns the string result.
     *
     * @return the produced string
     * @throws Exception on any error during production
     */
    default @NonNull String produceString() throws Exception {
        StringBuilder sb = new StringBuilder();
        produce(sb);
        return sb.toString();
    }

    /**
     * Returns an initial string displayed on the template output during
     * component initialization. May contain help information or hints.
     *
     * @return the initial string, or empty if none
     */
    default @NonNull Optional<@NonNull String> getInitialString() {
        return Optional.empty();
    }
}
