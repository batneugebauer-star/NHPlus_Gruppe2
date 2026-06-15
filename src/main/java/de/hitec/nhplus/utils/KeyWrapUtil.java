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

    public record WrappedKey(String wrappedKey, String salt, String iv, int iterations) {}

    private KeyWrapUtil() {
    }

    public static WrappedKey wrap(SecretKey dataKey, String password) {
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        byte[] iv = new byte[IV_LENGTH_BYTES];
        RANDOM.nextBytes(salt);
        RANDOM.nextBytes(iv);
        return wrap(dataKey, password, salt, iv, DEFAULT_ITERATIONS);
    }

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
