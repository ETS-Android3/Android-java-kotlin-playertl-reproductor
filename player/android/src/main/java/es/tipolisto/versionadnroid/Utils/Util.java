package es.tipolisto.versionadnroid.Utils;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Util {



    public static Uri getFileContentUri(Context context, String filePath) {
        //String filePath = file.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,new String[] { MediaStore.Audio.Media._ID },MediaStore.Audio.Media.DATA + "=? ",new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (filePath!=null) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    //Este metodo sirve para obtener las URIS de loas imágenes
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /*
    private File obtenerFileConElPath(String path){
        File file=null;
        SharedPreferences sharedPref = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        Set listaCancionesSharedPreferences= sharedPref.getStringSet("listaCanciones", new HashSet<String>());
        Log.d(TAG, "Tamaño lista de canciones: "+listaCancionesSharedPreferences.size());
        if(listaCancionesSharedPreferences==null || listaCancionesSharedPreferences.size()==0){
            Log.d(TAG, "La lista esta vacia");
        }else{
            Iterator<String> iterator=listaCancionesSharedPreferences.iterator();
            while (iterator.hasNext()){
                String ruta = iterator.next();

                //Log.d(TAG, "Elemnto lista de cannciones: "+id);
            }

        }

        return file;
    }*/
    /*public static void checkPermissionWriteStorage(AppCompatActivity appCompatActivity){
        if (ContextCompat.checkSelfPermission(appCompatActivity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(appCompatActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            } else {
                ActivityCompat.requestPermissions(appCompatActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        }
    }
*/
    public static String obtenerNombreDespesBarraEnPath(String path){
        int tamanio=path.length();
        int posicionBarra=path.lastIndexOf('/');
        return path.substring(posicionBarra,posicionBarra);
    }


    public static boolean comprobarSiExisteElArchivo(String path){
        File file=new File(path);
        if(file.exists()){
            return true;
        }else{
            return false;
        }
    }

    public static String obetnerDirectorioPadre(String path){
        String directorioPadre="";
        int posiciónUltimaBarra=path.lastIndexOf("/");
        directorioPadre=path.substring(0, posiciónUltimaBarra);
        return directorioPadre;
    }


    public static boolean comprobarQueElArhivoEsMusica(File file){
        String path=file.getAbsolutePath();
        int posiciónUltimaBarra=path.lastIndexOf(".");
        String cancion=path.substring(posiciónUltimaBarra+1, path.length());
        cancion=cancion.toLowerCase();
        if(cancion.equals("mp3") || cancion.equals("wav") || cancion.equals("ogg") ){
            return true;
        }else{
            return false;
        }
    }

}
