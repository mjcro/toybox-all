package io.github.mjcro.toybox.toys.crypt;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

abstract class AbstractCipherBasedAlgo implements Algo {
    private final SecureRandom random = new SecureRandom();

    protected abstract String getCipherName();

    protected abstract AlgorithmParameterSpec prepareIV(byte[] iv);

    protected abstract SecretKeySpec prepareSecret(byte[] secret);

    protected byte[] generateRandom(int n) {
        byte[] bytes = new byte[n];
        random.nextBytes(bytes);
        return bytes;
    }

    @Override
    public IVData encrypt(byte[] secret, byte[] iv, byte[] data) throws Exception {
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
    public byte[] decrypt(byte[] secret, IVData data) throws Exception {
        AlgorithmParameterSpec ivParameterSpec = prepareIV(data.getIv());
        SecretKeySpec secretKeySpec = prepareSecret(secret);

        Cipher cipher = Cipher.getInstance(getCipherName());
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        return cipher.doFinal(data.getData());
    }

    private byte[] extractIV(AlgorithmParameterSpec spec) {
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
    public String toString() {
        return getCipherName();
    }
}
