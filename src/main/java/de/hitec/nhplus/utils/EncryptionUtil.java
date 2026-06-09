package de.hitec.nhplus.utils;

import de.hitec.nhplus.Config;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionUtil {
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String KEY_ALGORITHM = "AES";
    private static final int AES_KEY_LENGTH_BYTES = 32;
    private static final int GCM_IV_LENGTH_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final String ENCRYPTION_PREFIX = "enc:v1:";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final SecretKey SECRET_KEY = loadKey();

    private EncryptionUtil() {
    }

    public static String encrypt(String plainText) {
        if (plainText == null) {
            return null;
        }

        try {
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            SECURE_RANDOM.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return ENCRYPTION_PREFIX + Base64.getEncoder().encodeToString(iv) + ":" +
                    Base64.getEncoder().encodeToString(cipherText);
        } catch (GeneralSecurityException exception) {
            throw new DataEncryptionException("Unable to encrypt data.", exception);
        }
    }

    public static String decrypt(String encryptedText) {
        if (encryptedText == null) {
            return null;
        }
        if (!encryptedText.startsWith(ENCRYPTION_PREFIX)) {
            throw new DataEncryptionException("Unexpected encrypted payload format.");
        }

        String payload = encryptedText.substring(ENCRYPTION_PREFIX.length());
        String[] parts = payload.split(":", 2);
        if (parts.length != 2) {
            throw new DataEncryptionException("Unexpected encrypted payload format.");
        }

        try {
            byte[] iv = Base64.getDecoder().decode(parts[0]);
            byte[] cipherText = Base64.getDecoder().decode(parts[1]);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            byte[] plainText = cipher.doFinal(cipherText);
            return new String(plainText, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException exception) {
            throw new DataEncryptionException("Unable to decrypt data.", exception);
        }
    }

    private static SecretKey loadKey() {
        String encodedKey = Config.get("NHPLUS_ENCRYPTION_KEY");
        if (encodedKey == null || encodedKey.isBlank()) {
            throw new DataEncryptionException("Missing encryption key. Set NHPLUS_ENCRYPTION_KEY as Base64 AES-256 key.");
        }

        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(encodedKey.trim());
        } catch (IllegalArgumentException exception) {
            throw new DataEncryptionException("NHPLUS_ENCRYPTION_KEY is not valid Base64.", exception);
        }

        if (keyBytes.length != AES_KEY_LENGTH_BYTES) {
            throw new DataEncryptionException("NHPLUS_ENCRYPTION_KEY must decode to 32 bytes for AES-256.");
        }

        return new SecretKeySpec(keyBytes, KEY_ALGORITHM);
    }

    public static String generateBase64Key() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
            keyGenerator.init(AES_KEY_LENGTH_BYTES * 8);
            SecretKey key = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (GeneralSecurityException exception) {
            throw new DataEncryptionException("Unable to generate encryption key.", exception);
        }
    }
}
