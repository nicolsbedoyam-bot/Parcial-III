package co.edu.uniquindio.EcoRuta;

public record Aportante(String identificacion, String nombre, String correo) {

    @Override
    public String toString(){
        return "Aportante:" + identificacion + "-" + nombre + "-" + correo;
    }

    @Override
    public String identificacion() {
        return identificacion;
    }

    @Override
    public String nombre() {
        return nombre;
    }

    @Override
    public String correo() {
        return correo;
    }
}
