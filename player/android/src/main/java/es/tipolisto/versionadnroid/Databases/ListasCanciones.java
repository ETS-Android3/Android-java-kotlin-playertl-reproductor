package es.tipolisto.versionadnroid.Databases;

import android.content.Context;
import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;

import es.tipolisto.versionadnroid.Pojos.Cancion;
import es.tipolisto.versionadnroid.Pojos.Lista;
import es.tipolisto.versionadnroid.Pojos.ListaCancion;
import es.tipolisto.versionadnroid.Utils.Constants;

public class ListasCanciones extends SQLiteOpenHelperClient implements IDataBase {
    CancionesSQLiteOpenHelper cancionesSQLiteOpenHelper;
    Listas listas;
    public ListasCanciones(Context context) {
        super(context);
        cancionesSQLiteOpenHelper=new CancionesSQLiteOpenHelper(context);
        listas=new Listas(context);
    }

    @Override
    public Object selectId(int id) {
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        //int id, int idCancion, int idLista
        ListaCancion listaCancion=null;

        Cursor cursor=sqLiteDatabase.query("listasCanciones",new String[]{"idCancion","idLista"},"id=?", new String[]{String.valueOf(id)}, null,null,null, String.valueOf(1));
        /**
         * Si quisieramos utilizar rawQuery (no aconsejado) habría que sustituir las 2 líneas anteriores por:
         * String sql="SELECT * FROM listasCanciones WHERE id='"+id+"'";
         * Cursor cursor=this.sqLiteDatabase.rawQuery(sql, null);
         *         *
         */
        if(cursor.moveToFirst()){
            do{
                listaCancion=new ListaCancion(cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2));
            }while(cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return listaCancion;
    }



    public ArrayList<Cancion> selectCancionesDeUnaLista(int idLista) {
        //Log.d(Constants.TAG, "ListaCnciones: selectCancionesDeUnaLista: la lista recibida es la "+idLista);
        Cancion cancion=null;
        ArrayList<Cancion> arrayListListaCanciones=new ArrayList<>();
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        //int id, int idCancion, int idLista
        ListaCancion listaCancion=null;

        //Cursor cursor=sqLiteDatabase.query("listasCanciones",new String[]{"idCancion","idLista"},"idLista='"+idLista+"", new String[]{"idLista"}, null,null,null, null);

        String sql="SELECT * FROM listasCanciones WHERE idLista='"+idLista+"'";
        Cursor cursor=sqLiteDatabase.rawQuery(sql, null);
        //Log.d(Constants.TAG, "ListaCnciones: selectCancionesDeUnaLista: Se obtuvieron "+cursor.getCount());

        if(cursor.moveToFirst()){
            do{
                int idCancion=cursor.getInt(1);
                cancion=(Cancion) cancionesSQLiteOpenHelper.selectId(idCancion);
                arrayListListaCanciones.add(cancion);
            }while(cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return arrayListListaCanciones;
    }




    @Override
    public Cursor selectAll() {
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        String sql="SELECT * FROM listasCanciones";
        return sqLiteDatabase.rawQuery(sql, null);
    }



    public void logerTodoElContenidoTablaListasCanciones(Cursor cursor){
        //Log.d(Constants.TAG,"ListasCanciones: logerTodoElContenidoTablaListasCanciones(curosr): Contenido de la tabla:");
        if(cursor.moveToFirst()){
            Log.d(Constants.TAG,"\n");
            do{
                Log.d(Constants.TAG,"id: "+cursor.getInt(0)+", idCancion: "+
                        cursor.getInt(1)+", idLista: "+
                        cursor.getInt(2));
            }while(cursor.moveToNext());
        }
        //Log.d(Constants.TAG,"ListasCanciones: logerTodoElContenidoTablaListasCanciones(curosr): Final del contenido de la tabla listasCanciones.");

        cursor.close();
    }





    /**
     * Fin de las consultas
     */
    @Override
    public int insert(Object object) {

        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        //int id, int idCancion, int idLista
        ListaCancion listaCancion=(ListaCancion) object;
        //Log.d(Constants.TAG, "ListasCanciones: insert: vamos a insertar la listaCancion "+listaCancion.toString());
        int idCancion=listaCancion.getIdCancion();
        int idLista=listaCancion.getIdLista();
        //El id se utiliza para devolverlo ya que devolver el que genera la base de datos
        long id=0;
        //int id, String name, String path, String duracion, String fecha
        String sql="INSERT INTO listasCanciones (idCancion,idLista) VALUES (?,?)";
        SQLiteStatement pst=sqLiteDatabase.compileStatement(sql);
        pst.bindLong(1,idCancion);
        pst.bindLong(2,idLista);
        id=pst.executeInsert();
        //Log.d(Constants.TAG, "ListasCanciones: insert: ejecutado insert, datos guardados: id "+id+", idCanción: "+idCancion+", idLista: "+idLista);
        /**
         *También es posible hacer el insert con:
         * sqLiteDatabase.execSQL("INSERT INTO canciones (name,path,duracion,fecha) VALUES ('"+name+"','"+path+"','"+duracion+"''"+name+"',datetime(strftime('%Y-%m-%d %H:%M:%S')))");
         */
        sqLiteDatabase.close();
        return (int)id;
    }

    @Override
    public int update(Object object) {
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        ListaCancion listaCancion=(ListaCancion) object;
        int id=listaCancion.getId();
        int idCancion=listaCancion.getIdCancion();
        int idLista=listaCancion.getIdLista();
        String sql="UPDATE listasCanciones SET idCancion='"+idCancion+"', idLista='"+idLista+"' WHERE id='"+id+"'";
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
        return 0;
    }

    @Override
    public int delete(int id) {
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        String sql="DELETE FROM listasCanciones WHERE id='"+id+"'";
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
        return 0;
    }


    public int deleteCancionesDeLaListaConId(int idCancion, int idLista) {
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        String sql="DELETE FROM listasCanciones WHERE idCancion='"+idCancion+"' AND idLista='"+idLista+"'";
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
        return 0;
    }


    public int deleteAll() {
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        String sql="DELETE FROM listasCanciones";
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
        return 0;
    }

    /**
     * Funciones de ayuda
     */
}

