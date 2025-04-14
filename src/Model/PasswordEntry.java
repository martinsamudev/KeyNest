package Model;

public class PasswordEntry {

    private String servicio;
    private String usuario;
    private String contraseñaEncriptada;

    public PasswordEntry(String servicio, String usuario, String contraseñaEncriptada) {
        this.servicio = servicio;
        this.usuario = usuario;
        this.contraseñaEncriptada = contraseñaEncriptada;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContraseñaEncriptada() {
        return contraseñaEncriptada;
    }

    public void setContraseñaEncriptada(String contraseñaEncriptada) {
        this.contraseñaEncriptada = contraseñaEncriptada;
    }
}