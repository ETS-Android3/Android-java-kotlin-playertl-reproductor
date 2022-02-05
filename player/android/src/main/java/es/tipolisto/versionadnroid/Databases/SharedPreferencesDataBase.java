package es.tipolisto.versionadnroid.Databases;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import es.tipolisto.versionadnroid.Pojos.Cancion;
import es.tipolisto.versionadnroid.Utils.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * Esta clase contendrá una lista de onjeto cancion a través de las preferencias
 * que contienen un set de string con las rutas de las canciones, si se lo solcicitan
 * devolverá la lista de objetos canción
 */
public class SharedPreferencesDataBase {
    private Context context;
    private Set listaCancionesSharedPreferences;
    //Este array almacenará las rutas de las canciones alamacenadas
    private ArrayList<Cancion> arrayListCanciones;
    private final String TAG="Mensaje";
    private SharedPreferences sharedPref;
    private static SharedPreferencesDataBase miSharedPreferencesDataBase;

    public static SharedPreferencesDataBase getListaCancionesGuardadas(Context context){
        if(miSharedPreferencesDataBase ==null){
            return new SharedPreferencesDataBase(context);
        }
        return miSharedPreferencesDataBase;
    }
    private SharedPreferencesDataBase(Context context){
        //Obtenemos los datos de la base de datos
        this.listaCancionesSharedPreferences=new HashSet();
        this.arrayListCanciones=new ArrayList<>();
        //La obtenemos de las preferencias
        sharedPref = context.getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        actualizarArrayList(context);

    }

    private void actualizarArrayList(Context context){
        arrayListCanciones.clear();
        this.listaCancionesSharedPreferences= sharedPref.getStringSet("listaCanciones", new HashSet<String>());
        if(listaCancionesSharedPreferences==null || listaCancionesSharedPreferences.size()==0){
            Log.d(TAG, "La lista esta vacia");
        }else{
            Iterator<String> iterator=listaCancionesSharedPreferences.iterator();
            int contadorLista=0;
            while (iterator.hasNext()){
                String path = iterator.next();
                //Log.d(TAG, "SharedPreferencesDataBase dice en metodo actualizarArrayList: "+path);
                try {
                    Uri uriSong= Util.getFileContentUri(context, path);
                    Cancion cancion=new Cancion(0, Util.obtenerNombreDespesBarraEnPath(path),path, 0,null );
                    arrayListCanciones.add(cancion);
                    //Log.d(TAG, "La lista ahora tiene "+arrayListCanciones.size());
                }catch (Exception ex){
                    Log.d(TAG, "Archivo no encontrado "+ex);
                }
                contadorLista++;
            }
            //Log.d(TAG, "CancionesSQLiteOpenHelper guardadas "+arrayListCanciones.size());
        }

    }

    public ArrayList<Cancion> getListaDeCancines(){
        return arrayListCanciones;
    }
    /*
    public ArrayList<String> getListaDeCancinesEnString(){
        ArrayList<String> arrayListString=new ArrayList<>();
        for(Cancion cancion: listaDeCanciones){
            arrayListString.add(file.getName());
        }
        return arrayListString;
    }
    */

    public void insertNuevaCancion(Context context, File file){
        listaCancionesSharedPreferences.add(file.getAbsolutePath());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet("listaCanciones", listaCancionesSharedPreferences);
        //editor.commit();
        editor.apply(); //Se debe de utilizar apply porque es asyncrono según google :https://developer.android.com/training/data-storage/shared-preferences?hl=es-419
        Toast.makeText(context, "Canción "+file.getAbsoluteFile()+", añadida a la lista de canciones.", Toast.LENGTH_LONG).show();
        Log.d("Mensaje", "Cancion"+file.getName()+" guardada");
        actualizarArrayList(context);
    }

    /*
    private void guardarPathEnPreferencias(File file){
        SharedPreferencesDataBase sharedPreferencesDataBase = SharedPreferencesDataBase.getListaCancionesGuardadas(context);
        sharedPreferencesDataBase.insertNuevaCancion(context,file);
        Toast.makeText(context, "La duración es ", Toast.LENGTH_SHORT).show();
    }
    */
}
