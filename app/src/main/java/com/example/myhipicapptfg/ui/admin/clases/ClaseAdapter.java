package com.example.myhipicapptfg.ui.admin.clases;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Clase;
import com.example.myhipicapptfg.datos.local.entidades.Pista;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClaseAdapter extends RecyclerView.Adapter<ClaseAdapter.ClaseViewHolder> {

    private List<Clase> listaClases;
    private List<Usuario> listaProfesores; // Nueva lista para buscar nombres
    private List<Pista> listaPistas;       // Nueva lista para buscar nombres
    private final OnClaseClickListener listener;

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public interface OnClaseClickListener {
        void editar(Clase clase);
        void eliminar(Clase clase);
    }

    // Constructor actualizado para recibir las listas de referencia
    public ClaseAdapter(List<Clase> listaClases, List<Usuario> listaProfesores, List<Pista> listaPistas, OnClaseClickListener listener) {
        this.listaClases = listaClases;
        this.listaProfesores = listaProfesores;
        this.listaPistas = listaPistas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clase, parent, false);
        return new ClaseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ClaseViewHolder holder, int position) {
        if (listaClases != null && position < listaClases.size()) {
            holder.bind(listaClases.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return listaClases != null ? listaClases.size() : 0;
    }

    // Método para actualizar todas las listas desde el Activity/Fragment
    public void actualizarTodo(List<Clase> nuevasClases, List<Usuario> nuevosProfesores, List<Pista> nuevasPistas) {
        this.listaClases = nuevasClases;
        this.listaProfesores = nuevosProfesores;
        this.listaPistas = nuevasPistas;
        notifyDataSetChanged();
    }

    class ClaseViewHolder extends RecyclerView.ViewHolder {
        TextView txtRangoHorario, txtDisciplina, txtInfoProfesorPista, txtNivel, txtFecha;
        ImageButton btnEditar, btnEliminar;
        View viewIndicator;

        public ClaseViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRangoHorario = itemView.findViewById(R.id.txtRangoHorarioClase);
            txtDisciplina = itemView.findViewById(R.id.txtDisciplinaClase);
            txtInfoProfesorPista = itemView.findViewById(R.id.txtInfoProfesorPista);
            txtNivel = itemView.findViewById(R.id.txtNivelClase);
            txtFecha = itemView.findViewById(R.id.txtFechaClase);
            btnEditar = itemView.findViewById(R.id.btnEditarClase);
            btnEliminar = itemView.findViewById(R.id.btnEliminarClase);
            viewIndicator = itemView.findViewById(R.id.viewIndicator);
        }

        public void bind(Clase clase) {
            try {
                // 1. Horas
                String inicio = (clase.horaInicio > 0) ? timeFormat.format(new Date(clase.horaInicio)) : "--:--";
                String fin = (clase.horaFin > 0) ? timeFormat.format(new Date(clase.horaFin)) : "--:--";
                txtRangoHorario.setText(inicio + " - " + fin);

                // 2. Disciplina y Color
                String disc = (clase.disciplina != null) ? clase.disciplina.toUpperCase() : "SIN DISCIPLINA";
                txtDisciplina.setText(disc);
                if (clase.disciplina != null && clase.disciplina.equalsIgnoreCase("Doma")) {
                    viewIndicator.setBackgroundColor(Color.parseColor("#4338CA"));
                } else {
                    viewIndicator.setBackgroundColor(Color.parseColor("#F59E0B"));
                }

                // 3. Nivel
                txtNivel.setText((clase.nivel != null) ? clase.nivel.toUpperCase() : "S.N.");

                // 4. Fecha
                if (clase.fecha > 0) {
                    txtFecha.setText(dateFormat.format(new Date(clase.fecha)));
                } else {
                    txtFecha.setText("Sin fecha");
                }

                // 5. BUSCAR NOMBRES (Profesor y Pista)
                String nombreProfesor = "Prof. Desconocido";
                if (listaProfesores != null) {
                    for (Usuario u : listaProfesores) {
                        if (u.idUsuario == clase.idProfesor) {
                            nombreProfesor = u.nombre;
                            break;
                        }
                    }
                }

                String nombrePista = "Pista " + clase.idPista;
                if (listaPistas != null) {
                    for (Pista p : listaPistas) {
                        if (p.idPista == clase.idPista) {
                            nombrePista = p.nombre;
                            break;
                        }
                    }
                }

                txtInfoProfesorPista.setText(nombreProfesor + " • " + nombrePista);

                // Listeners
                btnEditar.setOnClickListener(v -> listener.editar(clase));
                btnEliminar.setOnClickListener(v -> listener.eliminar(clase));

            } catch (Exception e) {
                txtDisciplina.setText("Error en datos");
            }
        }
    }
}