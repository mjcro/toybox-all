package io.github.mjcro.toybox.toys.crypt;

import com.google.common.hash.Hashing;
import org.jspecify.annotations.NonNull;

/**
 * Enumeration of key transformation strategies that hash raw key bytes
 * into a fixed-size key suitable for cipher algorithms.
 */
enum KeyTransformation {
    NO,
    MD5,
    SHA256;

    /**
     * Transforms the input key bytes according to this transformation strategy.
     *
     * @param in the raw key bytes
     * @return the transformed key bytes
     */
    public byte @NonNull [] transform(byte @NonNull [] in) {
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
    public @NonNull String toString() {
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
