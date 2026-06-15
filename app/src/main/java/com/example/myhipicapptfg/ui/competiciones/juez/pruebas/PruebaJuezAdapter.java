package com.example.myhipicapptfg.ui.competiciones.juez.pruebas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.model.PruebaConCompeticion;

/**
 * Adaptador para el RecyclerView que muestra las pruebas asignadas a un juez.
 *
 * Utiliza ListAdapter junto con DiffUtil para actualizar únicamente los
 * elementos que cambian, mejorando el rendimiento frente a un RecyclerView.Adapter tradicional.
 */
public class PruebaJuezAdapter
        extends ListAdapter<PruebaConCompeticion, PruebaJuezAdapter.ViewHolder> {

    /**
     * Interfaz que permite notificar cuando el usuario pulsa sobre una prueba.
     */
    public interface OnPruebaClickListener {
        void onPruebaClick(PruebaConCompeticion prueba);
    }

    // Listener que recibirá los eventos de clic sobre los elementos de la lista
    private final OnPruebaClickListener listener;

    /**
     * Callback de DiffUtil encargado de comparar elementos antiguos y nuevos.
     * Gracias a esto RecyclerView actualiza únicamente los ítems modificados.
     */
    private static final DiffUtil.ItemCallback<PruebaConCompeticion> DIFF =
            new DiffUtil.ItemCallback<PruebaConCompeticion>() {
                @Override
                public boolean areItemsTheSame(@NonNull PruebaConCompeticion a,
                                               @NonNull PruebaConCompeticion b) {
                    return a.idPrueba == b.idPrueba;
                }
                @Override
                public boolean areContentsTheSame(@NonNull PruebaConCompeticion a,
                                                  @NonNull PruebaConCompeticion b) {
                    return a.nombrePrueba.equals(b.nombrePrueba)
                            && a.nombreCompeticion.equals(b.nombreCompeticion);
                }
            };

    /**
     * Constructor del adaptador.
     *
     * @param listener Listener que gestionará el clic sobre una prueba.
     */
    public PruebaJuezAdapter(OnPruebaClickListener listener) {
        super(DIFF);
        this.listener = listener;
    }

    /**
     * Se ejecuta cuando RecyclerView necesita crear una nueva fila.
     * Aquí se infla el layout XML que representa cada elemento de la lista.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prueba_juez, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Asocia los datos de una prueba con las vistas del ViewHolder.
     *
     * @param holder   ViewHolder que contiene las vistas.
     * @param position Posición del elemento dentro de la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PruebaConCompeticion item = getItem(position);
        holder.tvCompeticion.setText(item.nombreCompeticion);
        holder.tvPrueba.setText(item.nombrePrueba);
        holder.tvCatNivel.setText(item.categoria + " · " + item.nivel);
        holder.itemView.setOnClickListener(v -> listener.onPruebaClick(item));
    }

    /**
     * ViewHolder encargado de almacenar las referencias a las vistas
     * de cada elemento para evitar búsquedas repetidas con findViewById.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCompeticion, tvPrueba, tvCatNivel;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCompeticion = itemView.findViewById(R.id.tvNombreCompeticion);
            tvPrueba      = itemView.findViewById(R.id.tvNombrePrueba);
            tvCatNivel    = itemView.findViewById(R.id.tvCategoriaYNivel);
        }
    }
}