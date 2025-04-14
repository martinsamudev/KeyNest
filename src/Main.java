import Manager.PasswordManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PasswordManager manager = new PasswordManager(scanner);

        while (true) {
            System.out.println("\n🗝️  Menú Principal - KeyNest");
            System.out.println("-----------------------------------");
            System.out.println("1. Añadir contraseña");
            System.out.println("2. Generar una contraseña segura");
            System.out.println("3. Ver contraseñas");
            System.out.println("4. Editar contraseña");
            System.out.println("5. Eliminar contraseña");
            System.out.println("6. Cambiar clave maestra");
            System.out.println("7. Salir");
            System.out.print("Selecciona una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    manager.añadirContraseña(scanner);
                    break;
                case "2":
                    System.out.print("¿De cuántos caracteres quieres la contraseña? (mínimo 8): ");
                    int length = Integer.parseInt(scanner.nextLine());
                    try {
                        String generatedPassword = Manager.PasswordGenerator.generate(length);
                        System.out.println("Tu contraseña generada es: " + generatedPassword);
                        System.out.println("Puedes copiarla y usarla al guardar un nuevo servicio.");
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "3":
                    manager.verContraseñas();
                    break;
                case "4":
                    manager.editarContraseña(scanner);
                    break;
                case "5":
                    manager.eliminarContraseña(scanner);
                    break;
                case "6":
                    manager.cambiarClaveMaestra(scanner);
                    break;
                case "7":
                    System.out.println("👋 Ciao!");
                    return;
                default:
                    System.out.println("❌ Opción no válida. Inténtalo de nuevo.");
            }
        }
    }
}