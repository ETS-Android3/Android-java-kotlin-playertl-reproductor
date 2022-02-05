package es.tipolisto.versionadnroid.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import es.tipolisto.versionadnroid.Activities.ExplorerFilesActivity;
import es.tipolisto.versionadnroid.Databases.*;
import es.tipolisto.versionadnroid.Pojos.Cancion;
import es.tipolisto.versionadnroid.Pojos.Lista;
import es.tipolisto.versionadnroid.Pojos.ListaCancion;
import es.tipolisto.versionadnroid.R;
import es.tipolisto.versionadnroid.SharedPreferencesManager;
import es.tipolisto.versionadnroid.Utils.Constants;
import es.tipolisto.versionadnroid.Utils.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static es.tipolisto.versionadnroid.Utils.Constants.TAG;

public class GridViewArrayListExplorerFilesAdapter extends BaseAdapter {
    private Context context;
    private AppCompatActivity appCompatActivity;
    private LayoutInflater layoutInflater;
    private ArrayList<File> files;
    private SharedPreferencesManager sharedPreferencesManager;
    private ArrayList<Cancion> arrayListCancionesDeLaListaActiva;
    private ListasCanciones listasCanciones;
    private int idListaActiva;
    public GridViewArrayListExplorerFilesAdapter(AppCompatActivity appCompatActivity, ArrayList<File> files){
        this.appCompatActivity=appCompatActivity;
        this.context=appCompatActivity.getApplicationContext();
        this.layoutInflater=LayoutInflater.from(context);
        this.files=files;
        this.sharedPreferencesManager=new SharedPreferencesManager(context);
        this.idListaActiva=1;
        this.arrayListCancionesDeLaListaActiva=new ArrayList<>();
        this.listasCanciones=new ListasCanciones(appCompatActivity);
    }
    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final File file=files.get(position);
        //Obtenemos los objetos
        convertView=layoutInflater.inflate(R.layout.item_gridview_explorer_files, null);
        ImageButton imageButton=convertView.findViewById(R.id.imageViewItemGridVViewExplorerFiles);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ExplorerFilesActivity.class);
                intent.putExtra("path", file.getAbsolutePath());
                context.startActivity(intent);
            }
        });
        TextView textView=convertView.findViewById(R.id.textViewItemGridVViewExplorerFiles);
        CheckBox checkBox=convertView.findViewById(R.id.checkBoxItemGridViewExplorerFilesAdapter);
        //Sino es un archivo y si no es música no mostramos el checkbox
        if(!file.isFile()){
            checkBox.setVisibility(View.GONE);
        }else{
            if(Util.comprobarQueElArhivoEsMusica(file)){
                Log.d(TAG, "GridViewArrayListExplorerFilesAdapter: getView: el archivo "+file.getName()+" es musica");
            }else{
                Log.d(TAG, "GridViewArrayListExplorerFilesAdapter: getView: el archivo "+file.getName()+" no es musica");
                checkBox.setVisibility(View.GONE);
            }
        }
        /**
         *    Comprobamos que la ruta de este archivo ya está en la lista activa de canciones
         *
         */
        idListaActiva=sharedPreferencesManager.obtenerListaActiva();
        arrayListCancionesDeLaListaActiva=listasCanciones.selectCancionesDeUnaLista(idListaActiva);
        if(comprobarSiLaCancionEstaEnLaLista(file.getPath())){
            checkBox.setChecked(true);
        }else{
            checkBox.setChecked(false);
        }
        //Cuando se checkee...
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(file.isFile()){
                        //1 comprobamos que es música
                        if(Util.comprobarQueElArhivoEsMusica(file)){
                            //Toast.makeText(context, "Si es musica", Toast.LENGTH_LONG).show();
                            guardarPathEnSQlite(file);
                        }else{
                            Toast.makeText(context, "No es musica", Toast.LENGTH_LONG).show();
                        }
                        //guardarPathEnSQlite(file);
                    }else{
                        crearAlerDialogGuardarCanciones(file);
                    }
                }else{
                    CancionesSQLiteOpenHelper cancionesSQLiteOpenHelper=new CancionesSQLiteOpenHelper(appCompatActivity);
                    Cancion cancion=cancionesSQLiteOpenHelper.selectCancionConEstePath(file.getPath());
                    eliminarCancionDeLaListaActiva(cancion);
                    Toast.makeText(context, "Se eliminó la canción de lalista activa", Toast.LENGTH_SHORT).show();
                }
            }


        });
        if(file.isDirectory()){


                imageButton.setImageResource(R.drawable.icon_folder50x50);

        }else{
            if(Util.comprobarQueElArhivoEsMusica(file)){
                //Log.d("Mensaje", "GridViewAdapter getView: dice: es un archivo de musica");
                imageButton.setImageResource(R.drawable.archivo_musica);
            }else{
                imageButton.setImageResource(R.drawable.icon_files50x50);
            }

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Mensaje", "GridViewAdapter dice: Has hecho click es un archivo");
                    lanzarMenuParaElegirAplicacion();
                }
            });
        }
        textView.setText(file.getName());
        return convertView;
    }

    private void eliminarCancionDeLaListaActiva(final Cancion cancion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(appCompatActivity);
        builder.setMessage("¿Estas seguro de que deseas borrar la canción "+cancion.getId()+" de la lista ")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listasCanciones.deleteCancionesDeLaListaConId(cancion.getId(), sharedPreferencesManager.obtenerListaActiva() );
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alertDialog= builder.create();
        alertDialog.show();
    }


    private void guardarPathEnSQlite(File file){
        /**
         * Para guardar un archivo tenemos que guardarlo en la tabla listasCanciones
         * para ello tenemos que crear un idCancion y un idLista
         *
         */
        //1.Obtenemos el idCancion
        Cancion cancion=new Cancion(0, file.getName(), file.getPath(), 0, "Sin definir");

        CancionesSQLiteOpenHelper canciones=new CancionesSQLiteOpenHelper(context);
        int idResultadoCancion=canciones.insert(cancion);
        cancion.setId(idResultadoCancion);

        if(idResultadoCancion!=0){
            //2.Obtenemos el idLista
            SharedPreferences  sharedPreferences = context.getSharedPreferences("preferencias", Context.MODE_PRIVATE);
            //3.Crearemos una conexión listas para si queremos el nombre de un idLista en oncreto
            Listas listas=new Listas(context);
            //Obtenemos el idListaActiva guardada en las preferencias
            int idListaActiva=sharedPreferences.getInt("listaActiva", 1);
            //4.Insertamos el listaCancion
            ListaCancion listaCancion=new ListaCancion(0, cancion.getId(), idListaActiva);
            ListasCanciones listasCanciones=new ListasCanciones(context);
            int idResultadoListaCancion=listasCanciones.insert(listaCancion);
            Log.d(Constants.TAG, "GridViewArrayListExplorerFilesAdapter: guardarPathEnSQlite: se insterá la canción: "+cancion.getId()+" ("+cancion.getNombre()+") y la lista "+idListaActiva+ " ("+listas.selectDevolverNombreDeUnaLista(idListaActiva)+")");
            if(idResultadoCancion!=0){
                Toast.makeText(context, "Insertada.", Toast.LENGTH_LONG).show();
                Cursor cursor=listasCanciones.selectAll();
                listasCanciones.logerTodoElContenidoTablaListasCanciones(cursor);
            }

        }
    }

    private void lanzarMenuParaElegirAplicacion(){
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        //Crear intención de mostrar el diálogo de selección.
        Intent chooser = Intent.createChooser(sendIntent, "Elige una aplicación para abrir el archivo");
        //Verifique que la intención original se resolverá al menos a una actividad.
        if (sendIntent.resolveActivity(context.getPackageManager()) != null) {
            appCompatActivity.startActivity(chooser);
            Log.d("Mensaje", "lanzada aplicacion");
        }else{
            Log.d("Mensaje", "l content resolver e snull");
        }
    }


    private void crearAlerDialogGuardarCanciones(File file){
        //int id, String name, String path, String duracion, String fecha
        //String duracionCancion=obtenerDuracionCancion(file.getAbsolutePath());
        Cancion cancion=new Cancion(0, file.getName(), file.getAbsolutePath(), 0, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(appCompatActivity);
        builder.setMessage("¿Deseas agregar una nueva canción a la lista?--->"+file.getName())
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String obtenerDuracionCancion(String path){
        int duracion=0;
        try{
            MediaPlayer mediaPlayer=new MediaPlayer();
            mediaPlayer.setDataSource(context.getApplicationContext(), Util.getFileContentUri(context, path));
            mediaPlayer.prepare();
            duracion=mediaPlayer.getDuration();
            mediaPlayer.release();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "ha ocurrido la excepcion "+e.getMessage());
            Toast.makeText(context, "Imposible reproducir este archivo.", Toast.LENGTH_LONG).show();
        }
        return String.valueOf(duracion);
    }




    public boolean comprobarSiLaCancionEstaEnLaLista(String path){
        boolean cancionEncontrada=false;
        for(Cancion cancion: arrayListCancionesDeLaListaActiva){
            if(cancion.getPath().equals(path)){
                cancionEncontrada=true;
                break;
            }
        }
        return cancionEncontrada;
    }
}
