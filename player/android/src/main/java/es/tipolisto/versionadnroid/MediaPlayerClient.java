package es.tipolisto.versionadnroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import es.tipolisto.versionadnroid.Activities.MainActivity;
import es.tipolisto.versionadnroid.Pojos.Cancion;
import es.tipolisto.versionadnroid.Utils.Util;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Esta clase reproducirá y seleccionará las canciones de una lista que habrá que meterle
 */
public class MediaPlayerClient implements MediaPlayer.OnPreparedListener, Serializable {
    private final String TAG="Mensaje";
    private String estado;
    private MediaPlayer mediaPlayer;
    //Contendrá la lista de canciones actual
    //private ArrayList<Cancion> arrayListCanciones;
    private int segundosReproducidos;
    //CancionEnReproducion se utiliza para guardar la ultima cancion escogida
    private Cancion cancionEnReproduccion;
    private MainActivity mainActivity;
    private int miliSegundosDeLaCancionActual;
    private boolean reproducirSiguiente;
    private boolean anularReproducirSiguente;

    public MediaPlayerClient(final MainActivity mainActivity){
        this.mainActivity=mainActivity;
        segundosReproducidos=0;
        miliSegundosDeLaCancionActual=0;
        estado="Null";

        cancionEnReproduccion=new Cancion(0,"Sin cancion",Environment.getDownloadCacheDirectory().getPath(),0, null);
        anularReproducirSiguente=true;
        //Inicializamos el mediaPlayer
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                Log.d(TAG, "mediaPlayerClient: completion : se completó la cancion se reproducirá la siguiente");

                SharedPreferences  sharedPreferences = mainActivity.getApplicationContext().getSharedPreferences("preferencias", Context.MODE_PRIVATE);
                reproducirSiguiente=sharedPreferences.getBoolean("reproduccion", false);
                Log.d(TAG, "mediaPlayerClient: completion "+mp.getCurrentPosition()+"--------"+mp.getDuration());
                if(reproducirSiguiente && anularReproducirSiguente){
                    stopCancion();
                    Cancion cancion=dameLaSiguienteCancion(cancionEnReproduccion);
                    int numeroEnLaLista=obtenerNumeroEnLaListaDeestaCancion(cancion,mainActivity.getArrayListCanciones());
                    Log.d(TAG, "mediaPlayerClient: oncompletion: la cancion obtenida es "+cancion.getNombre()+", numero de la lista la: "+numeroEnLaLista);
                    //Toast.makeText(mainActivity, "mediaPlayerClient: oncompletion: la cancion obtenida es "+cancion.getNombre()+", numero de la lista la: "+numeroEnLaLista, Toast.LENGTH_LONG).show();
                    Log.d(TAG, "mediaPlayerClient: oncompletion:  Esta en la lista el numero "+numeroEnLaLista);
                    try {
                        Thread.sleep(2000);
                        reproducirCancion(cancion);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mainActivity.comprobarEstadoYActualizarTextViewYBotones();
            }
        });

