package com.example.myhipicapptfg.ui.competiciones.participantes.ranking;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.model.RankingItem;

import java.util.Locale;


/**
 * Adaptador encargado de mostrar el ranking de una prueba.
 *
 * Utiliza ListAdapter junto con DiffUtil para actualizar únicamente
 * los elementos que cambian, mejorando el rendimiento del RecyclerView.
 *
 * Cada elemento muestra:
 * - Posición en la clasificación.
 * - Nombre del jinete.
 * - Nombre del caballo.
 * - Porcentaje obtenido.
 * - Nota final.
 */
public class RankingAdapter extends ListAdapter<RankingItem, RankingAdapter.VH> {

    /**
     * Constructor del adaptador.
     * Configura DiffUtil para detectar cambios entre elementos del ranking.
     */
    public RankingAdapter() {
        super(new DiffUtil.ItemCallback<RankingItem>() {
            @Override public boolean areItemsTheSame(@NonNull RankingItem a,
                                                     @NonNull RankingItem b) {
                return a.idParticipacion == b.idParticipacion;
            }
            @Override public boolean areContentsTheSame(@NonNull RankingItem a,
                                                        @NonNull RankingItem b) {
                return a.porcentaje == b.porcentaje
                        && a.eliminado == b.eliminado
                        && a.posicion  == b.posicion;
            }
        });
    }

    /**
     * Crea una nueva vista para un elemento del ranking.
     */
    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ranking, parent, false);
        return new VH(v);
    }

    /**
     * Vincula los datos de un participante con su fila correspondiente.
     */
    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        RankingItem item = getItem(pos);

        if (item.eliminado) {
            h.tvPosicion.setText("—");
            h.tvPosicion.setTextColor(Color.parseColor("#B71C1C"));
            h.tvPorcentaje.setText("ELIM.");
            h.tvPorcentaje.setTextColor(Color.parseColor("#B71C1C"));
        } else {
            h.tvPosicion.setText(item.posicion + "º");  // viene de la query
            h.tvPosicion.setTextColor(Color.parseColor("#8B1A2E"));
            h.tvPorcentaje.setText(String.format(Locale.getDefault(),
                    "%.3f%%", item.porcentaje));
            h.tvPorcentaje.setTextColor(Color.parseColor("#212121"));
        }

        h.tvJinete.setText(item.nombreJinete);
        h.tvCaballo.setText("🐴 " + item.nombreCaballo);
        h.tvNota.setText(String.format(Locale.getDefault(), "%.2f", item.notaFinal));
    }

    /**
     * Devuelve el número de elementos mostrados en el ranking.
     */
    @Override public int getItemCount() { return getCurrentList().size(); }

    /**
     * ViewHolder que mantiene las referencias a las vistas de cada fila
     * para evitar búsquedas repetidas mediante findViewById.
     */
    static class VH extends RecyclerView.ViewHolder {
        TextView tvPosicion, tvJinete, tvCaballo, tvPorcentaje, tvNota;

        VH(@NonNull View v) {
            super(v);
            tvPosicion   = v.findViewById(R.id.tvPosicion);
            tvJinete     = v.findViewById(R.id.tvJinete);
            tvCaballo    = v.findViewById(R.id.tvCaballo);
            tvPorcentaje = v.findViewById(R.id.tvPorcentaje);
            tvNota       = v.findViewById(R.id.tvNota);
        }
    }
}