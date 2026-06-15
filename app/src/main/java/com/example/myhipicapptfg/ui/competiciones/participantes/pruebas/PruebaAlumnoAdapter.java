package com.example.myhipicapptfg.ui.competiciones.participantes.pruebas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.model.PruebaAlumno;


/**
 * Adaptador encargado de mostrar las pruebas en las que participa un alumno.
 *
 * Utiliza ListAdapter y DiffUtil para optimizar la actualización
 * de los elementos mostrados en el RecyclerView.
 */
public class PruebaAlumnoAdapter
        extends ListAdapter<PruebaAlumno, PruebaAlumnoAdapter.VH> {

    /**
     * Interfaz para gestionar la pulsación sobre una prueba.
     */
    public interface OnClick {
        void onClick(PruebaAlumno item);
    }
    private OnClick listener;
    /**
     * Establece el listener que se ejecutará al pulsar una prueba.
     */
    public void setOnClick(OnClick l) {
        this.listener = l;
    }

    /**
     * Constructor del adaptador.
     * Configura DiffUtil para detectar cambios entre elementos.
     */
    public PruebaAlumnoAdapter() {
        super(new DiffUtil.ItemCallback<PruebaAlumno>() {
            /**
             * Comprueba si dos elementos representan la misma prueba.
             */
            @Override public boolean areItemsTheSame(@NonNull PruebaAlumno a,
                                                     @NonNull PruebaAlumno b) {
                return a.idPrueba == b.idPrueba;
            }
            /**
             * Comprueba si el contenido de dos elementos es idéntico.
             */
            @Override public boolean areContentsTheSame(@NonNull PruebaAlumno a,
                                                        @NonNull PruebaAlumno b) {
                return a.idPrueba == b.idPrueba;
            }
        });
    }

    /**
     * Crea una nueva vista para un elemento de la lista.
     */
    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prueba_alumno, parent, false);
        return new VH(v);
    }

    /**
     * Vincula los datos de una prueba con su correspondiente fila.
     */
    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        PruebaAlumno item = getItem(pos);
        h.tvNombrePrueba.setText(item.nombrePrueba);
        h.tvCompeticion.setText(item.nombreCompeticion);
        h.tvCategoria.setText(item.categoria + " · " + item.nivel);
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(item);
        });
    }

    /**
     * Devuelve el número de elementos mostrados.
     */
    @Override public int getItemCount() {
        return getCurrentList().size();
    }

    /**
     * ViewHolder que almacena las referencias a las vistas
     * de cada elemento de la lista.
     */
    static class VH extends RecyclerView.ViewHolder {
        TextView tvNombrePrueba, tvCompeticion, tvCategoria;
        VH(@NonNull View v) {
            super(v);
            tvNombrePrueba = v.findViewById(R.id.tvNombrePrueba);
            tvCompeticion  = v.findViewById(R.id.tvCompeticion);
            tvCategoria    = v.findViewById(R.id.tvCategoria);
        }
    }
}