package io.github.mjcro.toybox.toys.crypt;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

/**
 * Base class for cipher-based encryption/decryption algorithms providing
 * common encrypt/decrypt logic with pluggable IV and key preparation.
 */
abstract class AbstractCipherBasedAlgo implements Algo {
    private final @NonNull SecureRandom random = new SecureRandom();

    /**
     * Returns the JCA cipher transformation name (e.g., "AES/GCM/NoPadding").
     *
     * @return the cipher name
     */
    protected abstract @NonNull String getCipherName();

    /**
     * Prepares an algorithm parameter spec from the given IV bytes.
     *
     * @param iv the initialization vector bytes, or null to generate a random IV
     * @return the algorithm parameter spec, or null if the mode requires none
     */
    protected abstract @Nullable AlgorithmParameterSpec prepareIV(byte @Nullable [] iv);

    /**
     * Prepares a secret key spec from the given secret bytes.
     *
     * @param secret the raw secret key bytes
     * @return a secret key spec suitable for this algorithm
     */
    protected abstract @NonNull SecretKeySpec prepareSecret(byte @NonNull [] secret);

    /**
     * Generates a random byte array of the specified length.
     *
     * @param n the number of random bytes to generate
     * @return a new byte array filled with random data
     */
    protected byte @NonNull [] generateRandom(int n) {
        byte[] bytes = new byte[n];
        random.nextBytes(bytes);
        return bytes;
    }

    @Override
    public @NonNull IVData encrypt(byte @NonNull [] secret, byte @Nullable [] iv, byte @NonNull [] data) throws Exception {
        AlgorithmParameterSpec ivParameterSpec = prepareIV(iv);
        SecretKeySpec secretKeySpec = prepareSecret(secret);

        Cipher cipher = Cipher.getInstance(getCipherName());
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

        return new IVData(
                extractIV(ivParameterSpec),
                cipher.doFinal(data)
        );
    }

    @Override
    public byte @NonNull [] decrypt(byte @NonNull [] secret, @NonNull IVData data) throws Exception {
        AlgorithmParameterSpec ivParameterSpec = prepareIV(data.getIv());
        SecretKeySpec secretKeySpec = prepareSecret(secret);

        Cipher cipher = Cipher.getInstance(getCipherName());
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        return cipher.doFinal(data.getData());
    }

    /**
     * Extracts the IV bytes from an algorithm parameter spec.
     *
     * @param spec the algorithm parameter spec
     * @return the IV bytes, or an empty array if spec is null
     */
    private byte @NonNull [] extractIV(@Nullable AlgorithmParameterSpec spec) {
        if (spec instanceof IvParameterSpec) {
            return ((IvParameterSpec) spec).getIV();
        } else if (spec instanceof GCMParameterSpec) {
            return ((GCMParameterSpec) spec).getIV();
        } else if (spec == null) {
            return new byte[0];
        }

        throw new IllegalStateException("Unknown parameter spec " + spec.getClass());
    }

    @Override
    public @NonNull String toString() {
        return getCipherName();
    }
}
