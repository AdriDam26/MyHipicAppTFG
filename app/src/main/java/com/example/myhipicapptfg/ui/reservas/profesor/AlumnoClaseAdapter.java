package com.example.myhipicapptfg.ui.reservas.profesor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.google.android.material.imageview.ShapeableImageView;

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

        holder.tvNombreCompleto.setText(alumno.nombre + " " + alumno.apellido1);
        holder.tvTelefono.setText(alumno.telefono != null ? alumno.telefono : "Sin teléfono");

        ShapeableImageView ivFoto = holder.itemView.findViewById(R.id.iv_foto_alumno);

        if (alumno.fotoPerfil != null && !alumno.fotoPerfil.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(alumno.fotoPerfil)
                    .placeholder(R.drawable.ic_person_placeholder)
                    .error(R.drawable.ic_person_placeholder)
                    .circleCrop()
                    .into(ivFoto);
        } else {
            ivFoto.setImageResource(R.drawable.ic_person_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return alumnos.size();
    }

    static class AlumnoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreCompleto, tvTelefono;

        AlumnoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreCompleto = itemView.findViewById(R.id.tv_nombre_completo);
            tvTelefono       = itemView.findViewById(R.id.tv_telefono);
        }
    }
}