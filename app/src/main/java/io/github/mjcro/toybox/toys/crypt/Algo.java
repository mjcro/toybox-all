package io.github.mjcro.toybox.toys.crypt;

interface Algo {
    IVData encrypt(byte[] secret, byte[] iv, byte[] data) throws Exception;
    byte[] decrypt(byte[] secret, IVData data) throws Exception;
}
