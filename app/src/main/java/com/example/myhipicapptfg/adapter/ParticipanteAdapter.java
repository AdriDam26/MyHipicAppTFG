// com/example/myhipicapptfg/adapters/ParticipanteAdapter.java
package com.example.myhipicapptfg.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.model.ParticipacionDetalle;

public class ParticipanteAdapter
        extends ListAdapter<ParticipacionDetalle, ParticipanteAdapter.ViewHolder> {


    public interface OnParticipanteClickListener {
        void onPuntuar(ParticipacionDetalle item);
    }


    private OnParticipanteClickListener listener;



    private static final DiffUtil.ItemCallback<ParticipacionDetalle> DIFF =
            new DiffUtil.ItemCallback<ParticipacionDetalle>() {
                @Override
                public boolean areItemsTheSame(@NonNull ParticipacionDetalle a,
                                               @NonNull ParticipacionDetalle b) {
                    return a.idParticipacion == b.idParticipacion;
                }
                @Override
                public boolean areContentsTheSame(@NonNull ParticipacionDetalle a,
                                                  @NonNull ParticipacionDetalle b) {
                    return a.ordenSalida   == b.ordenSalida
                            && a.nombreJinete.equals(b.nombreJinete)
                            && a.nombreCaballo.equals(b.nombreCaballo);
                }
            };

    public ParticipanteAdapter() {
        super(DIFF);
    }



    public void setOnParticipanteClickListener(OnParticipanteClickListener l) {
        this.listener = l;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_participante, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParticipacionDetalle item = getItem(position);
        holder.tvOrden.setText(String.valueOf(item.ordenSalida));
        holder.tvJinete.setText(item.nombreJinete);
        holder.tvCaballo.setText("🐴 " + item.nombreCaballo);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPuntuar(item);
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrden, tvJinete, tvCaballo;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrden   = itemView.findViewById(R.id.tvOrdenSalida);
            tvJinete  = itemView.findViewById(R.id.tvNombreJinete);
            tvCaballo = itemView.findViewById(R.id.tvNombreCaballo);
        }
    }
}