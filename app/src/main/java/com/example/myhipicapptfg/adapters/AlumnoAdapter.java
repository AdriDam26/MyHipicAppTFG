package com.example.myhipicapptfg.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.entities.Usuario;
import java.util.ArrayList;
import java.util.List;

public class AlumnoAdapter extends RecyclerView.Adapter<AlumnoAdapter.AlumnoViewHolder> {

    private List<Usuario> alumnos = new ArrayList<>();
    private final OnAlumnoClickListener listener;

    public interface OnAlumnoClickListener {
        void onEditar(Usuario usuario);
        void onEliminar(Usuario usuario);
    }

    public AlumnoAdapter(OnAlumnoClickListener listener) {
        this.listener = listener;
    }

    public void setAlumnos(List<Usuario> lista) {
        this.alumnos = lista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlumnoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alumno, parent, false);
        return new AlumnoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AlumnoViewHolder holder, int position) {
        Usuario u = alumnos.get(position);
        holder.tvNombre.setText(u.nombre + " " + u.apellido1 + " " + (u.apellido2 != null ? u.apellido2 : ""));
        holder.tvDetalle.setText("DNI: " + u.dni + " | Email: " + u.email);

        holder.btnEditar.setOnClickListener(v -> listener.onEditar(u));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminar(u));
    }

    @Override
    public int getItemCount() { return alumnos.size(); }

    static class AlumnoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDetalle;
        ImageButton btnEditar, btnEliminar;

        public AlumnoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreAlumno);
            tvDetalle = itemView.findViewById(R.id.tvDetalleAlumno);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}