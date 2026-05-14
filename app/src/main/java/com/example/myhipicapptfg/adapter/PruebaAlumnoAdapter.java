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
import com.example.myhipicapptfg.model.PruebaAlumno;

public class PruebaAlumnoAdapter
        extends ListAdapter<PruebaAlumno, PruebaAlumnoAdapter.VH> {

    public interface OnClick { void onClick(PruebaAlumno item); }
    private OnClick listener;
    public void setOnClick(OnClick l) { this.listener = l; }

    public PruebaAlumnoAdapter() {
        super(new DiffUtil.ItemCallback<PruebaAlumno>() {
            @Override public boolean areItemsTheSame(@NonNull PruebaAlumno a,
                                                     @NonNull PruebaAlumno b) {
                return a.idPrueba == b.idPrueba;
            }
            @Override public boolean areContentsTheSame(@NonNull PruebaAlumno a,
                                                        @NonNull PruebaAlumno b) {
                return a.idPrueba == b.idPrueba;
            }
        });
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prueba_alumno, parent, false);
        return new VH(v);
    }

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

    @Override public int getItemCount() { return getCurrentList().size(); }

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