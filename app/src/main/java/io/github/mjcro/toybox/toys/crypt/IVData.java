package io.github.mjcro.toybox.toys.crypt;

import lombok.Data;

@Data
class IVData {
    private final byte[] iv;
    private final byte[] data;
}
