package io.github.mjcro.toybox.app.settings.storage;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;

@FunctionalInterface
public interface CipherInitializer {
    Cipher initialize(int mode, byte[] iv) throws GeneralSecurityException;
}
