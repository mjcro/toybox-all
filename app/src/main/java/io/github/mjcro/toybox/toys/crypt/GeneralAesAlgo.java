package io.github.mjcro.toybox.toys.crypt;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.AlgorithmParameterSpec;


class GeneralAesAlgo extends AbstractCipherBasedAlgo {
    private final String name;

    public GeneralAesAlgo(String name) {
        this.name = name;
    }

    @Override
    protected String getCipherName() {
        return name;
    }

    @Override
    protected AlgorithmParameterSpec prepareIV(byte[] iv) {
        if (name.contains("/ECB/")) {
            return null;
        }
        return new IvParameterSpec(iv == null ? generateRandom(16) : iv);
    }

    @Override
    protected SecretKeySpec prepareSecret(byte[] secret) {
        return new SecretKeySpec(secret, "AES");
    }
}
