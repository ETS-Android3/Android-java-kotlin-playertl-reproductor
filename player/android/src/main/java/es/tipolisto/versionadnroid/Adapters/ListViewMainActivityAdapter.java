package es.tipolisto.versionadnroid.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import es.tipolisto.versionadnroid.Activities.MainActivity;
import es.tipolisto.versionadnroid.Databases.ListasCanciones;
import es.tipolisto.versionadnroid.MediaPlayerClient;
import es.tipolisto.versionadnroid.Pojos.Cancion;
import es.tipolisto.versionadnroid.R;
import es.tipolisto.versionadnroid.Utils.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ListViewMainActivityAdapter extends BaseAdapter {
    private MainActivity mainActivity;
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Cancion> arrayListCanciones;
    private MediaPlayerClient mediaPlayerClient;
    public ListViewMainActivityAdapter(MainActivity mainActivity, ArrayList<Cancion> canciones, MediaPlayerClient mediaPlayerClient){
        this.mainActivity=mainActivity;
        this.context=mainActivity.getApplicationContext();
        this.layoutInflater=LayoutInflater.from(context);
        this.arrayListCanciones=canciones;
        this.mediaPlayerClient=mediaPlayerClient;
    }
    @Override
    public int getCount() {
        return arrayListCanciones.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayListCanciones.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    //Aquí es donde capturamos los eventos, seleccionamos una canción y le ponemos el nombre y el tiempo
    // al mainActivity
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Cancion cancion=arrayListCanciones.get(position);
        int duracionCancion=mediaPlayerClient.obtenerDurcionCancionEnMilisegundos(cancion);
        //Seleccionamos el 1 item por defecto

        String stringDuracionCancion=mediaPlayerClient.parsearAMinutosYSegundos(duracionCancion);
        cancion.setDuracion(duracionCancion);
        //Obtenemos los objetos
        convertView=layoutInflater.inflate(R.layout.item_listview_mainactivity, null);
        final TextView textView=convertView.findViewById(R.id.textViewItemListViewMainActivity);



        if(cancion.getNombre()!=null){
            textView.setText(cancion.getNombre()+": "+stringDuracionCancion);
        }

        //Esto es para comprobar que el archivo de música sigue en su sitio, si no sigue se deshabilitará
        if(!Util.comprobarSiExisteElArchivo(cancion.getPath())){
            textView.setText(cancion.getNombre()+": ya no existe");
            textView.setFocusable(false);
            textView.setEnabled(false);
            textView.setKeyListener(null);
        }


        //El textView ocupa todo el espacio menos la parte de la imagen por eso le ponemos el onclik
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerClient.reproducirCancionDeLaListaNumero(position, arrayListCanciones);
                mainActivity.comprobarEstadoYActualizarTextViewYBotones();
            }
        });
        ImageView imageView=convertView.findViewById(R.id.imageViewEliminarItemListViewMainActivity);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Mensaje","has pinchado en eliminar" );
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setMessage("¿Estas seguro de que deseas borrar la canción "+cancion.getId()+" de la lista ")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //mainActivity.getCancionesSQLiteOpenHelper().delete(cancion.getId());

                                //Obtenemos la lista activa
                                SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("preferencias", Context.MODE_PRIVATE);
                                int idlistaActiva=sharedPreferences.getInt("listaActiva", 1);
                                //borramos lacanción de la lista, como es una relacción de muchos a muchos hay que hacerlo así
                                ListasCanciones listasCanciones=new ListasCanciones(context);
                                listasCanciones.deleteCancionesDeLaListaConId(cancion.getId(), idlistaActiva);

                                //La borramos del arrayList
                                arrayListCanciones.remove(position);
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
        });

        return convertView;
    }
}
