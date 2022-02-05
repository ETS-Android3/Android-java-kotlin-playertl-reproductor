package es.tipolisto.playertl.repository;

import java.util.ArrayList;

import es.tipolisto.playertl.model.Cancion;
import es.tipolisto.playertl.model.Lista;
import es.tipolisto.playertl.model.ListaCancion;

public interface ListaCancionRepository {
    public ArrayList<Cancion> getAllListasCanciones();
    public ArrayList<Cancion> getCancionesDeLaLista1();
    public ArrayList<Cancion> getCancionesDeLaLista2();
}

class ListCancionRepositoryImp implements ListaCancionRepository{
    ArrayList<Cancion> arrayCanciones;
    Cancion cancion1, cancion2, cancion3,cancion4,cancion5,cancion6,cancion7,cancion8;
    ListaCancion listaCancion1,listaCancion2,listaCancion3,listaCancion4,listaCancion5;
    Lista lista1,lista2,lista3;
    public ListCancionRepositoryImp(){
        arrayCanciones=new ArrayList<>();
        //int id, String nombre, String path, float duracion, String fecha
        cancion1 = new Cancion(1, "nombre Cancion1", "ruta cancion 1", 20f, "1/1/22");
        cancion2 = new Cancion(2, "nombre Cancion2", "ruta cancion 2", 20f, "2/1/22");
        cancion3 = new Cancion(3, "nombre Cancion3", "ruta cancion 3", 20f, "3/1/22");
        cancion4 = new Cancion(4, "nombre Cancion4", "ruta cancion 4", 20f, "4/1/22");
        cancion5 = new Cancion(5, "nombre Cancion5", "ruta cancion 5", 20f, "5/1/22");
        cancion6 = new Cancion(6, "nombre Cancion6", "ruta cancion 6", 20f, "6/1/22");
        cancion7 = new Cancion(7, "nombre Cancion7", "ruta cancion 7", 20f, "7/1/22");
        cancion8 = new Cancion(8, "nombre Cancion8", "ruta cancion 8", 20f, "8/1/22");

        //int id, String nombre, String fecha
        lista1 = new Lista(1, "nombre lista1", "1/1/22");
        lista2 = new Lista(2, "nombre lista2", "2/1/22");
        lista3 = new Lista(3, "nombre lista3", "3/1/22");


        //int id, int idCancion, int idLista
        listaCancion1 = new ListaCancion(0, 1, 1);
        listaCancion2 = new ListaCancion(1, 2, 1);
        listaCancion3 = new ListaCancion(2, 3, 1);
        listaCancion4 = new ListaCancion(3, 4, 2);
        listaCancion5 = new ListaCancion(4, 5, 2);

    }
    @Override
    public ArrayList<Cancion> getAllListasCanciones() {
        arrayCanciones.clear();

        arrayCanciones.add(cancion1);
        arrayCanciones.add(cancion2);
        arrayCanciones.add(cancion3);
        arrayCanciones.add(cancion4);
        arrayCanciones.add(cancion5);
        arrayCanciones.add(cancion6);
        arrayCanciones.add(cancion7);
        arrayCanciones.add(cancion8);


        return arrayCanciones;
    }

    public ArrayList<Cancion> getCancionesDeLaLista1() {
        arrayCanciones.clear();

        return arrayCanciones;
    }

    public ArrayList<Cancion> getCancionesDeLaLista2() {
        arrayCanciones.clear();

        return arrayCanciones;
    }
}
