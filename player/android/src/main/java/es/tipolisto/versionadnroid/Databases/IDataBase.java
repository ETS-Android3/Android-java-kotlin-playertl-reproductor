package es.tipolisto.versionadnroid.Databases;

import android.database.Cursor;

public interface IDataBase {
    public Object selectId(int id);
    public Cursor selectAll();
    public int insert(Object object);
    public int update(Object object);
    public int delete(int id);
}
