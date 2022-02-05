package es.tipolisto.versionadnroid.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import es.tipolisto.versionadnroid.Adapters.GridViewArrayListExplorerFilesAdapter;
import es.tipolisto.versionadnroid.Databases.Listas;
import es.tipolisto.versionadnroid.Databases.ListasCanciones;
import es.tipolisto.versionadnroid.Pojos.Cancion;
import es.tipolisto.versionadnroid.SharedPreferencesManager;
import es.tipolisto.versionadnroid.Utils.CheckPermisions;
import es.tipolisto.versionadnroid.R;
import es.tipolisto.versionadnroid.Utils.Constants;
import es.tipolisto.versionadnroid.Utils.Util;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;

import static es.tipolisto.versionadnroid.UI.MenuYToolar.crearMenu;

public class ExplorerFilesActivity extends AppCompatActivity {
    private final static String TAG="Mensaje";
    //Esta variable solo sirve para poner invisible el botón de atrás de la navegación de directorios
    private String pathActual;
    /**
     * UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer_files);


        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.icon_home25x25);
        View logoView = getToolbarLogoIcon(toolbar);
        logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ExplorerFilesActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        setSupportActionBar(toolbar);
        //Quitamos el titulo
        getSupportActionBar().setTitle(null);





        //Obtenemos los widgets y inicializamos las variables globales
        GridView gridView=findViewById(R.id.gridViwewExplorerFilesActivity);
        TextView textViewTitulo=findViewById(R.id.textViewExplorerFilesActivity);
        ImageButton imageButtonDirectorioAnterior=findViewById(R.id.imageButtonDirectorioAnteriorExplorerFilesActivity);

        imageButtonDirectorioAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ExplorerFilesActivity.this, "Padre: "+), Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(ExplorerFilesActivity.this, ExplorerFilesActivity.class);

                intent.putExtra("path", Util.obetnerDirectorioPadre(pathActual));
                startActivity(intent);
            }
        });
        /*Variables*/
        //Recuperamos el intent y miramos si se  ha pasado algo
        Bundle bundle= getIntent().getExtras();
        //Esta variable representa el directorio o archivo actual
        File f;
        //El arrayList contiene todos los archivos y directorios del directorio actual
        ArrayList<File> filesAndFolders=new ArrayList<>();

        if(bundle==null){
            //Si no hay un intent con extras el directorio actual será el móvil
            f = new File(Environment.getExternalStorageDirectory().getPath());
            //Obtenemos todos los archivos y directorios del directorio actual
            filesAndFolders=devolverDirectoriosYArchivosOrdenados(f);
            //Escribimos la ruta en el textView
            textViewTitulo.setText(f.getAbsolutePath());
            Log.d(TAG, "No hay extras");
            //Esta bariable solo sirve para poner invisible el botón de atrás de la navegaci´n de directorios
            pathActual=f.getAbsolutePath();
        } else{
            //Si hay un intent coin extras miramos el valor conenido enla variable path
            String path=bundle.getString("path");

            Log.d(TAG, "Si hay extras: "+path);
            //Si en la variable path estaba el string SD obtenemos la ruta de la SD externa
            if(path.equalsIgnoreCase("SD")){
                f=obtenerFileSDExterna();
                //Log.d(TAG, "picha: "+f.getAbsolutePath());
                if(f==null){
                    Toast.makeText(this, "SD no encontrada.", Toast.LENGTH_LONG).show();
                }else{
                    filesAndFolders=devolverDirectoriosYArchivosOrdenados(f);
                }
                textViewTitulo.setText(f.getAbsolutePath());
            }else{
                textViewTitulo.setText(path);
                f=new File(path+"/");
                Log.d(TAG, "picha: "+f.getAbsolutePath());
            }
            //Esta bariable solo sirve para poner invisible el botón de atrás de la navegaci´n de directorios
            pathActual=f.getAbsolutePath();
            //Si lo que se muestra es un directorio habrá que ordenar lo archivos o mostrr un mensaje de que está vacío
            if (f.isDirectory()){
                Log.d(TAG, "Lo que has hecho click es un directorio");
                File[] arrayFiles= f.listFiles();
                if(arrayFiles.length==0){
                    //filesAndFolders.add(new File(f.getAbsolutePath()+"/Lista vacia"));
                    TextView textView=findViewById(R.id.textViewExplorerFilesActivity);
                    textView.setText("El directorio está vacío");
                }else{
                    filesAndFolders=devolverDirectoriosYArchivosOrdenados(f);
                    Log.d(TAG, "El contenido es del tamaño "+filesAndFolders.size());
                    //verLogDeItemsDeFilesDeArrayList(filesAndFolders);
                }
            }
        }


