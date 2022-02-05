package es.tipolisto.versionadnroid.Pojos;

public class Lista {
    private int id;
    private String nombre;
    private String fecha;

    public Lista(int id, String nombre, String fecha) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }


    @Override
    public String toString() {
        return "Lista{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", fecha='" + fecha + '\'' +
                '}';
    }
}
