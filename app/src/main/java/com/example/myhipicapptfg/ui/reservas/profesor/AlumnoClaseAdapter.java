package com.example.myhipicapptfg.ui.reservas.profesor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;

import java.util.ArrayList;
import java.util.List;

public class AlumnoClaseAdapter extends RecyclerView.Adapter<AlumnoClaseAdapter.AlumnoViewHolder> {

    private final Context context;
    private List<Usuario> alumnos = new ArrayList<>();

    public AlumnoClaseAdapter(Context context) {
        this.context = context;
    }

    public void setAlumnos(List<Usuario> nuevosAlumnos) {
        this.alumnos = nuevosAlumnos != null ? nuevosAlumnos : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlumnoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_alumno_clase, parent, false);
        return new AlumnoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlumnoViewHolder holder, int position) {
        Usuario alumno = alumnos.get(position);

        // Nombre y apellidos completos
        String nombreCompleto = alumno.nombre + " " + alumno.apellido1;
        holder.tvNombreCompleto.setText(nombreCompleto);

        // Teléfono (si tu entidad Usuario tiene campo telefono)
        holder.tvTelefono.setText(alumno.telefono != null ? alumno.telefono : "Sin teléfono");

        // Avatar con iniciales
        String iniciales = "";
        if (alumno.nombre != null && !alumno.nombre.isEmpty()) {
            iniciales += alumno.nombre.charAt(0);
        }
        if (alumno.apellido1 != null && !alumno.apellido1.isEmpty()) {
            iniciales += alumno.apellido1.charAt(0);
        }
        holder.tvIniciales.setText(iniciales.toUpperCase());
    }

    @Override
    public int getItemCount() {
        return alumnos.size();
    }

    static class AlumnoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreCompleto, tvTelefono, tvIniciales;

        AlumnoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreCompleto = itemView.findViewById(R.id.tv_nombre_completo);
            tvTelefono       = itemView.findViewById(R.id.tv_telefono);
            tvIniciales      = itemView.findViewById(R.id.tv_iniciales);
        }
    }
}