package io.github.mjcro.toybox.toys.crypt;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.AlgorithmParameterSpec;

/**
 * General-purpose AES algorithm supporting multiple cipher modes (CBC, ECB, GCM, etc.)
 * with automatic IV handling.
 */
class GeneralAesAlgo extends AbstractCipherBasedAlgo {
    private final @NonNull String name;

    /**
     * Constructs an AES algorithm with the given JCA cipher transformation name.
     *
     * @param name the cipher transformation (e.g., "AES/CBC/PKCS5Padding")
     */
    public GeneralAesAlgo(@NonNull String name) {
        this.name = name;
    }

    @Override
    protected @NonNull String getCipherName() {
        return name;
    }

    @Override
    protected @Nullable AlgorithmParameterSpec prepareIV(byte @Nullable [] iv) {
        if (name.contains("/ECB/")) {
            return null;
        }
        return new IvParameterSpec(iv == null ? generateRandom(16) : iv);
    }

    @Override
    protected @NonNull SecretKeySpec prepareSecret(byte @NonNull [] secret) {
        return new SecretKeySpec(secret, "AES");
    }
}
