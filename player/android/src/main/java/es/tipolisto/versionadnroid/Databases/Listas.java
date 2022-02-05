package es.tipolisto.versionadnroid.Databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;

import es.tipolisto.versionadnroid.Pojos.Lista;
import es.tipolisto.versionadnroid.Utils.Constants;

public class Listas extends SQLiteOpenHelperClient implements IDataBase{

    public Listas(Context context) {
        super(context);
    }

    @Override
    public Object selectId(int id) {
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        //int id, String nombre, String fecha
        Lista lista=null;

        Cursor cursor=sqLiteDatabase.query("listas",new String[]{"id","nombre","fecha"},"id=?", new String[]{String.valueOf(id)}, null,null,null, String.valueOf(1));
        /**
         * Si quisieramos utilizar rawQuery (no aconsejado) habría que sustituir las 2 líneas anteriores por:
         * String sql="SELECT * FROM listas WHERE id='"+id+"'";
         * Cursor cursor=this.sqLiteDatabase.rawQuery(sql, null);
         *         *
         */
        if(cursor.moveToFirst()){
            do{
                lista=new Lista(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2));
            }while(cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return lista;
    }

    public String selectDevolverNombreDeUnaLista(int id) {
        Log.d(Constants.TAG, "Listas: selectDevolverNombreDeUnaLista(id): vamos a ver el nombre de la lista con el id: "+id);

        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        String nombreLista="";

        String sql="SELECT * FROM listas WHERE id='"+id+"'";
        Cursor cursor=sqLiteDatabase.rawQuery(sql, null);
        Log.d(Constants.TAG, "Listas: selectDevolverNombreDeUnaLista(id): tamaño del cursor: "+cursor.getCount());
        if(cursor.moveToFirst()){
            do{
                nombreLista=cursor.getString(1);
                Log.d(Constants.TAG, "Listas: selectDevolverNombreDeUnaLista(id): obtenida: "+nombreLista);

            }while(cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        Log.d(Constants.TAG, "Listas: selectDevolverNombreDeUnaLista(id): se consulto por un nombre de lista y se obtuvo "+nombreLista);
        return nombreLista;
    }

    @Override
    public Cursor selectAll() {
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        String sql="SELECT * FROM listas";
        return sqLiteDatabase.rawQuery(sql, null);
    }



    public void logerTodoElContenidoTablaListas(Cursor cursor){
        //int id, String nombre, String fecha
        Log.d(Constants.TAG,"Listas: logerTodoElContenidoTablaListas(curosr): Contenido de la tabla listas:");
        if(cursor.moveToFirst()){
            Log.d(Constants.TAG,"\n");
            do{
                Log.d(Constants.TAG,"id: "+cursor.getInt(0)+", nombre: "+
                        cursor.getString(1)+", fecha: "+
                        cursor.getString(2));
            }while(cursor.moveToNext());
        }
        Log.d(Constants.TAG,"Listas: logerTodoElContenidoTablaListas(curosr): Final del contenido de la tabla listas.");

        cursor.close();
    }


    public ArrayList<Lista> selectAllinArraylistString(){
        //int id, String nombre, String fecha
        Lista lista=null;
        ArrayList<Lista> arrayList=new ArrayList<>();
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        String sql="SELECT * FROM listas";
        Cursor cursor=sqLiteDatabase.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            Log.d(Constants.TAG,"\n");
            do{
                lista=new Lista(cursor.getInt(0),cursor.getString(1),cursor.getString(2));
                arrayList.add(lista);
            }while(cursor.moveToNext());
        }
        return arrayList;
    }




    public ArrayList<String> selectAllNamesInArraylistString(){
        ArrayList<String> arrayListString=new ArrayList<>();
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        String sql="SELECT * FROM listas";
        Cursor cursor=sqLiteDatabase.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            Log.d(Constants.TAG,"\n");
            do{
                arrayListString.add(cursor.getString(1));
            }while(cursor.moveToNext());
        }
        return arrayListString;
    }








    public void logerContenidoListaCursor(Cursor cursor){
        if(cursor.moveToFirst()){
            do{
                Log.d(Constants.TAG,"Listas: logerContenidoListaCursor: "+cursor.getInt(0)+", "+
                        cursor.getString(1)+","+
                        cursor.getString(2));
            }while(cursor.moveToNext());
        }
        cursor.close();
    }


    /**
     *
     * Fin de consultas selesct
     *
     */

    @Override
    public int insert(Object object) {
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        //int id, String nombre, String fecha
        Lista lista=(Lista) object;
        String name=lista.getNombre();
        //String path=lista.getFecha();
        long id=0;
        //int id, String name, String path, String duracion, String fecha
        String sql="INSERT INTO listas (nombre,fecha) VALUES (?,datetime(strftime('%Y-%m-%d %H:%M:%S')))";
        SQLiteStatement pst=sqLiteDatabase.compileStatement(sql);
        pst.bindString(1,name);
        id=pst.executeInsert();
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
        Lista lista=(Lista) object;
        int id=lista.getId();
        String nombre=lista.getNombre();
        String fecha=lista.getFecha();
        String sql="UPDATE listas SET nombre='"+nombre+"', fecha='"+fecha+"' WHERE id='"+id+"'";
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
        return 0;
    }

    @Override
    public int delete(int id) {
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        String sql="DELETE FROM listas WHERE id='"+id+"'";
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
        return 0;
    }

    public int deleteAll() {
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        String sql="DELETE FROM listas";
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
        return 0;
    }
}