        //El audioManager se utiliza para ajustar el volumen
        /*
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        */

    }

    public Cancion getCancionAtual() {
        return cancionEnReproduccion;
    }
    public void setCancionActual(Cancion cancion){
        this.cancionEnReproduccion=cancion;
    }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado){
        this.estado=estado;
    }

    public void setReproducirSiguiente(boolean reproducirSiguiente) {
        this.reproducirSiguiente = reproducirSiguiente;
    }

    public int getMiliSegundosDeLaCancionActual() {
        miliSegundosDeLaCancionActual=mediaPlayer.getCurrentPosition();
        return miliSegundosDeLaCancionActual;
    }
    public void setMiliSegundosDeLaCancionActual(int miliSegundosDeLaCancionActual){
        this.miliSegundosDeLaCancionActual=miliSegundosDeLaCancionActual;
    }
    //Este método anulará la reproducción siguiente cuando se presione en el main el play
    public void setAnularReproducirSiguente(boolean anularReproducirSiguente) {
        this.anularReproducirSiguente = anularReproducirSiguente;
    }

    public void reproducirCancionDeLaListaNumero(int numeroCancionDeLaLista, ArrayList<Cancion> arrayListCanciones) {
        /**
         * 1 comprobamos si la cancion se ha vuelto a plusar, es decir si es la misma
         * 2 Si es la misma que se ha vuelto a pulsar y se estaba reproduciendo la ponemos en pausa
         *
         */
        anularReproducirSiguente=true;
        //Si el numero de la canción en la lista es mayor que el array empezamos desde el 1
        if(numeroCancionDeLaLista >= arrayListCanciones.size()){
            numeroCancionDeLaLista=0;
        }
        final Cancion cancion=arrayListCanciones.get(numeroCancionDeLaLista);

        //Comprobamos que la que está reproduciendo no es la misma que han vuelto a tocar
        Uri uriSongEnReproduccion =Util.getFileContentUri(mainActivity,cancionEnReproduccion.getPath());
        Uri uriSong =Util.getFileContentUri(mainActivity,cancion.getPath());
        Log.d(TAG, "MediaPlayerClient: reproducirCancionDeLaListaNumero(numero, arrarCanciones): canción "+cancion.getNombre()+", número de la lista: "+numeroCancionDeLaLista+", cancionGuaradada: "+cancionEnReproduccion.getPath());
        //Si la canción que se ha hecho clic en la lista no es la misma que la guarda
        if(!uriSongEnReproduccion.getPath().equalsIgnoreCase(uriSong.getPath())){
            mediaPlayer.stop();
            mediaPlayer.reset();
            segundosReproducidos=0;
            cancionEnReproduccion=cancion;
            estado="Seleccionada";
            Log.d(TAG, "mediaPlayerClient: reproducirCancionDeLaListaNumero(numero, arrarCanciones): has pinchado en otra cancion: "+cancion.getNombre()+", se reinicia todo, estado: "+estado);
        }else{ //Si es la misma canción...
            if(mediaPlayer.isPlaying()){ //Y se estaba reproduciendo la ponemos en pausa y conservamos el punto en el que estaba
                //Puede ser que se estaba finalizada
                if(estado.equalsIgnoreCase("Finalizada")){
                    try {
                    mediaPlayer.setDataSource(mainActivity.getApplicationContext(), uriSong);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    cancionEnReproduccion=cancion;
                   // mainActivity.asignartextoAlTextViewDuracionCancionActiva(String.valueOf(mediaPlayer.getDuration()));
                    //cancionEnReproduccion.setDuracion(mediaPlayer.getDuration());
                    estado="Reproduciendo";
                    //Toast.makeText(mainActivity, "   >   "+cancion.getNombre(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "mediaPlayerClient: reproducirCancionDeLaListaNumero(numero, arrarCanciones): ha ocurrido la excepcion "+e.getMessage());
                       //Toast.makeText(mainActivity, "Imposible reproducir este archivo.", Toast.LENGTH_LONG).show();
                    }
                }else{
                    //mediaPlayer.pause();
                    pauseCancion();
                    segundosReproducidos=mediaPlayer.getCurrentPosition();
                }
                Log.d(TAG, "mediaPlayerClient: reproducirCancionDeLaListaNumero(numero, arrarCanciones): Cancion se estaba reproduciendo y se ha quedado en pausa, segundos: "+segundosReproducidos);
               // Toast.makeText(mainActivity, "   ||   ", Toast.LENGTH_LONG).show();
            }else{ //Si no se estaba reprodiendo vemos si estaba en pausa con el punto guardado
                if(segundosReproducidos!=0){
                    mediaPlayer.seekTo(segundosReproducidos);
                    mediaPlayer.start();
                    estado="Reproduciendo";
                   // Toast.makeText(mainActivity, "   ||  --->  >   "+cancion.getNombre(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "mediaPlayerClient: reproducirCancionDeLaListaNumero(numero, arrarCanciones): La cancion estaba en pausa con un punto guardado: "+segundosReproducidos);
                }else{ //Si el punto guardado es 0 es que no hay ninguna canción reproduciendo
                    try {
                        //if(mediaPlayerSessionId!=mediaPlayer.getAudioSessionId()){
                            mediaPlayer.setDataSource(mainActivity.getApplicationContext(), uriSong);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            cancionEnReproduccion=cancion;
                            estado="Reproduciendo";
                            Log.d(TAG, "mediaPlayerClient: reproducirCancionDeLaListaNumero(numero, arrarCanciones): Cancion ha comenzado desde cero");
                            cancion.setDuracion(mediaPlayer.getDuration());
                            //mediaPlayerSessionId=mediaPlayer.getAudioSessionId();
                       // }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "mediaPlayerClient: reproducirCancionDeLaListaNumero(numero, arrarCanciones): ha ocurrido la excepcion "+e.getMessage());
                        //Toast.makeText(mainActivity, "Imposible reproducir este archivo.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }




    public void anteriorCancion(ArrayList<Cancion> arrayListCanciones){
        int posicionCancionEnreproduccion=obtenerNumeroEnLaListaDeestaCancion(cancionEnReproduccion, arrayListCanciones);
        posicionCancionEnreproduccion--;
        //Toast.makeText(mainActivity, "Vamos a reproducir la cancion anterior: "+posicionCancionEnreproduccion, Toast.LENGTH_LONG).show();
        reproducirCancionDeLaListaNumero(posicionCancionEnreproduccion,arrayListCanciones);
    }
    public void retrocederCancion(){
        Log.d(TAG, " reroceder");
    }

    public void pauseCancion(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            segundosReproducidos=mediaPlayer.getCurrentPosition();
            estado="Pausa";
            Log.d(TAG, "mediaPlayerClient: pausaCancion: has pulsado pausa porque había una canción que se estaba reproduciendo.");
        }else{
            Log.d(TAG, "mediaPlayerClient: pausaCancion: has pulsado pausa pero no se esta reproduciendo nada");
        }
    }

    public void stopCancion(){
        estado="Parada";
        //if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.reset();
            segundosReproducidos=0;
            Log.d(TAG, "MediaPlayerClient: stopCancion: stop");
        //}else{
           // Log.d(TAG, "MediaPlayerClient: stopCancion: no se puede para porque no se estaba reproduciendo nada");
        //}
    }
    public void adelantarCancion(){
        Log.d(TAG, " avanzar");
    }

    public void adelantarAlPunto(int punto){

        if(mediaPlayer.isPlaying()){
            segundosReproducidos=punto;
            mediaPlayer.seekTo(punto);
            Log.d("mono","MediaPlayerClient: adelantarAlPunto: se adelanto al punto: "+punto);
        }
    }


    //Como 1 la seleccionamos utilizamos este método para que sea más facil
    public void reproducirCancion(Cancion cancion){
        ArrayList<Cancion> arrayListCanciones=mainActivity.getArrayListCanciones();
        int posicionCancionEnreproduccion=obtenerNumeroEnLaListaDeestaCancion(cancion, arrayListCanciones);

        reproducirCancionDeLaListaNumero(posicionCancionEnreproduccion,arrayListCanciones);
        //Como la primera vex solo la selecciona la llamamos otra vez
        reproducirCancionDeLaListaNumero(posicionCancionEnreproduccion,arrayListCanciones);
    }



    public Cancion dameLaSiguienteCancion(Cancion cancion){
        Cancion cancionADevolver=null;
        ArrayList<Cancion> arrayListCanciones=mainActivity.getArrayListCanciones();
        //Obtenemos la posicion en el arrayList de la cancionEnreproduccion
        int posicionCancionEnreproduccion=obtenerNumeroEnLaListaDeestaCancion(cancion, arrayListCanciones);
        posicionCancionEnreproduccion++;
        //Aumentamos en uno en la lista la siguiente cancion si llegamos al maximo ponemos 0
        if(posicionCancionEnreproduccion>=arrayListCanciones.size()){
            posicionCancionEnreproduccion=0;
        }
        //Comprobamos si existe la canción
        Cancion cancionSiguiente=arrayListCanciones.get(posicionCancionEnreproduccion);
        if(!Util.comprobarSiExisteElArchivo(cancionSiguiente.getPath())){
            cancionADevolver=dameLaSiguienteCancion(cancionSiguiente);
        }else{
            cancionADevolver=arrayListCanciones.get(posicionCancionEnreproduccion);
        }
        return cancionADevolver;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
    }



    private int obtenerNumeroEnLaListaDeestaCancion(Cancion cancion, ArrayList<Cancion> arrayListCanciones){
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



    public String parsearAMinutosYSegundos(int milisegundos){
        float minutos=0.00f;
        String stringMinutos="";
        String stringMinutos2="";
        int intMinutosParteDerecha=0;
        int intMinutosParteDerecha2=0;
        int intMinutosParteIzquierda=0;
        DecimalFormat decimalFormat = new DecimalFormat("0000.00");
        DecimalFormat decimalFormat2 = new DecimalFormat("00");
        minutos=(milisegundos/1000)/60f;
        stringMinutos=decimalFormat.format(minutos);
        //Log.d(TAG, "mediaPlayerClient: obtnerDuracionCancion(cancion): a entrado: "+milisegundos+", la duracion de la cancion es: "+minutos+", con decimalformat: "+stringMinutos);
        if(!stringMinutos.equals("") || stringMinutos!=null){
            intMinutosParteIzquierda=Integer.parseInt(stringMinutos.substring(0,stringMinutos.indexOf(",")));
            intMinutosParteDerecha=Integer.parseInt(stringMinutos.substring(stringMinutos.indexOf(",")+1, stringMinutos.length()));
            //Log.d(TAG, "mediaPlayerClient: obtnerDuracionCancion(cancion): parte izquierda: "+intMinutosParteIzquierda+", conparte derecha: "+intMinutosParteDerecha);

            if(intMinutosParteDerecha>59){
                intMinutosParteDerecha2=intMinutosParteDerecha-60;
                intMinutosParteIzquierda+=1;
            }else{
                intMinutosParteDerecha2=intMinutosParteDerecha;
            }
            stringMinutos2=intMinutosParteIzquierda+":"+decimalFormat2.format(intMinutosParteDerecha2);
            //Log.d(TAG, "mediaPlayerClient: obtnerDuracionCancion(cancion): convertido a: "+intMinutosParteIzquierda+", conparte derecha: "+intMinutosParteDerecha2+", devuelto: "+stringMinutos2);

        }else{
            stringMinutos2="Espera...";
        }
        return stringMinutos2;
    }

    public String parsearCronometro(int miliSegundos){
        //Log.d(TAG, "mediaPlayerClient: parsearCronometro(segundos): ha llegado "+segundos);
        //1 hay que dividir los segundos entre mil ya que nos da milisegundos
        int segundos=miliSegundos/1000;
        DecimalFormat decimalFormatMinutos = new DecimalFormat("0000.00");
        DecimalFormat decimalFormatSegundos = new DecimalFormat("00");
        int minutos;
        minutos=miliSegundos/60;
        int intMinutosParteIzquierda=0;
        int intMinutosParteDerecha=0;
        String stringMinutos=decimalFormatMinutos.format(minutos);

        intMinutosParteIzquierda=Integer.parseInt(stringMinutos.substring(0,stringMinutos.indexOf(",")));
        intMinutosParteDerecha=Integer.parseInt(stringMinutos.substring(stringMinutos.indexOf(",")+1, stringMinutos.length()));
        //Log.d(TAG, "mediaPlayerClient: obtnerDuracionCancion(cancion): parte izquierda: "+intMinutosParteIzquierda+", conparte derecha: "+intMinutosParteDerecha);

        if(intMinutosParteDerecha>59){
            intMinutosParteDerecha=intMinutosParteDerecha-60;
            intMinutosParteIzquierda+=1;
        }

        if(segundos<60){
            stringMinutos=intMinutosParteIzquierda+":"+decimalFormatSegundos.format(segundos);
        }else{
            stringMinutos=intMinutosParteIzquierda+":"+decimalFormatSegundos.format(intMinutosParteDerecha);
        }

        //Log.d(TAG, "mediaPlayerClient: parsearCronometro(segundos): devueltos "+stringMinutos);
        return stringMinutos;
    }

    public String parsearMilisegundosAHorasYMinutos(int milisegundos){
        long seconds = (int) (milisegundos / 1000) % 60 ;
        long minutes = (int) ((milisegundos / (1000*60)) % 60);
        long hours   = (int) ((milisegundos / (1000*60*60)) % 24);
        String cadena=minutes+":"+seconds;
        if(hours!=0){
            cadena=hours+":"+minutes+":"+seconds;
        }
        return cadena ;
    }


    public String obtenerDuracionDeIntAString(float minutos){
        DecimalFormat decimalFormat = new DecimalFormat("####.00");
        String stringMinutos="0";
        String stringDuracion="";
        String stringMinutosIzquierda="";
        String stringMinutosDerecha="";
        stringMinutos=decimalFormat.format(minutos);
        stringMinutosIzquierda=stringMinutos.substring(0,stringMinutos.indexOf(","));
        stringMinutosDerecha=stringMinutos.substring(stringMinutos.indexOf(",")+1, stringMinutos.length());
        stringMinutos=stringMinutosIzquierda+":"+stringMinutosDerecha;
        return stringMinutos;
    }


    public int obtenerDurcionCancionEnMilisegundos(Cancion cancion){
        int milisegundos=0;
        Uri uriSong =Util.getFileContentUri(mainActivity,cancion.getPath());
        try {
            MediaPlayer mediaPlayer2=new MediaPlayer();
            mediaPlayer2.setDataSource(mainActivity.getApplicationContext(), uriSong);
            mediaPlayer2.prepare();
            milisegundos=mediaPlayer2.getDuration();
            mediaPlayer2.release();
            mediaPlayer2=null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "mediaPlayerClient: obtenerDurcionCancionEnMilisegundos(cancion): ha ocurrido la excepcion "+e.getMessage());
        }
        return milisegundos;
    }



}
