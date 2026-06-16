package de.hitec.nhplus.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

public final class KeyWrapUtil {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String WRAP_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String KEY_ALGORITHM = "AES";
    private static final int SALT_LENGTH_BYTES = 16;
    private static final int IV_LENGTH_BYTES = 12;
    private static final int KEY_LENGTH_BITS = 256;
    private static final int DEFAULT_ITERATIONS = 210_000;
    private static final int GCM_TAG_LENGTH_BITS = 128;

    /**
     * Holds the wrapped data key and all parameters required for unwrapping.
     *
     * @param wrappedKey Base64-encoded wrapped key bytes
     * @param salt Base64-encoded PBKDF2 salt
     * @param iv Base64-encoded AES-GCM IV
     * @param iterations PBKDF2 iteration count used during wrapping
     */
    public record WrappedKey(String wrappedKey, String salt, String iv, int iterations) {}

    /**
     * Utility class for key wrapping helpers.
     */
    private KeyWrapUtil() {
    }

    /**
     * Wraps the given data key with a password-derived key using randomly generated salt and IV.
     *
     * @param dataKey the data encryption key to wrap
     * @param password the password used to derive the key-encryption key
     * @return wrapper object containing the wrapped key and derivation parameters
     * @throws DataEncryptionException when wrapping fails
     */
    public static WrappedKey wrap(SecretKey dataKey, String password) {
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        byte[] iv = new byte[IV_LENGTH_BYTES];
        RANDOM.nextBytes(salt);
        RANDOM.nextBytes(iv);
        return wrap(dataKey, password, salt, iv, DEFAULT_ITERATIONS);
    }

    /**
     * Wraps the given data key with caller-provided salt, IV and iteration count.
     *
     * @param dataKey the data encryption key to wrap
     * @param password the password used to derive the key-encryption key
     * @param salt the PBKDF2 salt bytes
     * @param iv the AES-GCM IV bytes
     * @param iterations the PBKDF2 iteration count
     * @return wrapper object containing the wrapped key and derivation parameters
     * @throws DataEncryptionException when wrapping fails
     */
    public static WrappedKey wrap(SecretKey dataKey, String password, byte[] salt, byte[] iv, int iterations) {
        try {
            SecretKey keyEncryptionKey = deriveKeyEncryptionKey(password, salt, iterations);
            Cipher cipher = Cipher.getInstance(WRAP_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keyEncryptionKey, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            byte[] wrapped = cipher.doFinal(dataKey.getEncoded());
            return new WrappedKey(
                    Base64.getEncoder().encodeToString(wrapped),
                    Base64.getEncoder().encodeToString(salt),
                    Base64.getEncoder().encodeToString(iv),
                    iterations
            );
        } catch (GeneralSecurityException exception) {
            throw new DataEncryptionException("Unable to wrap encryption key.", exception);
        }
    }

    /**
     * Unwraps a previously wrapped data key.
     *
     * @param wrappedKey Base64-encoded wrapped key bytes
     * @param salt Base64-encoded PBKDF2 salt
     * @param iv Base64-encoded AES-GCM IV
     * @param iterations PBKDF2 iteration count used during wrapping
     * @param password the password used to derive the key-encryption key
     * @return the unwrapped AES data key
     * @throws DataEncryptionException when payload decoding or unwrapping fails
     */
    public static SecretKey unwrap(String wrappedKey, String salt, String iv, int iterations, String password) {
        try {
            byte[] wrappedKeyBytes = Base64.getDecoder().decode(wrappedKey);
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            byte[] ivBytes = Base64.getDecoder().decode(iv);

            SecretKey keyEncryptionKey = deriveKeyEncryptionKey(password, saltBytes, iterations);
            Cipher cipher = Cipher.getInstance(WRAP_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keyEncryptionKey, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, ivBytes));

            byte[] dataKeyBytes = cipher.doFinal(wrappedKeyBytes);
            return new SecretKeySpec(dataKeyBytes, KEY_ALGORITHM);
        } catch (GeneralSecurityException | IllegalArgumentException exception) {
            throw new DataEncryptionException("Unable to unwrap encryption key.", exception);
        }
    }

    /**
     * Derives a key-encryption key from password and salt using PBKDF2-HMAC-SHA256.
     *
     * @param password the password input
     * @param salt the PBKDF2 salt bytes
     * @param iterations PBKDF2 iteration count
     * @return derived AES key-encryption key
     * @throws GeneralSecurityException when key derivation fails
     */
    private static SecretKey deriveKeyEncryptionKey(String password, byte[] salt, int iterations)
            throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH_BITS);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), KEY_ALGORITHM);
        } finally {
            spec.clearPassword();
        }
    }
}
