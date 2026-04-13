package io.github.mjcro.toybox.app.settings.storage;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.google.common.hash.Hashing;
import io.github.mjcro.toybox.api.Setting;
import org.jspecify.annotations.NonNull;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;

/**
 * File-based settings storage that encrypts data using a cipher.
 * Settings are serialized as JSON with Jackson polymorphic typing,
 * then encrypted with the configured cipher algorithm.
 */
public class CipheredJsonFileStorage extends AbstractFileSettingsStorage {
    private final @NonNull CipherInitializer cipherInitializer;
    private final int ivLen;
    private final @NonNull ObjectMapper mapper;

    /**
     * Creates a storage instance using AES-256-GCM encryption with a password-derived key.
     *
     * @param file     the file to store encrypted settings in
     * @param password the password used to derive the encryption key
     * @return a new AES-256-GCM backed storage
     */
    public static @NonNull CipheredJsonFileStorage Aes256Gcm(@NonNull File file, @NonNull String password) {
        SecretKeySpec keySpec = deriveKey(password, 256);
        CipherInitializer cipherInitializer = (mode, iv) -> {
            GCMParameterSpec gcmSpec = new GCMParameterSpec(96, iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(mode, keySpec, gcmSpec);
            return cipher;
        };
        return new CipheredJsonFileStorage(file, 12, cipherInitializer);
    }

    /**
     * Derives an AES key from the given password using SHA-256 hashing.
     *
     * @param password the password string
     * @param bits     the key size in bits
     * @return the derived secret key specification
     */
    private static @NonNull SecretKeySpec deriveKey(@NonNull String password, int bits) {
        byte[] untruncated = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).asBytes();
        byte[] truncated = Arrays.copyOf(untruncated, bits / 8);
        return new SecretKeySpec(truncated, "AES");
    }

    /**
     * Constructs a ciphered JSON file storage.
     *
     * @param file              the file to store settings in
     * @param ivLen             the initialization vector length in bytes
     * @param cipherInitializer the cipher initializer for encryption and decryption
     */
    public CipheredJsonFileStorage(@NonNull File file, int ivLen, @NonNull CipherInitializer cipherInitializer) {
        super(file);
        this.cipherInitializer = Objects.requireNonNull(cipherInitializer, "cipherInitializer");
        this.ivLen = ivLen;
        this.mapper = new ObjectMapper();
        this.mapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator
                        .builder()
                        .allowIfSubType(Setting.class)
                        .build(),
                ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE,
                JsonTypeInfo.As.PROPERTY
        );
    }

    @Override
    protected @NonNull Setting[] readFile() {
        if (!file.exists()) {
            return new Setting[0];
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            // Reading IV
            byte[] iv = new byte[ivLen];
            fis.read(iv);

            // Decrypting rest of the stream
            try (CipherInputStream cis = new CipherInputStream(fis, cipherInitializer.initialize(Cipher.DECRYPT_MODE, iv))) {
                return mapper.readValue(cis, Setting[].class);
            }
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException("Error reading configuration file " + file.getName(), e);
        }
    }

    @Override
    protected void writeFile(@NonNull Setting[] settings) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            // Generating and saving IV
            byte[] iv = new byte[ivLen];
            new SecureRandom().nextBytes(iv);
            fos.write(iv);

            // Encrypting
            try (CipherOutputStream cos = new CipherOutputStream(fos, cipherInitializer.initialize(Cipher.ENCRYPT_MODE, iv))) {
                mapper.writeValue(cos, settings);
            }
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException("Error writing configuration file " + file.getName(), e);
        }
    }
}
