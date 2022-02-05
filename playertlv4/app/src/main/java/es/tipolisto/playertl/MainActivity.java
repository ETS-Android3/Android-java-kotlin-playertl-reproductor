package es.tipolisto.playertl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

import es.tipolisto.playertl.adapters.CancionesAdapter;
import es.tipolisto.playertl.model.Cancion;



public class MainActivity extends AppCompatActivity {
    ArrayList<Cancion> arrayCanciones;
    RecyclerView recyclerViewCanciones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewCanciones=(RecyclerView) findViewById(R.id.recyclerViewMainActivity);
        recyclerViewCanciones.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        arrayCanciones=new ArrayList<Cancion>();

       for (int i=0; i<10;i++){
           arrayCanciones.add(new Cancion(i,"cancion "+i, "ruta "+i, 00f,"fecha "+i));
       }
       Cancion cancion=new Cancion(0, "tocate el piripi","ruta piripi", 0,"21/12/2022");
        arrayCanciones.add(cancion);
        CancionesAdapter cancionesAdapter=new CancionesAdapter(arrayCanciones);
        recyclerViewCanciones.setAdapter(cancionesAdapter);
    }
}