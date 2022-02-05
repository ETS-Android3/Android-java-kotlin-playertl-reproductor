package es.tipolisto.playertl.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import es.tipolisto.playertl.R;
import es.tipolisto.playertl.model.Cancion;

public class CancionesAdapter extends RecyclerView.Adapter<CancionesAdapter.ViewHolder> {
    //1.creamos la lista y el contructor que le asigna la lista
    ArrayList<Cancion> arrayListCanciones;
    public CancionesAdapter(ArrayList<Cancion> arrayListCanciones){
        this.arrayListCanciones=arrayListCanciones;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //2 .Creamos el layout
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //4.Le actualizamos los cambios en el holder
        holder.asignarDatos(arrayListCanciones.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayListCanciones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //3.Creamos el acceso a los texteView del layout
        TextView nombreTexView,rutaTextViewm, duracionTextView, fechatextView;
        public ViewHolder(View view) {
            super(view);
            nombreTexView=(TextView) view.findViewById(R.id.nombre);
            rutaTextViewm=(TextView) view.findViewById(R.id.path);
            duracionTextView=(TextView) view.findViewById(R.id.duracion);
            fechatextView=(TextView) view.findViewById(R.id.fecha);
        }
        //5.Le asignamos los cambios
        public void asignarDatos(Cancion cancion) {
            nombreTexView.setText(cancion.getNombre());
            rutaTextViewm.setText(cancion.getPath());
            duracionTextView.setText(String.valueOf(cancion.getDuracion()));
            fechatextView.setText(cancion.getFecha());
        }
    }
}