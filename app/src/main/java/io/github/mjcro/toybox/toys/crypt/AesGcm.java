package io.github.mjcro.toybox.toys.crypt;

import org.jspecify.annotations.Nullable;

import javax.crypto.spec.GCMParameterSpec;
import java.security.spec.AlgorithmParameterSpec;

/**
 * AES-GCM encryption algorithm implementation with 128-bit authentication tag
 * and 12-byte IV.
 */
class AesGcm extends GeneralAesAlgo {
    /**
     * Constructs an AES-GCM algorithm instance.
     */
    public AesGcm() {
        super("AES/GCM/NoPadding");
    }

    @Override
    protected @Nullable AlgorithmParameterSpec prepareIV(byte @Nullable [] iv) {
        if (iv == null) {
            return new GCMParameterSpec(128, generateRandom(12));
        }

        return new GCMParameterSpec(128, iv);
    }
}
