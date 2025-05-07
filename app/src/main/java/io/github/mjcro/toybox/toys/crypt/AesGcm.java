package io.github.mjcro.toybox.toys.crypt;

import javax.crypto.spec.GCMParameterSpec;
import java.security.spec.AlgorithmParameterSpec;

class AesGcm extends GeneralAesAlgo {
    public AesGcm() {
        super("AES/GCM/NoPadding");
    }

    @Override
    protected AlgorithmParameterSpec prepareIV(byte[] iv) {
        if (iv == null) {
            return new GCMParameterSpec(128, generateRandom(12));
        }

        return new GCMParameterSpec(128, iv);
    }
}
