package com.example.myhipicapptfg.ui.admin.equidos;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Equino;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter encargado de mostrar la lista de Equinos en el sistema.
 *
 * Responsabilidades:
 * - Mostrar datos del caballo (nombre, raza, microchip, disciplina)
 * - Mostrar estado de temperamento con colores
 * - Cargar imagen con Glide
 * - Gestionar acciones de edición y eliminación
 */
public class EquinoAdapter extends RecyclerView.Adapter<EquinoAdapter.EquinoViewHolder> {

    private List<Equino> lista;
    private final OnClick listener;

    /**
     * Interfaz de eventos del adapter
     */
    public interface OnClick {
        void editar(Equino e);
        void eliminar(Equino e);
    }

    public EquinoAdapter(List<Equino> lista, OnClick listener) {
        this.lista = (lista != null) ? lista : new ArrayList<>();
        this.listener = listener;
    }

    /**
     * Actualiza la lista de equinos
     */
    public void actualizar(List<Equino> nuevaLista) {
        this.lista = (nuevaLista != null) ? nuevaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EquinoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_equino, parent, false);
        return new EquinoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EquinoViewHolder h, int position) {

        Equino e = lista.get(position);

        h.txtNombre.setText(e.nombre);
        h.txtRaza.setText(e.raza);
        h.txtMicrochip.setText("Chip: " + e.numeroMicrochip);
        h.txtDisciplina.setText(getDisciplina(e));
        h.txtTemperamento.setText(e.temperamento != null ? e.temperamento.toUpperCase() : "");

        configurarColorTemperamento(h.txtTemperamento, e.temperamento);

        Glide.with(h.itemView.getContext())
                .load(e.fotoPerfil) // Carga el String o Uri
                .placeholder(R.drawable.ic_horse_placeholder) // Imagen mientras carga
                .error(R.drawable.ic_horse_placeholder)       // Imagen si la ruta falla o es nula
                .centerCrop()                                 // Ajusta la imagen al círculo/cuadrado
                .into(h.foto);

        h.btnEditar.setOnClickListener(v -> listener.editar(e));
        h.btnEliminar.setOnClickListener(v -> listener.eliminar(e));
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    /**
     * Calcula la disciplina del equino según sus capacidades
     */
    private String getDisciplina(Equino e) {
        if (e.sabeDoma && e.sabeSalto) {
            return "DOMA + SALTO";
        }
        if (e.sabeDoma) {
            return "DOMA";
        }
        if (e.sabeSalto) {
            return "SALTO";
        }
        return "SIN DISCIPLINA";
    }

    /**
     * Asigna color según el temperamento del equino
     */
    private void configurarColorTemperamento(TextView view, String t) {
        if (t == null) {
            return;
        }
        switch (t) {
            case Equino.FACIL:
                view.setBackgroundTintList(ColorStateList.valueOf(0xFF10B981));
                break;
            case Equino.DIFICIL:
                view.setBackgroundTintList(ColorStateList.valueOf(0xFFEF4444));
                break;
            case Equino.MANEJABLE:
            default:
                view.setBackgroundTintList(ColorStateList.valueOf(0xFFD97706));
                break;
        }
    }

    /**
     * ViewHolder:
     * cachea las vistas del item_equino para mejorar rendimiento
     */
    static class EquinoViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre, txtRaza, txtMicrochip, txtDisciplina, txtTemperamento;
        ImageButton btnEditar, btnEliminar;
        ShapeableImageView foto;

        public EquinoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre       = itemView.findViewById(R.id.txtNombreEquino);
            txtRaza         = itemView.findViewById(R.id.txtRazaEquino);
            txtMicrochip    = itemView.findViewById(R.id.txtMicrochipEquino);
            txtDisciplina   = itemView.findViewById(R.id.txtDisciplinaEquino);
            txtTemperamento = itemView.findViewById(R.id.txtTemperamentoEquino);
            btnEditar       = itemView.findViewById(R.id.btnEditarEquino);
            btnEliminar     = itemView.findViewById(R.id.btnEliminarEquino);
            foto            = itemView.findViewById(R.id.imgFotoEquino); // ✅
        }
    }
}