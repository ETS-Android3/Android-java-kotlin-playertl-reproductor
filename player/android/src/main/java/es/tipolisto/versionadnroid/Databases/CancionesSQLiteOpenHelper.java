package es.tipolisto.versionadnroid.Databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import es.tipolisto.versionadnroid.Pojos.Cancion;

import java.util.ArrayList;

import static es.tipolisto.versionadnroid.Utils.Constants.TAG;

public class CancionesSQLiteOpenHelper extends SQLiteOpenHelperClient implements IDataBase {
    public CancionesSQLiteOpenHelper(Context context) {
        super(context);
    }

    @Override
    public Cancion selectId(int id) {
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        //int id, String name, String path, String duracion, String fecha
        Cancion cancion=null;

        Cursor cursor=sqLiteDatabase.query("canciones",new String[]{"id","nombre","path","duracion","fecha"},"id=?", new String[]{String.valueOf(id)}, null,null,null, String.valueOf(1));
        /**
         * Si quisieramos utilizar rawQuery (no aconsejado) habría que sustituir las 2 líneas anteriores por:
         * String sql="SELECT * FROM canciones WHERE id='"+id+"'";
         * Cursor cursor=this.sqLiteDatabase.rawQuery(sql, null);
         *         *
         */
        if(cursor.moveToFirst()){
            do{
                cancion=new Cancion(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4));
            }while(cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return cancion;
    }


    @Override
    public Cursor selectAll() {
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        String sql="SELECT * FROM canciones";
        return sqLiteDatabase.rawQuery(sql, null);
    }


    public ArrayList<Cancion> selectAllArrayList() {
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        ArrayList<Cancion> canciones=new ArrayList<>();
        Cancion cancion=null;
        //Cursor cursor=this.sqLiteDatabase.query("canciones",new String[]{"id","nombre","path","duracion","fecha"},null, null, null,null,null, null);
        String sql="SELECT * FROM canciones";
        Cursor cursor= sqLiteDatabase.rawQuery(sql, null);

        if(cursor.moveToFirst()){
            do{
                cancion=new Cancion(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4));
                canciones.add(cancion);
            }while(cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        Log.d(TAG, "CancionesSQLiteOpenHelper: selectAllArrayList: "+canciones.size());
        return canciones;
    }


    public Cancion selectCancionConEstePath(String path) {
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        Cancion cancion=null;
        //Cursor cursor=this.sqLiteDatabase.query("canciones",new String[]{"id","nombre","path","duracion","fecha"},null, null, null,null,null, null);
        String sql="SELECT * FROM canciones WHERE path='"+path+"'";
        Cursor cursor= sqLiteDatabase.rawQuery(sql, null);

        if(cursor.moveToFirst()){
            do{
                cancion=new Cancion(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4));

            }while(cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();

        return cancion;
    }


    @Override
    public int insert(Object object) {
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        Cancion cancion=(Cancion) object;
        String nombre=cancion.getNombre();
        String path=cancion.getPath();
        float duracion=cancion.getDuracion();
        long id=0;
        //int id, String name, String path, String duracion, String fecha
        String sql="INSERT INTO canciones (nombre,path,duracion,fecha) VALUES (?,?,?,datetime(strftime('%Y-%m-%d %H:%M:%S')))";
        SQLiteStatement pst=sqLiteDatabase.compileStatement(sql);
        pst.bindString(1,nombre);
        pst.bindString(2,path);
        pst.bindDouble(3,duracion);
        id=pst.executeInsert();
        sqLiteDatabase.close();
        return (int) id;
    }

    @Override
    public int update(Object object) {
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        Cancion cancion=(Cancion) object;
        int id=cancion.getId();
        String nombre=cancion.getNombre();
        String path=cancion.getPath();
        float duracion=cancion.getDuracion();
        String sql="UPDATE canciones SET nombre='"+nombre+"', path='"+path+"', duracion='"+duracion+"' WHERE id='"+id+"'";
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
        return 0;
    }

    @Override
    public int delete(int id) {
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        String sql="DELETE FROM canciones WHERE id='"+id+"'";
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
        return 0;
    }


    public int deleteAll() {
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        String sql="DELETE FROM canciones ";
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
        return 0;
    }
}
