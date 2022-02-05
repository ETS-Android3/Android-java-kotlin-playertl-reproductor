package es.tipolisto.player.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ListView
import es.tipolisto.player.Libs.isExternalStorageReadable
import es.tipolisto.player.Libs.isExternalStorageWritable
import es.tipolisto.player.R
import java.io.File

class ExplorerFilesActivity : AppCompatActivity() {
    private val TAG = "tag"
    private var arrayListContenidoDirectorio: ArrayList<File>? = null
    private var arrayListContenidoDirectorioString: ArrayList<String>? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explorer_files)
        //Inicializamos el arraylist
        arrayListContenidoDirectorio = ArrayList<File>()
        arrayListContenidoDirectorioString = ArrayList<String>()
        //Chequeamos los permisos
        isExternalStorageWritable(this)
        isExternalStorageReadable(this)

        //Obtenemos los views
        var gridView:GridView =findViewById(R.id.gridViwewExplorerFilesActivity)

        //Obtenemos e contenido de la SD externa y rellenamos el arrayList
        val SD = File(Environment.getExternalStorageDirectory().toString() + "/")
        val SDFiles = SD.listFiles()
        if(SDFiles!=null){
            for (i in SDFiles.indices) {
                arrayListContenidoDirectorio!!.add(SDFiles[i])
                arrayListContenidoDirectorioString!!.add(SDFiles[i].name)
            }
        }

        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayListContenidoDirectorioString)
        gridView.setAdapter(arrayAdapter)
        mostrarArrayContenidoDirectorio()
    }

    fun mostrarArrayContenidoDirectorio(){
        if(arrayListContenidoDirectorio!=null){
            for (i in arrayListContenidoDirectorio!!.indices) {
                val file = arrayListContenidoDirectorio!![i]
                if (file.isDirectory)
                    Log.d(TAG, file.name+"/")
                else
                    Log.d(TAG, file.name)
            }
        }

    }
}
