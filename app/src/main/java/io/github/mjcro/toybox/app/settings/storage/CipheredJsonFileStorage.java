package io.github.mjcro.toybox.app.settings.storage;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.google.common.hash.Hashing;
import io.github.mjcro.toybox.api.Setting;

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

public class CipheredJsonFileStorage extends AbstractFileSettingsStorage {
    private final CipherInitializer cipherInitializer;
    private final int ivLen;
    private final ObjectMapper mapper;

    public static CipheredJsonFileStorage Aes256Gcm(File file, String password) {
        SecretKeySpec keySpec = deriveKey(password, 256);
        CipherInitializer cipherInitializer = (mode, iv) -> {
            GCMParameterSpec gcmSpec = new GCMParameterSpec(96, iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(mode, keySpec, gcmSpec);
            return cipher;
        };
        return new CipheredJsonFileStorage(file, 12, cipherInitializer);
    }

    private static SecretKeySpec deriveKey(String password, int bits) {
        byte[] untruncated = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).asBytes();
        byte[] truncated = Arrays.copyOf(untruncated, bits / 8);
        return new SecretKeySpec(truncated, "AES");
    }

    public CipheredJsonFileStorage(File file, int ivLen, CipherInitializer cipherInitializer) {
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
    protected Setting[] readFile() {
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
    protected void writeFile(Setting[] settings) {
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
