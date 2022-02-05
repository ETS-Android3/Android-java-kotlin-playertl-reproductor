package es.tipolisto.versionadnroid.Utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;

public class CheckPermisions {
    public static final int MY_PERMISSIONS_SD_READ=100;
    public static final int MY_PERMISSIONS_SD_WRITE=101;
    //Este método comprobará el permiso de escritura
    public static void solicitarPermisosDeLectura(AppCompatActivity activity){
        String TAG = "tag";
        String state=Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
            Log.d(TAG, "Sin permiso para leer en la sd ");
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_SD_READ);

        }

    }
    //Este método coprobará que tenemos el permiso de lectura
    public static void solicitarPermisosDeEscritura(AppCompatActivity activity){
        String TAG = "tag";
        String state= Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            Log.d(TAG, "Sin permiso para escribir en la sd ");
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_SD_WRITE);
        }
    }
    public static void checkPermissionWriteStorage(AppCompatActivity appCompatActivity){
        if (ContextCompat.checkSelfPermission(appCompatActivity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //Si se puede mostrar el mensaje solicitando los permisos
            if (ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //Solicitamos los permisos
                ActivityCompat.requestPermissions(appCompatActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSIONS_SD_WRITE);
            } else {
                ActivityCompat.requestPermissions(appCompatActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSIONS_SD_WRITE);
            }
        }
    }


    public static boolean isAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean isWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;

    }


    public static boolean verSiExisteSDExterna(){
        boolean SDExternaExiste=false;
        File fileSDExterna = new File("/storage/");

        if(fileSDExterna.exists()){
            File file=null;
            File[] files=fileSDExterna.listFiles();
            for(File file3: files){
                //Log.d(Constants.TAG, "CheckPermisions: verSiExisteSDExterna: "+file3.getName()+", lectura: "+file3.canRead()+", escritura: "+file3.canWrite()+", ejecucion "+file3.canExecute()+", nombre: "+file3.getTotalSpace());
                if(!file3.getName().contains("emulated") );
                {
                    if(file3.canRead()){
                        Log.d(Constants.TAG, "CheckPermisions: verSiExisteSDExterna: Se puede leer");
                        SDExternaExiste=true;
                        break;
                    }else{
                        Log.d(Constants.TAG, "CheckPermisions: verSiExisteSDExterna: no sepuede leer y no existe SD");
                        SDExternaExiste=false;
                        break;
                    }
                }
            }
        }else{
            Log.d(Constants.TAG, "Picha no existe");
            fileSDExterna=null;
        }

        return SDExternaExiste;

    }


}
