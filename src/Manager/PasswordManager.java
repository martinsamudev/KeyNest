package Manager;

import Crypto.PasswordUtils;
import Model.PasswordEntry;
import Storage.StorageService;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class PasswordManager {
    private static final String archivoConfiguracion = "config.txt";
    private String masterKey;
    private final StorageService storageService = new StorageService();
    private final PasswordUtils passwordUtils = new PasswordUtils();

    public PasswordManager(Scanner scanner) {
        File configFile = new File(archivoConfiguracion);
        if (!configFile.exists()) {
            configurarPrimeraVez(scanner);
        } else {
            autenticarUsuario(scanner);
        }
    }

    // --- CONFIGURACIÓN INICIAL ---

    private void configurarPrimeraVez(Scanner scanner) {
        while (true){
            System.out.println("Es tu primera vez usando KeyNest, necesitas crear tu clave maestra:");
            String nuevaClave = scanner.nextLine();
            System.out.println("Confirma tu clave maestra: ");
            String nuevaClaveConfirmacion = scanner.nextLine();
            if (Objects.equals(nuevaClave, nuevaClaveConfirmacion)){
                this.masterKey = nuevaClave;
                String claveRecuperacion = generarClaveRecuperacion();
                System.out.println("\n⚠️ Tu clave de recuperación es: " + claveRecuperacion);
                System.out.println("Guárdala bien. Solo se mostrará una vez.\n");

                guardarClaves(nuevaClaveConfirmacion, claveRecuperacion);
                return;
            }else {
                System.out.println("Las contraseñas no coinciden. Intentalo de nuevo");
            }
        }
    }

    private void autenticarUsuario(Scanner scanner) {
        System.out.println("1. Ingresar clave maestra");
        System.out.println("2. Recuperar acceso con clave de recuperación");
        System.out.print("Elige una opción: ");
        String opcion = scanner.nextLine();

        String[] clavesCifradas = leerClaves();
        String claveMaestraCifrada = clavesCifradas[0];
        String claveRecuperacionCifrada = clavesCifradas[1];

        boolean accesoPermitido = false;
        while (!accesoPermitido) {
            switch (opcion) {
                case "1":
                    accesoPermitido = ingresarClaveMaestra(scanner, claveMaestraCifrada);
                    break;
                case "2":
                    accesoPermitido = recuperarAcceso(scanner, claveRecuperacionCifrada);
                    break;
                default:
                    System.out.println("❌ Opción no válida. Intenta de nuevo.");
                    return;
            }
        }
    }

    private boolean ingresarClaveMaestra(Scanner scanner, String claveMaestraCifrada) {
        System.out.print("Introduce tu clave maestra: ");
        String entrada = scanner.nextLine();

        // Intentamos descifrar la clave maestra
        try {
            String claveDesencriptada = passwordUtils.descriptar(claveMaestraCifrada, generateKeyFrom(entrada));

            // Comprobamos si la clave descifrada coincide con la introducida
            if (!entrada.equals(claveDesencriptada)) {
                System.out.println("❌ Clave maestra incorrecta. Inténtalo de nuevo.");
                return false;
            }

            this.masterKey = entrada; // Si la clave es correcta, la guardamos
            return true;
        } catch (Exception e) {
            // Si ocurre una excepción al intentar descifrar, mostramos un mensaje de error
            System.out.println("❌ Error al intentar autenticar. Clave maestra incorrecta.");
            return false;
        }
    }


    private boolean recuperarAcceso(Scanner scanner, String claveRecuperacionCifrada) {
        System.out.print("Introduce tu clave de recuperación: ");
        String recoveryInput = scanner.nextLine();
        String originalRecoveryKey = passwordUtils.descriptar(claveRecuperacionCifrada, generateKeyFrom(recoveryInput));

        if (!recoveryInput.equals(originalRecoveryKey)) {
            System.out.println("❌ Clave de recuperación incorrecta. Intenta de nuevo.");
            return false;
        }

        System.out.print("Introduce tu nueva clave maestra: ");
        String nuevaClave = scanner.nextLine();
        this.masterKey = nuevaClave;

        guardarClaves(nuevaClave, recoveryInput);
        System.out.println("🔐 Nueva clave maestra guardada con éxito.");
        return true;
    }

    // --- FUNCIONALIDAD PRINCIPAL ---

    public boolean isPasswordValid(String inputPassword) {
        return inputPassword.equals(masterKey);
    }

    public void cambiarClaveMaestra(Scanner scanner) {
        System.out.print("Introduce tu clave maestra actual: ");
        String claveActual = scanner.nextLine();
        if (!isPasswordValid(claveActual)) {
            System.out.println("Clave maestra incorrecta.");
            return;
        }

        System.out.print("Nueva clave maestra (mínimo 4 caracteres): ");
        String nuevaClave;
        while (true) {
            nuevaClave = scanner.nextLine();
            if (nuevaClave.length() < 4) {
                System.out.print("Demasiado corta. Intenta otra: ");
            } else {
                break;
            }
        }

        List<PasswordEntry> entries = storageService.cargarContraseñas();
        for (PasswordEntry entry : entries) {
            String oldDecrypted = passwordUtils.descriptar(entry.getContraseñaEncriptada(), generateKeyFrom(masterKey));
            String newEncrypted = passwordUtils.encriptar(oldDecrypted, generateKeyFrom(nuevaClave));
            entry.setContraseñaEncriptada(newEncrypted);
        }

        this.masterKey = nuevaClave;
        saveMasterKey(nuevaClave);
        storageService.cambiarContraseñaMaestra(entries);
        System.out.println("Clave maestra actualizada con éxito.");
    }

    public void añadirContraseña(Scanner scanner) {
        System.out.print("Servicio (Ej: Gmail): ");
        String service = scanner.nextLine();
        System.out.print("Usuario: ");
        String username = scanner.nextLine();
        System.out.print("Contraseña: ");
        String password = scanner.nextLine();

        String encryptedPassword = passwordUtils.encriptar(password, generateKeyFrom(masterKey));
        storageService.guardarContraseña(service, username, encryptedPassword);

        System.out.println("Contraseña guardada correctamente.");
    }

    public void verContraseñas() {
        List<PasswordEntry> entries = storageService.cargarContraseñas();
        if (entries.isEmpty()) {
            System.out.println("No tienes contraseñas guardadas.");
        } else {
            for (PasswordEntry entry : entries) {
                String decryptedPassword = passwordUtils.descriptar(entry.getContraseñaEncriptada(), generateKeyFrom(masterKey));
                System.out.println("Servicio: " + entry.getServicio());
                System.out.println("Usuario: " + entry.getUsuario());
                System.out.println("Contraseña: " + decryptedPassword);
                System.out.println("-----------------------------");
            }
        }
    }

    public void editarContraseña(Scanner scanner) {
        List<PasswordEntry> entries = storageService.cargarContraseñas();

        if (entries.isEmpty()) {
            System.out.println("No hay contraseñas guardadas.");
            return;
        }

        mostrarListaServicios(entries);

        System.out.print("Selecciona el número del servicio que quieres editar: ");
        int choice = leerOpcion(scanner, entries.size());
        if (choice == -1) return;

        PasswordEntry selectedEntry = entries.get(choice - 1);
        System.out.println("Seleccionado: " + selectedEntry.getServicio() + " (" + selectedEntry.getUsuario() + ")");

        System.out.print("Nuevo usuario: ");
        String newUsername = scanner.nextLine();
        System.out.print("Nueva contraseña: ");
        String newPassword = scanner.nextLine();

        String encryptedPassword = passwordUtils.encriptar(newPassword, generateKeyFrom(masterKey));
        storageService.editarContraseña(selectedEntry.getServicio(), selectedEntry.getUsuario(), newUsername, encryptedPassword);
        System.out.println("Contraseña actualizada correctamente.");
    }

    public void eliminarContraseña(Scanner scanner) {
        List<PasswordEntry> entries = storageService.cargarContraseñas();

        if (entries.isEmpty()) {
            System.out.println("No hay contraseñas guardadas.");
            return;
        }

        mostrarListaServicios(entries);

        System.out.print("Selecciona el número del servicio que quieres eliminar: ");
        int choice = leerOpcion(scanner, entries.size());
        if (choice == -1) return;

        PasswordEntry selectedEntry = entries.get(choice - 1);
        System.out.println("Seleccionado: " + selectedEntry.getServicio() + " (" + selectedEntry.getUsuario() + ")");
        storageService.eliminarContraseña(selectedEntry.getServicio());
        System.out.println("Contraseña eliminada correctamente.");
    }

    // --- UTILIDADES INTERNAS ---

    private void mostrarListaServicios(List<PasswordEntry> entries) {
        System.out.println("Servicios guardados:");
        for (int i = 0; i < entries.size(); i++) {
            PasswordEntry entry = entries.get(i);
            System.out.printf("%d. %s (%s)%n", i + 1, entry.getServicio(), entry.getUsuario());
        }
    }

    private int leerOpcion(Scanner scanner, int limite) {
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > limite) {
                System.out.println("Selección inválida.");
                return -1;
            }
            return choice;
        } catch (NumberFormatException e) {
            System.out.println("Entrada no válida.");
            return -1;
        }
    }

    private void saveMasterKey(String key) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoConfiguracion))) {
            String encrypted = passwordUtils.encriptar(key, generateKeyFrom(key));
            writer.write(encrypted);
        } catch (IOException e) {
            System.out.println("❌ Error al guardar la clave maestra: " + e.getMessage());
        }
    }

    private String[] leerClaves() {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoConfiguracion))) {
            String masterKeyEncrypted = reader.readLine();
            String recoveryKeyEncrypted = reader.readLine();

            // Verificamos que las claves estén presentes en el archivo
            if (masterKeyEncrypted == null || recoveryKeyEncrypted == null) {
                throw new RuntimeException("Las claves no están correctamente guardadas.");
            }

            return new String[]{masterKeyEncrypted, recoveryKeyEncrypted};
        } catch (IOException e) {
            System.out.println("❌ Error al leer las claves guardadas: " + e.getMessage());
            return new String[]{"", ""}; // Devolvemos valores vacíos para manejar el error en el flujo principal
        }
    }


    private void guardarClaves(String claveMaestra, String claveRecuperacion) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoConfiguracion))) {
            String masterEncrypted = passwordUtils.encriptar(claveMaestra, generateKeyFrom(claveMaestra));
            String recoveryEncrypted = passwordUtils.encriptar(claveRecuperacion, generateKeyFrom(claveRecuperacion));

            writer.write(masterEncrypted);
            writer.newLine();
            writer.write(recoveryEncrypted);
        } catch (IOException e) {
            System.out.println("❌ Error al guardar las claves: " + e.getMessage());
        }
    }

    private String generateKeyFrom(String input) {
        try {
            byte[] bytes = input.getBytes("UTF-8");
            // Si la longitud de la clave es menor que 16 bytes, completamos con ceros
            if (bytes.length < 16) {
                byte[] newBytes = new byte[16];
                System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
                return new String(newBytes, "UTF-8");
            } else if (bytes.length > 16) {
                // Si la longitud es mayor a 16, truncamos
                return new String(bytes, 0, 16, "UTF-8");
            } else {
                return new String(bytes, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error al generar la clave", e);
        }
    }


    private String generarClaveRecuperacion() {
        return java.util.UUID.randomUUID().toString().substring(0, 19).toUpperCase().replace("-", "");
    }
}

