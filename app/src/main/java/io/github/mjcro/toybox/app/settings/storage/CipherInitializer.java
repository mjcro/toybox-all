package io.github.mjcro.toybox.app.settings.storage;

import org.jspecify.annotations.NonNull;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;

/**
 * Functional interface for initializing a {@link Cipher} with the given mode
 * and initialization vector.
 */
@FunctionalInterface
public interface CipherInitializer {
    /**
     * Initializes and returns a cipher configured for the specified mode.
     *
     * @param mode the cipher mode (e.g., {@link Cipher#ENCRYPT_MODE} or {@link Cipher#DECRYPT_MODE})
     * @param iv   the initialization vector bytes
     * @return the initialized cipher
     * @throws GeneralSecurityException if cipher initialization fails
     */
    @NonNull Cipher initialize(int mode, byte @NonNull [] iv) throws GeneralSecurityException;
}
