package es.tipolisto.versionadnroid.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import es.tipolisto.versionadnroid.Activities.MainActivity;
import es.tipolisto.versionadnroid.Databases.Listas;
import es.tipolisto.versionadnroid.Pojos.Lista;
import es.tipolisto.versionadnroid.R;
import es.tipolisto.versionadnroid.SharedPreferencesManager;

public class SpinnerAdaptermainActivity extends BaseAdapter {
    private MainActivity mainActivity;
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Lista> arrayListListas;

    public SpinnerAdaptermainActivity(MainActivity mainActivity, ArrayList<Lista> listas){
        this.mainActivity=mainActivity;
        this.context=mainActivity.getApplicationContext();
        this.layoutInflater=LayoutInflater.from(context);
        this.arrayListListas=listas;

    }
    @Override
    public int getCount() {
        return arrayListListas.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayListListas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Lista lista=arrayListListas.get(position);

        convertView=layoutInflater.inflate(R.layout.item_spinner_listas_mainactivity, null);
        final TextView textView=convertView.findViewById(R.id.textViewItemSpinnerListasMainActivity);
        textView.setText(lista.getNombre());


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesManager sharedPreferencesManager=mainActivity.getSharedPreferencesManager();
                //Si has pinchado em una lista que no es la activa
                if(lista.getId()!=sharedPreferencesManager.obtenerListaActiva()){
                    sharedPreferencesManager.escribirListaActiva(lista.getId());
                    Toast.makeText(context, "Lista cambiada a: "+lista.getNombre(), Toast.LENGTH_LONG).show();
                    Intent intent=new Intent( mainActivity,MainActivity.class);
                    mainActivity.startActivity(intent);
                    mainActivity.finish();
                }

            }
        });





        return convertView;
    }
}
