package io.github.mjcro.toybox.toys.crypt;

import com.google.common.hash.Hashing;

enum KeyTransformation {
    NO,
    MD5,
    SHA256;

    public byte[] transform(byte[] in) {
        switch (this) {
            case NO:
                return in;
            case MD5:
                return Hashing.md5().hashBytes(in).asBytes();
            case SHA256:
                return Hashing.sha256().hashBytes(in).asBytes();
            default:
                throw new IllegalArgumentException("Unsupported type " + this);
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case NO:
                return "No transformation";
            case MD5:
                return "MD5 (128bit)";
            case SHA256:
                return "SHA-256 (256bit)";
            default:
                return "Unknown";
        }
    }
}
