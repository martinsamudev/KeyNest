package Crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class PasswordUtils {

    private static final String algoritmo = "AES";

    private SecretKeySpec generarClave(String secretKey) {
        // Asegura que la clave tenga exactamente 16 bytes (AES-128)
        byte[] keyBytes = Arrays.copyOf(secretKey.getBytes(StandardCharsets.UTF_8), 16);
        return new SecretKeySpec(keyBytes, algoritmo);
    }

    // Cifrado con AES
    public String encriptar(String plaintext, String secretKey) {
        try {
            SecretKeySpec key = generarClave(secretKey);
            Cipher cipher = Cipher.getInstance(algoritmo);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encriptarBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encriptarBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error al cifrar la contraseña", e);
        }
    }

    // Descifrado con AES
    public String descriptar(String encryptedText, String secretKey) {
        try {
            SecretKeySpec key = generarClave(secretKey);
            Cipher cipher = Cipher.getInstance(algoritmo);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] byteDescifrado = Base64.getDecoder().decode(encryptedText);
            byte[] byteDesencriptado = cipher.doFinal(byteDescifrado);
            return new String(byteDesencriptado, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error al descifrar la contraseña", e);
        }
    }
}
