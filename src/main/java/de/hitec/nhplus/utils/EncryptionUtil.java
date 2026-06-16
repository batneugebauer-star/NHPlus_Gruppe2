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
    private static final SecretKey BOOTSTRAP_KEY = loadKey();

    /**
     * Utility class for AES-GCM encryption helpers.
     */
    private EncryptionUtil() {
    }

    /**
     * Encrypts plain text using AES/GCM and returns a versioned payload.
     * <p>
     * The returned format is {@code enc:v1:<base64-iv>:<base64-ciphertext>}.
     * </p>
     *
     * @param plainText the text to encrypt; may be {@code null}
     * @return the encrypted payload, or {@code null} when {@code plainText} is {@code null}
     * @throws DataEncryptionException when encryption fails
     */
    public static String encrypt(String plainText) {
        if (plainText == null) {
            return null;
        }

        try {
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            SECURE_RANDOM.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, resolveKey(), new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return ENCRYPTION_PREFIX + Base64.getEncoder().encodeToString(iv) + ":" +
                    Base64.getEncoder().encodeToString(cipherText);
        } catch (GeneralSecurityException exception) {
            throw new DataEncryptionException("Unable to encrypt data.", exception);
        }
    }

    /**
     * Decrypts a versioned AES/GCM payload produced by {@link #encrypt(String)}.
     *
     * @param encryptedText the encrypted payload; may be {@code null}
     * @return the decrypted plain text, or {@code null} when {@code encryptedText} is {@code null}
     * @throws DataEncryptionException when the payload format is invalid or decryption fails
     */
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
            cipher.init(Cipher.DECRYPT_MODE, resolveKey(), new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            byte[] plainText = cipher.doFinal(cipherText);
            return new String(plainText, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException exception) {
            throw new DataEncryptionException("Unable to decrypt data.", exception);
        }
    }

    /**
     * Returns the statically loaded fallback key from configuration.
     *
     * @return the bootstrap encryption key
     */
    public static SecretKey getBootstrapKey() {
        return BOOTSTRAP_KEY;
    }

    /**
     * Resolves the active data encryption key.
     * <p>
     * Uses the session key when available, otherwise falls back to the bootstrap key.
     * </p>
     *
     * @return the key used for encryption and decryption
     */
    private static SecretKey resolveKey() {
        SecretKey sessionKey = SessionManager.getInstance().getDataEncryptionKey();
        return sessionKey != null ? sessionKey : BOOTSTRAP_KEY;
    }

    /**
     * Loads and validates the application encryption key from configuration.
     *
     * @return a valid AES key
     * @throws DataEncryptionException when the configuration value is missing or invalid
     */
    private static SecretKey loadKey() {
        String encodedKey = Config.get("NHPLUS_ENCRYPTION_KEY");
        if (encodedKey == null || encodedKey.isBlank()) {
            throw new DataEncryptionException("Missing encryption key. Set NHPLUS_ENCRYPTION_KEY as Base64 AES-256 key.");
        }

        byte[] keyBytes = decodeConfiguredKey(encodedKey.trim());
        if (keyBytes.length != AES_KEY_LENGTH_BYTES) {
            throw new DataEncryptionException("NHPLUS_ENCRYPTION_KEY must resolve to 32 bytes for AES-256.");
        }

        return new SecretKeySpec(keyBytes, KEY_ALGORITHM);
    }

    /**
     * Decodes the configured key value either as Base64 or raw UTF-8 bytes.
     *
     * @param encodedKey the configured key value
     * @return key bytes with AES-256 length
     * @throws DataEncryptionException when the value cannot be decoded to 32 bytes
     */
    private static byte[] decodeConfiguredKey(String encodedKey) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encodedKey);
            if (decoded.length == AES_KEY_LENGTH_BYTES) {
                return decoded;
            }
        } catch (IllegalArgumentException exception) {
            // fall through to raw bytes
        }

        byte[] rawBytes = encodedKey.getBytes(StandardCharsets.UTF_8);
        if (rawBytes.length == AES_KEY_LENGTH_BYTES) {
            return rawBytes;
        }

        throw new DataEncryptionException("NHPLUS_ENCRYPTION_KEY must be a 32-byte AES key or Base64-encoded 32-byte key.");
    }

    /**
     * Generates a new random AES-256 key and returns it as Base64.
     *
     * @return a Base64-encoded AES-256 key
     * @throws DataEncryptionException when key generation fails
     */
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
