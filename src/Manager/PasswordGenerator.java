package Manager;

import java.security.SecureRandom;

public class PasswordGenerator {
    private static final String minusculas = "abcdefghijkklmnñopqrstuvxyz";
    private static final String mayusculas = "ABCDEFGHIJJKLMNÑOPPQRSTUVXYZ";
    private static final String numeros = "0123456789";
    private static final String especiales = "!@#$%^&*()-_=+[]{}|;:,.<>?";
    private static final String combinacion = minusculas + mayusculas + numeros + especiales;
    private static final SecureRandom random = new SecureRandom();

    public static String generate(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }

        StringBuilder password = new StringBuilder(length);

        // Aseguramos que la contraseña contenga al menos un carácter de cada tipo
        password.append(getRandomChar(minusculas));
        password.append(getRandomChar(mayusculas));
        password.append(getRandomChar(numeros));
        password.append(getRandomChar(especiales));

        // El resto de caracteres se generan aleatoriamente del conjunto completo
        for (int i = 4; i < length; i++) {
            password.append(getRandomChar(combinacion));
        }

        // Mezclar la contraseña para que no siga el patrón de arriba
        return shuffleString(password.toString());
    }

    private static char getRandomChar(String input) {
        int index = random.nextInt(input.length());
        return input.charAt(index);
    }

    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            int j = random.nextInt(characters.length);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        return new String(characters);
    }
}
