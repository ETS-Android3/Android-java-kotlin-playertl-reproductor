package es.tipolisto.player.Libs

import android.Manifest
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log

fun isExternalStorageWritable(activity:AppCompatActivity): Boolean{
    val TAG = "tag"
    val state=Environment.getExternalStorageState()
    if(Environment.MEDIA_MOUNTED.equals(state)){
        Log.d(TAG, "Sin permiso para escribir en la sd ")
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)

        return true
    }
    return false
}

fun isExternalStorageReadable(activity:AppCompatActivity):Boolean{
    val TAG = "tag"
    val state=Environment.getExternalStorageState()
    if(Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
        Log.d(TAG, "Sin permiso para leer en la sd ")
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)

        return true
    }
    return false
}