package es.tipolisto.versionadnroid.Activities;


import android.Manifest;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.widget.*;
import es.tipolisto.versionadnroid.Adapters.ListViewMainActivityAdapter;
import es.tipolisto.versionadnroid.Adapters.SpinnerAdaptermainActivity;
import es.tipolisto.versionadnroid.Databases.CancionesSQLiteOpenHelper;
import es.tipolisto.versionadnroid.Databases.Listas;
import es.tipolisto.versionadnroid.Databases.ListasCanciones;
import es.tipolisto.versionadnroid.MediaPlayerClient;
import es.tipolisto.versionadnroid.Pojos.Cancion;
import es.tipolisto.versionadnroid.Pojos.Lista;
import es.tipolisto.versionadnroid.R;
import es.tipolisto.versionadnroid.SharedPreferencesManager;
import es.tipolisto.versionadnroid.Utils.CheckPermisions;
import es.tipolisto.versionadnroid.Utils.Constants;

import java.util.ArrayList;

import static es.tipolisto.versionadnroid.UI.MenuYToolar.crearMenu;

/**
 * Esta clase será pantalla inicial
 *
 */
public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    //TAG para depuración
    private final String TAG="Mensaje";




    /**
     * Base de datos
     */
    //cancionesSQLiteOpenHelper será la base de datos de donde obtenedremos las listas y canciones
    private CancionesSQLiteOpenHelper cancionesSQLiteOpenHelper;
    //Contedrá una lista de cancionesSQLiteOpenHelper
    private ArrayList<Cancion> arrayListCanciones;
    //listView será el componente que mostrará la lista de cancionesSQLiteOpenHelper
    private ListView listView;
    //list view se apoyará en un adapador para que sea este el que ibbuje los items de la lista
    private ListViewMainActivityAdapter listViewMainActivityAdapter;
    //El spinner contendrá las lista guardadas
    private Spinner spinnerListaActiva;
    //mediaplayer será nuestro proveedor de servicio de audio, configurará, reproducirá, parará, etc
    private MediaPlayerClient mediaPlayerClient;



    //Controles para mostrar información
    private TextView textViewEstado,textViewCancionActical, textViewDuracionCancionActiva;
    private LinearLayout linearLayoutbotonesReproductor;
    private ImageButton imageButtonAnteriorEstilo,imageButtonSiguienteEstilo,imageButtonAnterior, imageButtonRetroceder,imageButtonPlay,imageButtonPause,imageButtonStop,imageButtonAvanzar,imageButtonSiguiente, imageButtonAgregarLista,imageButtonBorrarLista;
    private ImageView imageViewImagenEstilo;
    private SeekBar seekBar;
    private Switch switchReproducirSiguiente;

    //Esto es para dejar la pantalla siempre activada
    private PowerManager.WakeLock mWakeLock;
    //Preferencias
    public SharedPreferencesManager sharedPreferencesManager;
    //private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String[] estilos;
    private String tipoEstilo;

    boolean booleanSwitchReproduccionActumatica;

    private SeekBarThread seekBarThread;
    private boolean estaDeshabilitadaElSeekBarChangeListner;

    int permissionCheck;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferencesManager=new SharedPreferencesManager(this);
        //sharedPreferences = getApplicationContext().getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        editor = sharedPreferencesManager.obtenerEditor();
        estilos= new String[]{"DEFECTO","CLASICO","RETRO", "MODERNO"};

        //tipoEstilo=sharedPreferences.getString("tipoEstilo", "DEFECTO");
        tipoEstilo=sharedPreferencesManager.obtenerTipoEstilo();
        asignarEstilos();
        setContentView(R.layout.activity_main);

        //Creamos la barra de arriba
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.icon_home25x25);
        View logoView = getToolbarLogoIcon(toolbar);
        logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        setSupportActionBar(toolbar);
        //Quitamos el titulo
        getSupportActionBar().setTitle(null);

        //Solicitamos los permisos
        CheckPermisions.checkPermissionWriteStorage(this);
        dejarSiempreActivadaLaPantalla();

        //Creamos la lista, el adapter, el mediaplayer y el eakelock

        inicializar();
        botonesCancionSeekBarYSwitch();
        cambiarImagenesAlCambiarEstilos();
        inicializarSeekBarThread();


        //Si hay un cambio en la pantalla recuperamos la canción (si había que se estaba reproduciendoi
        if(savedInstanceState!=null){
            MediaPlayerClient mediaPlayerClientRecivida=new MediaPlayerClient(this);
            mediaPlayerClientRecivida=(MediaPlayerClient) savedInstanceState.getSerializable("mediaPlayerClient");
            //Log.d(TAG, "MainActivity: onCreate: mediaPlayerClientRecivida "+mediaPlayerClientRecivida.getCancionAtual().getNombre()+", estado: "+mediaPlayerClientRecivida.getEstado()+", milisegundos: "+mediaPlayerClientRecivida.getMiliSegundosDeLaCancionActual());
            Cancion cancionRecvida=mediaPlayerClientRecivida.getCancionAtual();
            String estadoRecivido=mediaPlayerClientRecivida.getEstado();
            final int milisegundosTranscurridosEnLaCancionActivaRecivida=mediaPlayerClientRecivida.getMiliSegundosDeLaCancionActual();
            mediaPlayerClient.setEstado(estadoRecivido);
            //mediaPlayerClient.setCancionActual(cancionRecvida);
            Log.d("mono", "MainActivity: onCreate: mediaPlayerClient "+cancionRecvida.getNombre()+", estado: "+estadoRecivido+" milisegundos: "+milisegundosTranscurridosEnLaCancionActivaRecivida);
            mediaPlayerClientRecivida.stopCancion();
            //mediaPlayerClient.setMiliSegundosDeLaCancionActual(mediaPlayerClientRecivida.getMiliSegundosDeLaCancionActual());
            //mediaPlayerClientRecivida.stopCancion();
            switch (estadoRecivido){
                case "Seleccionada":
                    mediaPlayerClient.setCancionActual(cancionRecvida);
                    break;
                case "Reproduciendo":
                    //mediaPlayerClient.reproducirCancion(cancionRecvida);
                    //mediaPlayerClient.adelantarAlPunto(milisegundosTranscurridosEnLaCancionActivaRecivida);
                    mediaPlayerClient.reproducirCancion(cancionRecvida);
                    mediaPlayerClient.adelantarAlPunto(milisegundosTranscurridosEnLaCancionActivaRecivida);
                    Log.d("mono", "MainActivity: onCreate: mediaPlayerClient: se adelanto al punto "+milisegundosTranscurridosEnLaCancionActivaRecivida+", cancion "+cancionRecvida.getNombre());
                    break;
                case "Pausa":
                    //int progress2=mediaPlayerClientRecivida.getMiliSegundosDeLaCancionActual();
                    //mediaPlayerClient.setCancionActual(mediaPlayerClientRecivida.getCancionAtual());
                    //mediaPlayerClient.adelantarAlPunto(progress2);
                   // mediaPlayerClient.pauseCancion();
                   // Log.d("mono", "MainActivity: onCreate: mediaPlayerClient: estas en pausa: se adelanto al punto "+progress2+", cancion "+mediaPlayerClientRecivida.getCancionAtual().getNombre());
                    break;
                case "Parada":
                    mediaPlayerClient.setCancionActual(mediaPlayerClientRecivida.getCancionAtual());
                    mediaPlayerClient.stopCancion();
                    break;
                    default:
                        Toast.makeText(this, "Acción no detectada", Toast.LENGTH_SHORT).show();

            }
            SeekBarThread seekBarThread=new SeekBarThread();
            seekBarThread.setDurancion((int)mediaPlayerClientRecivida.getCancionAtual().getDuracion());
            seekBarThread.reanudar(mediaPlayerClientRecivida.getMiliSegundosDeLaCancionActual());
            seekBarThread.start();

        }


        //comprobarEstadoYActualizarTextViewYBotones();


        /**
         * Todos los método importantes (setAdapter(), comprobarEstado(),etc) estarán en el onResume para que
         * se ejecuten cuando se cambie laorientación de la pamtalla
         */
    }

    private static View getToolbarLogoIcon(Toolbar toolbar){
        //check if contentDescription previously was set
        boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
        String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
        toolbar.setLogoDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
        //find the view based on it's content description, set programatically or with android:contentDescription
        toolbar.findViewsWithText(potentialViews,contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        //Nav icon is always instantiated at this point because calling setLogoDescription ensures its existence
        View logoIcon = null;
        if(potentialViews.size() > 0){
            logoIcon = potentialViews.get(0);
        }
        //Clear content description if not previously present
        if(hadContentDescription)
            toolbar.setLogoDescription(null);
        return logoIcon;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        //Si no existe la SD la quitamos del menu
        if(!CheckPermisions.verSiExisteSDExterna()){
            MenuItem item_SDCard = menu.findItem(R.id.icon_sd_card);
            item_SDCard.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return crearMenu(this, item.getItemId());
    }

    @Override
    protected void onResume() {
        super.onResume();
        comprobarEstadoYActualizarTextViewYBotones();
    }

    private void inicializar(){
        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionCheck!=-1){
            mediaPlayerClient=new MediaPlayerClient(this);
            //Cargamos la listaActiva
            //int idlistaActiva=sharedPreferences.getInt("listaActiva", 1);
            int idlistaActiva=sharedPreferencesManager.obtenerListaActiva();
            //Obtenemos las canciones que están asociadas a la lista activa
            ListasCanciones listasCanciones=new ListasCanciones(this);
            Log.d(TAG, "MainActivity: onResume: Se consultarán las canciones asociadas al idLista "+idlistaActiva);
            arrayListCanciones=(ArrayList<Cancion>) listasCanciones.selectCancionesDeUnaLista(idlistaActiva);
            listView=findViewById(R.id.listViewListaCancionesActivityMain);
            linearLayoutbotonesReproductor=findViewById(R.id.linearLayoutbotonesReproductor);


            //Si la lista está vacía le ponemos un adapter básico con el item "Lista vacía"
            if(arrayListCanciones==null || arrayListCanciones.size()==0){
                ArrayAdapter arrayAdapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"Lista vacia"});
                listView.setAdapter(arrayAdapter);
                Log.d(TAG, "MainActivity: onResume: La lista esta vacia");
            }else {
                listViewMainActivityAdapter=new ListViewMainActivityAdapter(this, arrayListCanciones, mediaPlayerClient);
                listView.setAdapter(listViewMainActivityAdapter);
            }
        }
        mWakeLock.acquire();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(permissionCheck!=-1){
            //Paramos la canción
            //Sino ponemos esto se reproducirá la siguiente canción porque así es el mediaPlayer.OnCompletion
            //mediaPlayerClient.setAnularReproducirSiguente(false);
            //mediaPlayerClient.stopCancion();
            //Toast.makeText(this, "Al cambiar, la canción s epara", Toast.LENGTH_LONG).show();

        }
        mWakeLock.release();

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(permissionCheck!=-1){

            //mediaPlayerClient.stopCancion();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        seekBarThread.parar();

        //Si ha un cambio en la pantalla guardaremos la canción que se está reproduciendo
        if(mediaPlayerClient!=null && mediaPlayerClient.getCancionAtual()!=null){

            outState.putSerializable("mediaPlayerClient", mediaPlayerClient);

            Log.d(TAG, "Se ha volteado la pantalla");
            //mediaPlayerClient=null;
        }

    }
    /**
     *
     * Permisos
     *
     */

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Toast.makeText(this, "Permiso recibido", Toast.LENGTH_LONG).show();
        switch (requestCode) {
            case CheckPermisions.MY_PERMISSIONS_SD_READ: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permiso de lectura aprobado", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Permiso de lectura denegado, no sepodrá continuar", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
            case CheckPermisions.MY_PERMISSIONS_SD_WRITE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permiso de escritura aprobado", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Permiso de escritura denegado, no se podrá continuar", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
        }
    }



















    /**
     *
     *Estilos
     *
     */
    private void guardarNuevoEstilo(String nuevoEstilo){
        editor.putString("tipoEstilo", nuevoEstilo);
        editor.commit();
    }
    private void asignarEstilos(){
        //El tipodeestilo ya lo obtuvimos en el OnCrete

        if (tipoEstilo.equalsIgnoreCase("DEFECTO"))
        {
            setTheme(R.style.AppTheme_defecto);
            Log.d(TAG, "MainActivity: asignarEstilos: a entrado en defecto");

        }else if (tipoEstilo.equalsIgnoreCase("CLASICO"))
        {
            setTheme(R.style.AppTheme_clasico);
            Log.d(TAG, "MainActivity: asignarEstilos: a entrado en clasico");
        }else if (tipoEstilo.equalsIgnoreCase("RETRO"))
        {
            setTheme(R.style.AppTheme_retro);
            Log.d(TAG, "MainActivity: asignarEstilos: a entrado en retro");
        }else if (tipoEstilo.equalsIgnoreCase("MODERNO"))
        {
            setTheme(R.style.AppTheme_moderno);
            Log.d(TAG, "MainActivity: asignarEstilos: a entrado en moderno");
        }
        Log.d(TAG, "MainActivity: asignarEstilos: el estilo a cambiado a: "+tipoEstilo);
    }
    private void cambiarImagenesAlCambiarEstilos(){
        switch (tipoEstilo){
            case "DEFECTO":
                imageViewImagenEstilo.setImageResource(R.drawable.radio_moderna);
                break;
            case "CLASICO":
                imageViewImagenEstilo.setImageResource(R.drawable.radio_clasica);
                break;
            case "RETRO":
                imageViewImagenEstilo.setImageResource(R.drawable.radio_retro);
                break;
            case "MODERNO":
                imageViewImagenEstilo.setImageResource(R.drawable.radio_moderna);
                break;
        }
    }
    /**
     *
     *Fin de los estilos
     *
     */





    /**
     * Este método lo utilizamos en el adapter para poder borrar las cancionesSQLiteOpenHelper de una lista
     */
    public CancionesSQLiteOpenHelper getCancionesSQLiteOpenHelper(){
        return this.cancionesSQLiteOpenHelper;
    }



    /**
     * Rollo pantalla siempre activa
     */
    private void dejarSiempreActivadaLaPantalla() {
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "tipolisto:My Tag");
    }

    public ArrayList<Cancion> getArrayListCanciones() {
        return arrayListCanciones;
    }

    public void asignarTextoAlTextViewCancionActual(String cancionActual) {
        this.textViewCancionActical.setText(cancionActual);
    }

    public void asignartextoAlTextViewDuracionCancionActiva(String texto){
        this.textViewDuracionCancionActiva.setText(texto);
    }

    public SharedPreferencesManager getSharedPreferencesManager(){
        return sharedPreferencesManager;
    }

    public void comprobarEstadoYActualizarTextViewYBotones(){
        String estado=mediaPlayerClient.getEstado();
        Cancion cancion=mediaPlayerClient.getCancionAtual();
        Log.d(TAG, "MainActivity: comprobarEstadoYActualizarTextViewYBotones1: se ha escrito "+estado+", cancion: "+cancion.getNombre());
        if(!cancion.getNombre().equals("")){
            asignarTextoAlTextViewCancionActual(cancion.getNombre());
        }
        if(!estado.equals("Null")){
            textViewEstado.setText(estado);
        }
        linearLayoutbotonesReproductor.setVisibility(View.VISIBLE);

        switch (estado){
            case "Seleccionada":
                imageButtonAnterior.setVisibility(View.VISIBLE);
                imageButtonRetroceder.setVisibility(View.INVISIBLE);
                imageButtonPlay.setVisibility(View.VISIBLE);
                imageButtonPause.setVisibility(View.INVISIBLE);
                imageButtonStop.setVisibility(View.INVISIBLE);
                imageButtonAvanzar.setVisibility(View.INVISIBLE);
                imageButtonSiguiente.setVisibility(View.VISIBLE);
                break;
            case "Reproduciendo":
                imageButtonAnterior.setVisibility(View.VISIBLE);
                imageButtonRetroceder.setVisibility(View.VISIBLE);
                imageButtonPlay.setVisibility(View.INVISIBLE);
                imageButtonPause.setVisibility(View.VISIBLE);
                imageButtonStop.setVisibility(View.VISIBLE);
                imageButtonAvanzar.setVisibility(View.VISIBLE);
                imageButtonSiguiente.setVisibility(View.VISIBLE);
                break;
            case "Pausa":
                imageButtonAnterior.setVisibility(View.VISIBLE);
                imageButtonRetroceder.setVisibility(View.INVISIBLE);
                imageButtonPlay.setVisibility(View.VISIBLE);
                imageButtonPause.setVisibility(View.INVISIBLE);
                imageButtonStop.setVisibility(View.INVISIBLE);
                imageButtonAvanzar.setVisibility(View.INVISIBLE);
                imageButtonSiguiente.setVisibility(View.VISIBLE);
                break;
            case "Parada":
                imageButtonAnterior.setVisibility(View.VISIBLE);
                imageButtonRetroceder.setVisibility(View.INVISIBLE);
                imageButtonPlay.setVisibility(View.VISIBLE);
                imageButtonPause.setVisibility(View.INVISIBLE);
                imageButtonStop.setVisibility(View.INVISIBLE);
                imageButtonAvanzar.setVisibility(View.INVISIBLE);
                imageButtonSiguiente.setVisibility(View.VISIBLE);
                break;
            case "Finalizada":
                imageButtonAnterior.setVisibility(View.VISIBLE);
                imageButtonRetroceder.setVisibility(View.INVISIBLE);
                imageButtonPlay.setVisibility(View.VISIBLE);
                imageButtonPause.setVisibility(View.INVISIBLE);
                imageButtonStop.setVisibility(View.INVISIBLE);
                imageButtonAvanzar.setVisibility(View.INVISIBLE);
                imageButtonSiguiente.setVisibility(View.VISIBLE);
                break;
                default:
                    linearLayoutbotonesReproductor.setVisibility(View.GONE);
        }




        if(cancion.getDuracion()>0 && !mediaPlayerClient.getEstado().equals("null")){
            seekBar.setActivated(true);
            seekBar.setVisibility(View.VISIBLE);
            final float duracion=cancion.getDuracion();
            seekBar.setMax((int)duracion);
            if(seekBarThread!=null){
                seekBarThread.setDurancion((int)duracion);
                Log.d(TAG, "MainActivity: comprobarEstadoYActualizarTextViewYBotones2: el seebar es null");
            }
        }

        Log.d(TAG, "MainActivity: comprobarEstadoYActualizarTextViewYBotones2: se ha escrito "+estado+", cancion: "+cancion.getNombre());
    }





    private void botonesCancionSeekBarYSwitch(){
        /**
         *
         * Componentes para cambiar los estilos
         *
         */
        imageButtonAnteriorEstilo=findViewById(R.id.imageButtonAnteriorEstilo);
        imageButtonAnteriorEstilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //String estiloActivo=sharedPreferences.getString("tipoEstilo", "DEFECTO");
              String estiloActivo=sharedPreferencesManager.obtenerTipoEstilo();
              for (int i=0; i<estilos.length;i++){

                  if(estilos[i].equals(estiloActivo)){
                      i-=1;
                      if(i<0) i=estilos.length-1;
                      guardarNuevoEstilo(estilos[i]);
                      //Toast.makeText(MainActivity.this, "Se ha cambiado del estilo "+estiloActivo+" al estilo "+estilos[i], Toast.LENGTH_LONG).show();
                      //asignarEstilos();
                      Intent intent=new Intent(MainActivity.this, MainActivity.class);
                      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                      startActivity(intent);
                      break;
                  }
              }
            }
        });

        imageButtonSiguienteEstilo=findViewById(R.id.imageButtonSiguienteEstilo);
        imageButtonSiguienteEstilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //"DEFECTO","CLASICO","RETRO", "MODERNO"
                //String estiloActivo=sharedPreferences.getString("tipoEstilo", "DEFECTO");
                String estiloActivo=sharedPreferencesManager.obtenerTipoEstilo();
                for (int i=0; i<estilos.length;i++){
                    //Log.d(TAG, estilos[i]);
                    if(estilos[i].equals(estiloActivo)){
                       // Log.d(TAG, "estilo encontrado en la posicion "+i);
                        i+=1;
                        if(i>=estilos.length) {
                            //Log.d(TAG, "Hemos llegado al final del array "+i);
                            i=0;
                            //break;
                        }

                        guardarNuevoEstilo(estilos[i]);
                        //Toast.makeText(MainActivity.this, "Se ha cambiado del estilo "+estiloActivo+" al estilo "+estilos[i], Toast.LENGTH_LONG).show();
                        //asignarEstilos();
                        Intent intent=new Intent(MainActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    }
                }
            }
        });

        imageViewImagenEstilo=findViewById(R.id.imageViewEstilo);
        imageViewImagenEstilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String estiloActivo=sharedPreferences.getString("tipoEstilo", "DEFECTO");
                String estiloActivo=sharedPreferencesManager.obtenerTipoEstilo();
                for (int i=0; i<estilos.length;i++){
                    if(estilos[i].equals(estiloActivo)){
                       Toast.makeText(MainActivity.this, "el estilo actual es: "+estilos[i], Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        /**
         *
         * Final de los compnentes para cambiar los estilos
         *
         */






        /**
         * Componetes de botones de navegación canciones
         */
        imageButtonAnterior=findViewById(R.id.imageButtonAnterior);
        imageButtonAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerClient.anteriorCancion(arrayListCanciones);
                asignarTextoAlTextViewCancionActual(mediaPlayerClient.getCancionAtual().getNombre());
                comprobarEstadoYActualizarTextViewYBotones();
            }
        });


        imageButtonAnterior.setVisibility(View.GONE);

        imageButtonRetroceder=findViewById(R.id.imageButtonRetroceder);
        imageButtonRetroceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerClient.retrocederCancion();
            }
        });
        imageButtonRetroceder.setVisibility(View.GONE);



        imageButtonPlay=findViewById(R.id.imageButtonPlay);
        imageButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerClient.reproducirCancionDeLaListaNumero(obtenerNumeroEnLaListaDeestaCancion(mediaPlayerClient.getCancionAtual()), arrayListCanciones);
                comprobarEstadoYActualizarTextViewYBotones();
            }
        });
        imageButtonPlay.setVisibility(View.GONE);


        imageButtonPause=findViewById(R.id.imageButtonPause);
        imageButtonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerClient.pauseCancion();
                comprobarEstadoYActualizarTextViewYBotones();
            }
        });
        imageButtonPause.setVisibility(View.GONE);

        imageButtonStop=findViewById(R.id.imageButtonStop);
        imageButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sino ponemos esto se reproducirá la siguiente canción porque así es el mediaPlayer.OnCompletion
                mediaPlayerClient.setAnularReproducirSiguente(false);
                mediaPlayerClient.stopCancion();
                comprobarEstadoYActualizarTextViewYBotones();
            }
        });
        imageButtonStop.setVisibility(View.GONE);


        imageButtonAvanzar=findViewById(R.id.imageButtonAdelantar);
        imageButtonAvanzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerClient.adelantarCancion();
            }
        });
        imageButtonAvanzar.setVisibility(View.GONE);


        imageButtonSiguiente=findViewById(R.id.imageButtonSiguiente);
        imageButtonSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                asignarTextoAlTextViewCancionActual(mediaPlayerClient.getCancionAtual().getNombre());
                comprobarEstadoYActualizarTextViewYBotones();
            }
        });
        imageButtonSiguiente.setVisibility(View.GONE);

        /**
         * Fin de los componentes para el control y navegación de las canciones
         */








        /**
         * Componentes de información textView quemuestra estado, el nombre, la duración y seekBar
         */
        textViewEstado=findViewById(R.id.textViewEstadoActivityMain);
        textViewCancionActical=findViewById(R.id.textViewCacionActivaActivityMain);
        textViewDuracionCancionActiva=findViewById(R.id.textViewDuracionCacionActivaActivityMain);



        switchReproducirSiguiente=findViewById(R.id.switchReproducirSiguiente);
        //booleanSwitchReproduccionActumatica=sharedPreferences.getBoolean("reproduccion", false);
        booleanSwitchReproduccionActumatica=sharedPreferencesManager.obtenerEstadoSwitchParaReproducciónAutomatica();
        Log.d(TAG, "el valor del switch es "+booleanSwitchReproduccionActumatica);
        if(booleanSwitchReproduccionActumatica){
            mediaPlayerClient.setReproducirSiguiente(true);
            switchReproducirSiguiente.setChecked(true);
        }else{
            mediaPlayerClient.setReproducirSiguiente(false);
            switchReproducirSiguiente.setChecked(false);
        }
        switchReproducirSiguiente.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Guardamos estos cambios en las preferencias, como es una clase anónima las creamos desde cero
                //SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("preferencias", Context.MODE_PRIVATE);
                //SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putBoolean("reproduccion", isChecked);
                editor.commit();
                Log.d(TAG, "grabado el valor del switch como "+isChecked);

                if(isChecked){
                    Toast.makeText(MainActivity.this, "Activada la reprodición automática", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MainActivity.this, "Desactivada la reproducción automática", Toast.LENGTH_LONG).show();
                }
            }
        });
        /**
         * Fin de los componentes de información
         */










        /**
         *
         * 2 botones para el control de las lista y un spinner
         *
         */

        imageButtonAgregarLista=findViewById(R.id.imageButtonAgregarLista);
        imageButtonAgregarLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialog_crear_lista);
                dialog.setTitle("Crear lista nueva");
                final TextView textView = (TextView) dialog.findViewById(R.id.textViewAlertDialogCrearVistaNuevaMainActivity);
                textView.setText("¿Estas seguro que deseas crear una lista nueva?");
                final EditText editTextAlertDialogCrearVista=dialog.findViewById(R.id.editTextAlertDialogCrearVistaNuevaMainActivity);
                ImageButton dialogButton =  dialog.findViewById(R.id.imageButtonCrearListaNuevaAlertDialodMainActivity);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Listas listas=new Listas(getApplicationContext());
                        final Lista lista=new Lista(0, editTextAlertDialogCrearVista.getText().toString(),null);
                        int idListaCreada=listas.insert(lista);
                        if (idListaCreada!=-1){
                            Toast.makeText(MainActivity.this, "Lista "+lista.getNombre()+" creada.", Toast.LENGTH_SHORT).show();
                            editor.putInt("listaActiva",idListaCreada);
                            editor.apply();
                            Intent intent=new Intent( MainActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });


        imageButtonBorrarLista=findViewById(R.id.imageButtonBorrarLista);
        imageButtonBorrarLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Creamos una conexion a la base de datos porque necesitaremos el nombre de lalista
                final Listas listas=new Listas(MainActivity.this);
                //Obtenemos la lista activa de las preferencias
                //final int idlistaActiva=sharedPreferences.getInt("listaActiva", 1);
                final int idlistaActiva=sharedPreferencesManager.obtenerListaActiva();
                //Comprobamos que no sea la lista principal
                if(idlistaActiva==1){
                    Toast.makeText(MainActivity.this, "La lista principal no se puede borrar.", Toast.LENGTH_LONG).show();
                }else{
                    Log.d(Constants.TAG, "MainActivity: botonesCancionSeekBarYSwitch: Onclocik boton menos: Onclick SI: idLista obtenida: "+idlistaActiva);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("¿Estas seguro de que deseas borrar la lista "+listas.selectDevolverNombreDeUnaLista(idlistaActiva)+"? ")
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //1. Ponemos la lista activa en la 1
                                    editor.putInt("listaActiva", 1);
                                    editor.apply();
                                    listas.delete(idlistaActiva);
                                    Toast.makeText(MainActivity.this, "Borrada.", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent( MainActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alertDialog= builder.create();
                    alertDialog.show();
                }


            }
        });



        spinnerListaActiva=findViewById(R.id.spinnerListaActiva);
        final Listas listas=new Listas(MainActivity.this);
        ArrayList<Lista> arrayListListas=listas.selectAllinArraylistString();
        SpinnerAdaptermainActivity adapter = new SpinnerAdaptermainActivity(this, arrayListListas);
        spinnerListaActiva.setAdapter(adapter);
        //Comprobamos la posicion de la lista activa
        //int idListaActiva=sharedPreferences.getInt("listaActiva", 1);
        int idListaActiva=sharedPreferencesManager.obtenerListaActiva();
        //Seleccionamos en el spinner la lista activa
        for(int i=0;i<arrayListListas.size();i++){
            Lista lista=arrayListListas.get(i);
            if(lista.getId()==idListaActiva){
                spinnerListaActiva.setSelection(i);
            }
        }


        /**
         * Fin de componenetes para navegación por las listas y spinner
         */

    }


    private void inicializarSeekBarThread(){

        //El seekbar (barra de busqueda) permite visulizar el tiempo de la canción
        seekBar=findViewById(R.id.seekBarMainActivity);
        //seekBar.setVisibility(View.GONE);
        seekBar.setOnSeekBarChangeListener(this);
        //El seekbar tiene que ser ejecutado en 2 plano
        if(seekBarThread==null){
            seekBarThread=new SeekBarThread();
            seekBarThread.start();
        }else{
            seekBarThread.stop();
        }

    }



    private int obtenerNumeroEnLaListaDeestaCancion(Cancion cancion){
        int posicionCancion=0;
        for (int posicionEnLaLista=0; posicionEnLaLista<arrayListCanciones.size();posicionEnLaLista++){
            Cancion cancionDeLaLista=arrayListCanciones.get(posicionEnLaLista);
            if(cancionDeLaLista.getPath().equals(cancion.getPath())){
                posicionCancion=posicionEnLaLista;
                break;
            }

        }
        Log.d(TAG, "MainActivity: obtenerNumeroEnLaListaDeestaCancion: la cancion tiene la posicion en la lista: "+posicionCancion);
        return posicionCancion;
    }

    public Cancion dameUnaCancionDeLaLista(int numeroDeCancionEnLaLista){
        return arrayListCanciones.get(numeroDeCancionEnLaLista);
    }













    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(mediaPlayerClient.getEstado().equalsIgnoreCase("Reproduciendo")) {
            if(mediaPlayerClient!=null){
                mediaPlayerClient.adelantarAlPunto(progress);
                seekBar.setProgress(progress);
                if(seekBarThread!=null){
                    seekBarThread.reanudar(progress);
                }
            }
        }else {
            Toast.makeText(this, "Pulsa al play", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    public class SeekBarThread extends Thread {
        private int duracion;
        private int miliSegundoActual;
        private String horasYminutos;
        private boolean running;
        public SeekBarThread(){
            this.miliSegundoActual=0;
            this.horasYminutos="";
            this.running=true;
        }
        public void setDurancion(int duracion){
            this.duracion=duracion;
        }
        public void reanudar(int miliSegundoActual){
            this.miliSegundoActual=miliSegundoActual;
        }
        public void parar(){
            this.running=false;
        }
        @Override
        public void run() {
            //Log.d(TAG, "MainActivity: SeekbarThread: corriendo  milisegundo"+miliSegundoActual+", duracion: "+duracion);
            while(running){
               // Log.d(TAG, "MainActivity: SeekbarThread: corriendo  milisegundo"+miliSegundoActual+", duracion: "+duracion);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (miliSegundoActual<duracion){
                    //Log.d(TAG, "MainActivity: SeekbarThread: corriendo "+miliSegundoActual);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            seekBar.setOnSeekBarChangeListener(null);
                            if(mediaPlayerClient.getCancionAtual()!=null){
                                miliSegundoActual = mediaPlayerClient.getMiliSegundosDeLaCancionActual();
                                horasYminutos=mediaPlayerClient.parsearMilisegundosAHorasYMinutos(miliSegundoActual);
                                asignartextoAlTextViewDuracionCancionActiva(horasYminutos);
                                seekBar.setProgress(miliSegundoActual);
                                //Log.d(TAG, "MainActivity: SeekbarThread: en marcha "+horasYminutos);
                            }else{
                                //Log.d(TAG, "MainActivity: SeekbarThread: en marcha la cancione es null");
                            }
                            seekBar.setOnSeekBarChangeListener(MainActivity.this);
                        }
                    });
                }
            }
        }
    }
}
