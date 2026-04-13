package io.github.mjcro.toybox.toys.crypt;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Interface for symmetric encryption/decryption algorithms that operate on raw byte arrays.
 */
interface Algo {
    /**
     * Encrypts the given data using the provided secret and IV.
     *
     * @param secret the secret key bytes
     * @param iv     the initialization vector bytes, or null to generate one
     * @param data   the plaintext data to encrypt
     * @return the encrypted data together with the IV used
     * @throws Exception if encryption fails
     */
    @NonNull IVData encrypt(byte @NonNull [] secret, byte @Nullable [] iv, byte @NonNull [] data) throws Exception;

    /**
     * Decrypts the given IV+ciphertext using the provided secret.
     *
     * @param secret the secret key bytes
     * @param data   the IV and ciphertext to decrypt
     * @return the decrypted plaintext bytes
     * @throws Exception if decryption fails
     */
    byte @NonNull [] decrypt(byte @NonNull [] secret, @NonNull IVData data) throws Exception;
}
