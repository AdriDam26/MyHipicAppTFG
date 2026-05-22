// com/example/myhipicapptfg/adapters/PruebaJuezAdapter.java
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

public class PruebaJuezAdapter
        extends ListAdapter<PruebaConCompeticion, PruebaJuezAdapter.ViewHolder> {

    public interface OnPruebaClickListener {
        void onPruebaClick(PruebaConCompeticion prueba);
    }

    private final OnPruebaClickListener listener;

    // DiffUtil para actualizar sólo los ítems que cambian
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

    public PruebaJuezAdapter(OnPruebaClickListener listener) {
        super(DIFF);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prueba_juez, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PruebaConCompeticion item = getItem(position);
        holder.tvCompeticion.setText(item.nombreCompeticion);
        holder.tvPrueba.setText(item.nombrePrueba);
        holder.tvCatNivel.setText(item.categoria + " · " + item.nivel);
        holder.itemView.setOnClickListener(v -> listener.onPruebaClick(item));
    }

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