package com.example.myhipicapptfg.ui.competiciones.participantes;

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

public class RankingAdapter extends ListAdapter<RankingItem, RankingAdapter.VH> {

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

    // La posición la calcula la query, no el adapter
    // submitList heredado de ListAdapter es suficiente, no lo sobreescribimos

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ranking, parent, false);
        return new VH(v);
    }

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
            h.tvPosicion.setTextColor(Color.parseColor("#6A1B9A"));
            h.tvPorcentaje.setText(String.format(Locale.getDefault(),
                    "%.3f%%", item.porcentaje));
            h.tvPorcentaje.setTextColor(Color.parseColor("#212121"));
        }

        h.tvJinete.setText(item.nombreJinete);
        h.tvCaballo.setText("🐴 " + item.nombreCaballo);
        h.tvNota.setText(String.format(Locale.getDefault(), "%.2f", item.notaFinal));
    }

    @Override public int getItemCount() { return getCurrentList().size(); }

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