package es.tipolisto.versionadnroid.Pojos;

import android.net.Uri;
import es.tipolisto.versionadnroid.Utils.Util;

import java.io.File;

public class Cancion {
    private int id;
    private String nombre;
    private String path;
    private Uri uriSong;
    private float duracion;
    private String fecha;

    public Cancion(int id, String nombre, String path, float duracion, String fecha) {
        this.id = id;
        this.nombre = nombre;
        this.path = path;
        this.duracion = duracion;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public float getDuracion() {
        return duracion;
    }

    public void setDuracion(float duracion) {
        this.duracion = duracion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }


    @Override
    public String toString() {
        return "Cancion{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", path='" + path + '\'' +
                ", uriSong=" + uriSong +
                ", duracion=" + duracion +
                ", fecha='" + fecha + '\'' +
                '}';
    }
}
