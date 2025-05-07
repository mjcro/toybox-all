package io.github.mjcro.toybox.swing;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.swing.text.JTextComponent;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Defines bytes representation.
 */
public enum BytesRepresentation {
    PLAINTEXT,
    HEXADECIMAL,
    BASE64;

    /**
     * Reads byte data from given text component using byte representation.
     *
     * @param c Source text component.
     * @return Byte representation of component text.
     */
    public byte[] asBytes(JTextComponent c) {
        return asBytes(c.getText());
    }

    /**
     * Returns bytes from given char sequence using byte representation.
     *
     * @param cs Source char sequence.
     * @return Byte representation of char sequence.
     */
    public byte[] asBytes(CharSequence cs) {
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
     * Constructs string from given bytes using byte representation.
     *
     * @param bytes Source bytes.
     * @return Output string.
     */
    public String fromBytes(byte[] bytes) {
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
    public String toString() {
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
