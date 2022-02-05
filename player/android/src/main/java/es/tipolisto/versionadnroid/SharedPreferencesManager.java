package es.tipolisto.versionadnroid;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    /**
     * Uliciremos el patrón singletñón ya que la misma instancia del objeto la necesitamos en
     * todas las clases de la aplicación
     */

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    /*public static SharedPreferencesManager getInstancia(){
        if(instancia==null){
            instancia=new SharedPreferencesManager();
        }
        return instancia;
    }*/

    public SharedPreferencesManager(Context context){
        this.context=context;
        sharedPreferences=context.getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    public SharedPreferences.Editor obtenerEditor(){
        return editor;
    }
    public String obtenerTipoEstilo(){
        return sharedPreferences.getString("tipoEstilo", "DEFECTO");
    }
    public int obtenerListaActiva(){
        return sharedPreferences.getInt("listaActiva", 1);
    }
    public void escribirListaActiva(int idListaActiva){
        editor.putInt("listaActiva", idListaActiva);
        editor.apply();
    }

    public boolean obtenerEstadoSwitchParaReproducciónAutomatica(){
        return sharedPreferences.getBoolean("reproduccion", false);
    }

    public void grabarTipoEstiloEnPreferencias(){

    }

    public void grabarListaActivaEnPreferencias(){

    }

}
