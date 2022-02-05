package es.tipolisto.versionadnroid.UI;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import es.tipolisto.versionadnroid.Activities.ExplorerFilesActivity;
import es.tipolisto.versionadnroid.Activities.MainActivity;
import es.tipolisto.versionadnroid.R;

public class MenuYToolar {
    public static final String TAG="Mensaje";
    public static boolean crearMenu(AppCompatActivity appCompatActivity, int id){
        switch (id) {
            case R.id.icon_sd_card:
                //Toast.makeText(appCompatActivity, "Click", Toast.LENGTH_LONG).show();
                Intent intent3=new Intent(appCompatActivity, ExplorerFilesActivity.class);
                intent3.putExtra("path", "SD");
                appCompatActivity.startActivity(intent3);
                Log.d("Mensaje", "Directorio: "+appCompatActivity.getFilesDir());

                return true;
            case R.id.icon_search_files_phone:
                Intent intent2=new Intent(appCompatActivity, ExplorerFilesActivity.class);
                appCompatActivity.startActivity(intent2);
                Log.d(TAG, "Click");
                //Toast.makeText(appCompatActivity, "Click", Toast.LENGTH_LONG).show();
                return true;
            default:
                return false;
        }
    }



}
