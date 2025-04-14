package Storage;

import Model.PasswordEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StorageService {

    private static final String archivoContraseñas = "passwords.txt";

    //Leer contraseñas guardadas
    public List<PasswordEntry> cargarContraseñas(){
        List<PasswordEntry> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoContraseñas))){
            String line;
            while ((line = reader.readLine()) != null){
                String[] parts = line.split("\\|");
                if (parts.length == 3){
                    entries.add(new PasswordEntry(parts[0], parts[1], parts[2]));
                }
            }
        }catch (IOException e){
            throw new RuntimeException("Error al cargar las contraseñas", e);
        }
        return entries;
    }


    //Guardar nueva contraseña
    public void guardarContraseña(String servicio, String usuario, String contraseñaEncriptada){

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(archivoContraseñas, true))){
            writer.write(servicio + "|" + usuario + "|" + contraseñaEncriptada);
            writer.newLine();
        }catch (IOException e){
            throw new RuntimeException("Error al guardar la contraseña" , e);
        }
    }

    //Editar contraseña
    public void editarContraseña(String serviceToUpdate, String usuarioOriginal, String newUsername, String newEncryptedPassword) {
        List<PasswordEntry> entries = cargarContraseñas();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoContraseñas))) {
            for (PasswordEntry entry : entries) {
                if (entry.getServicio().equalsIgnoreCase(serviceToUpdate) &&
                        entry.getUsuario().equalsIgnoreCase(usuarioOriginal)) {
                    // Reemplazamos usuario y contraseña
                    writer.write(serviceToUpdate + "|" + newUsername + "|" + newEncryptedPassword);
                } else {
                    writer.write(entry.getServicio() + "|" + entry.getUsuario() + "|" + entry.getContraseñaEncriptada());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al actualizar la contraseña.", e);
        }
    }


    //Eliminar contraseña
    public void eliminarContraseña(String service) {
        List<PasswordEntry> entries = cargarContraseñas();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoContraseñas))) {
            for (PasswordEntry entry : entries) {
                if (!entry.getServicio().equals(service)) {
                    writer.write(entry.getServicio() + "|" + entry.getUsuario() + "|" + entry.getContraseñaEncriptada());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al eliminar la contraseña.", e);
        }
    }

    public void cambiarContraseñaMaestra(List<PasswordEntry> nuevasEntradas) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoContraseñas))) {
            for (PasswordEntry entry : nuevasEntradas) {
                writer.write(entry.getServicio() + "|" + entry.getUsuario() + "|" + entry.getContraseñaEncriptada());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al reescribir las contraseñas.", e);
        }
    }
}