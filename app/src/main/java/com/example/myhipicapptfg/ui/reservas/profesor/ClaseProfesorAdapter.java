package com.example.myhipicapptfg.ui.reservas.profesor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Clase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClaseProfesorAdapter extends RecyclerView.Adapter<ClaseProfesorAdapter.ClaseViewHolder> {

    public interface OnClaseClickListener {
        void onClaseClick(Clase clase);
    }

    private final Context context;
    private List<Clase> clases = new ArrayList<>();
    private final OnClaseClickListener listener;

    private static final SimpleDateFormat FMT_FECHA =
            new SimpleDateFormat("EEEE, d MMM yyyy", new Locale("es", "ES"));
    private static final SimpleDateFormat FMT_HORA =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    public ClaseProfesorAdapter(Context context, OnClaseClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setClases(List<Clase> nuevasClases) {
        this.clases = nuevasClases != null ? nuevasClases : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_clase_profesor, parent, false);
        return new ClaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClaseViewHolder holder, int position) {
        Clase clase = clases.get(position);

        // Disciplina y nivel
        holder.tvDisciplina.setText(clase.disciplina);
        holder.tvNivel.setText(clase.nivel);

        // Fecha formateada
        String fechaStr = FMT_FECHA.format(new Date(clase.fecha));
        // Capitalizar primera letra
        if (!fechaStr.isEmpty()) {
            fechaStr = fechaStr.substring(0, 1).toUpperCase() + fechaStr.substring(1);
        }
        holder.tvFecha.setText(fechaStr);

        // Horario
        String horaInicio = FMT_HORA.format(new Date(clase.horaInicio));
        String horaFin    = FMT_HORA.format(new Date(clase.horaFin));
        holder.tvHorario.setText(horaInicio + " – " + horaFin);

        // Click
        holder.itemView.setOnClickListener(v -> listener.onClaseClick(clase));
    }

    @Override
    public int getItemCount() {
        return clases.size();
    }

    static class ClaseViewHolder extends RecyclerView.ViewHolder {
        TextView tvDisciplina, tvNivel, tvFecha, tvHorario;

        ClaseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDisciplina = itemView.findViewById(R.id.tv_disciplina);
            tvNivel      = itemView.findViewById(R.id.tv_nivel);
            tvFecha      = itemView.findViewById(R.id.tv_fecha);
            tvHorario    = itemView.findViewById(R.id.tv_horario);
        }
    }
}