        if(pathActual.equals(Environment.getExternalStorageDirectory().getPath())){
            imageButtonDirectorioAnterior.setVisibility(View.INVISIBLE);
        }else if(pathActual.equals(obtenerFileSDExterna().getAbsolutePath())){
            imageButtonDirectorioAnterior.setVisibility(View.INVISIBLE);
        }
        GridViewArrayListExplorerFilesAdapter adapter=new GridViewArrayListExplorerFilesAdapter(this,filesAndFolders);
        gridView.setAdapter(adapter);

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return crearMenu(this, item.getItemId());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Fin de UI
     *
     */









    /**
     *
     * Trabajos con array
     */

    private void verLogDeItemsDeStringsDeArray(String[] array){
        for(int i=0; i<array.length;i++){
            Log.d(TAG, "-->"+array[i]);
        }
    }
    private void verLogDeItemsDeStringsDeArrayList(ArrayList<String> array){
        for(int i=0; i<array.size();i++){
            Log.d(TAG, "-->"+array.get(i));
        }
    }
    private void verLogDeItemsDeFilesDeArray(File[] array){
        for(int i=0; i<array.length;i++){
            Log.d(TAG, "-->"+array[i].getName());
        }
    }
    private void verLogDeItemsDeFilesDeArrayList(ArrayList<File> array){
        for(int i=0; i<array.size();i++){
           // Log.d(TAG, "-->"+array.get(i).getName());
        }
    }

    private File devolverFileConNombre(String nombre, File[] arrayFiles){
        for(File file:arrayFiles){
            if(file.getName().equals(nombre)) return file;
        }
        return null;
    }

    private ArrayList<File> devolverDirectoriosYArchivosOrdenados(File f){
        //1.obtenemos e contenido de la SD y creamos los arrayñist de ayuda
        File[] arrayFiles= f.listFiles();

         ArrayList<String>filesNames=new ArrayList<String>();
         ArrayList<String>folderNames=new ArrayList<String>();
         ArrayList<File> filesAndFolders=new ArrayList<>();

        //2 añadimos los nombres de los directorios al arrayList folderNames
        for (int i = 0; i < arrayFiles.length; i++)
        {
            File file = arrayFiles[i];
            if (file.isDirectory())folderNames.add(file.getName());
        }
        //3.Ordenamos los nombres de los directorios
        Collections.sort(folderNames);
        //4.Obtenemos los nombres de los archivos
        for (int i = 0; i < arrayFiles.length; i++)
        {
            File file = arrayFiles[i];
            if (file.isFile())filesNames.add(file.getName());
        }
        //5.Ordenamos los nombres de los archivos
        Collections.sort(filesNames);

        //6.Creamos un array en el que le meteremos primero los directorios y despues los archivos ordenados
        String[] stringFiles=new String[folderNames.size()+filesNames.size()];
        //Log.d(TAG, "Tamaño del array: "+stringFiles.length);
        for (int i=0;i<folderNames.size();i++){
            stringFiles[i]=folderNames.get(i);
        }
        int posicionFiles=0;
        for (int i=folderNames.size();i<folderNames.size()+filesNames.size();i++){
            stringFiles[i]=filesNames.get(posicionFiles);
            posicionFiles++;
            //Log.d(TAG,""+ i);
        }

        //7. Con otro array añadiremos los files que tengan esos nombres
        for(int i=0; i<stringFiles.length;i++){
            File file=devolverFileConNombre(stringFiles[i],arrayFiles);
            filesAndFolders.add(file);
        }
        return filesAndFolders;
    }

    /**
     * Fin de trabajos con arry
     *
     */









    private File obtenerFileSDExterna(){
        File fileSDExterna = new File("/storage/");

        if(fileSDExterna.exists()){
            File file=null;
            File[] files=fileSDExterna.listFiles();
            for(File file3: files){
                //Log.d(TAG, "Picha :"+file3.getName()+", lectura: "+file3.canRead()+", escritura: "+file3.canWrite()+", ejecucion "+file3.canExecute()+", nombre: "+file3.getTotalSpace());
                if(!file3.getName().contains("emulated")  );
                {
                    //Log.d(TAG, "No contiene emulated");
                    if(file3.canRead()){
                        //Log.d(TAG, "Se puede ller");
                        fileSDExterna=file3;
                        break;
                    }
                }
            }
        }else{
            Log.d(TAG, "Picha no existe");
            fileSDExterna=null;
        }
        return fileSDExterna;
    }









}
