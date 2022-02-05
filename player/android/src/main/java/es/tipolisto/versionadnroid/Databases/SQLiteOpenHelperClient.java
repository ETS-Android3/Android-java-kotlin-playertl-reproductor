package es.tipolisto.versionadnroid.Databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteOpenHelperClient extends SQLiteOpenHelper {
    //protected SQLiteDatabase sqLiteDatabase;
    private String SQLCreateTableListas="create table listas (" +
            "id integer primary key autoincrement," +
            "nombre varchar (255)," +
            "fecha varchar (255))";
    private final String SQLCreateTableCanciones="create table canciones (id integer primary key autoincrement," +
            "nombre varchar(255)," +
            "path varchar (255), " +
            "duracion varchar (50), " +
            "fecha varchar (50))";
    private final String SQLCreateListasCanciones="create table listasCanciones (id  integer primary key autoincrement," +
            "idCancion integer," +
            "idLista integer," +
            "foreign key(idCancion) references canciones(id) on delete cascade on update cascade," +
            "foreign key(idLista) references listas(id) on delete cascade on update cascade);";

    private final String SQLInsertTableListas="insert into listas (nombre, fecha) values ('Lista principal',datetime(strftime('%Y-%m-%d %H:%M:%S')))";

    public SQLiteOpenHelperClient( Context context) {
        super(context, "reproductorTL.db", null, 1);
        //sqLiteDatabase=getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLCreateTableCanciones);
        db.execSQL(SQLCreateTableListas);
        db.execSQL(SQLCreateListasCanciones);
        db.execSQL(SQLInsertTableListas);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
