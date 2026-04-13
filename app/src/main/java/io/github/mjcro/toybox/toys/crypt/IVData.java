package io.github.mjcro.toybox.toys.crypt;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;

/**
 * Holds an initialization vector and encrypted data pair.
 */
class IVData {

    private final byte @NonNull [] iv;
    private final byte @NonNull [] data;

    /**
     * Creates a new IV/data pair.
     *
     * @param iv   the initialization vector
     * @param data the encrypted data
     */
    IVData(byte @NonNull [] iv, byte @NonNull [] data) {
        this.iv = iv;
        this.data = data;
    }

    /**
     * Returns the initialization vector.
     *
     * @return the IV bytes
     */
    public byte @NonNull [] getIv() {
        return iv;
    }

    /**
     * Returns the encrypted data.
     *
     * @return the data bytes
     */
    public byte @NonNull [] getData() {
        return data;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IVData ivData = (IVData) o;
        return Arrays.equals(iv, ivData.iv)
                && Arrays.equals(data, ivData.data);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(iv);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public @NonNull String toString() {
        return "IVData{iv=" + Arrays.toString(iv) + ", data=" + Arrays.toString(data) + "}";
    }
}
