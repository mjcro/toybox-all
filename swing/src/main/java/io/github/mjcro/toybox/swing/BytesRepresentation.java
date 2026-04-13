package io.github.mjcro.toybox.swing;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.text.JTextComponent;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Defines bytes representation formats and provides conversion
 * between byte arrays and their string representations.
 */
public enum BytesRepresentation {
    PLAINTEXT,
    HEXADECIMAL,
    BASE64;

    /**
     * Reads byte data from the given text component using this representation.
     *
     * @param c source text component
     * @return byte representation of the component text
     */
    public byte @NonNull [] asBytes(@NonNull JTextComponent c) {
        return asBytes(c.getText());
    }

    /**
     * Returns bytes from the given char sequence using this representation.
     *
     * @param cs source char sequence, may be {@code null}
     * @return byte representation of the char sequence
     */
    public byte @NonNull [] asBytes(@Nullable CharSequence cs) {
        if (cs == null || cs.length() == 0) {
            return new byte[0];
        }

        try {
            switch (this) {
                case PLAINTEXT:
                    return cs.toString().getBytes(StandardCharsets.UTF_8);
                case HEXADECIMAL:
                    return Hex.decodeHex(cs.toString());
                case BASE64:
                    return Base64.getDecoder().decode(cs.toString());
                default:
                    throw new IllegalStateException("Unsupported representation " + this);
            }
        } catch (DecoderException de) {
            throw new IllegalArgumentException("Unable to decode hex string", de);
        }
    }

    /**
     * Constructs a string from the given bytes using this representation.
     *
     * @param bytes source bytes, may be {@code null}
     * @return output string
     */
    public @NonNull String fromBytes(byte @Nullable [] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }

        switch (this) {
            case PLAINTEXT:
                return new String(bytes, StandardCharsets.UTF_8);
            case HEXADECIMAL:
                return Hex.encodeHexString(bytes);
            case BASE64:
                return Base64.getEncoder().encodeToString(bytes);
            default:
                throw new IllegalStateException("Unsupported representation " + this);
        }
    }

    @Override
    public @NonNull String toString() {
        switch (this) {
            case PLAINTEXT:
                return "Plain text";
            case HEXADECIMAL:
                return "Hexadecimal";
            case BASE64:
                return "Base 64";
            default:
                return "Unknown";
        }
    }
}
