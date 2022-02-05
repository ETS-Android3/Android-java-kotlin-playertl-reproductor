package es.tipolisto.versionadnroid.Pojos;

public class ListaCancion {
    private int id;
    private int idCancion;
    private int idLista;

    public ListaCancion(int id, int idCancion, int idLista) {
        this.id = id;
        this.idCancion = idCancion;
        this.idLista = idLista;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCancion() {
        return idCancion;
    }

    public void setIdCancion(int idCancion) {
        this.idCancion = idCancion;
    }

    public int getIdLista() {
        return idLista;
    }

    public void setIdLista(int idLista) {
        this.idLista = idLista;
    }

    @Override
    public String toString() {
        return "ListasCanciones{" +
                "id=" + id +
                ", idCancion=" + idCancion +
                ", idLista=" + idLista +
                '}';
    }
}
