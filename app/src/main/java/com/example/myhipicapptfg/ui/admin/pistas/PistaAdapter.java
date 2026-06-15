package com.example.myhipicapptfg.ui.admin.pistas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Pista;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter encargado de mostrar la lista de Pistas en un RecyclerView.
 *
 * Su función es adaptar los datos de la entidad Pista
 * a la vista item_pista.xml.
 *
 * Permite también gestionar acciones de edición y eliminación
 * mediante un listener externo.
 */
public class PistaAdapter extends RecyclerView.Adapter<PistaAdapter.PistaViewHolder> {

    private List<Pista> lista;

    // Listener para acciones de usuario (editar / eliminar)
    private final OnClick listener;

    /**
     * Interfaz para manejar eventos desde el Activity/Fragment
     */
    public interface OnClick {
        void editar(Pista p);
        void eliminar(Pista p);
    }

    public PistaAdapter(List<Pista> lista, OnClick listener) {
        this.lista = lista != null ? lista : new ArrayList<>();
        this.listener = listener;
    }

    /**
     * Actualiza la lista de pistas y refresca la vista
     */
    public void actualizar(List<Pista> nuevaLista) {
        this.lista = nuevaLista != null ? nuevaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PistaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pista, parent, false);
        return new PistaViewHolder(v);
    }

    // Asignar datos a la vista
    @Override
    public void onBindViewHolder(@NonNull PistaViewHolder holder, int position) {

        Pista pista = lista.get(position);

        holder.txtNombre.setText(pista.nombre);

        holder.txtDimensiones.setText(
                "Ancho: " + pista.ancho + " m  |  Largo: " + pista.largo + " m"
        );

        double area = pista.ancho * pista.largo;

        holder.txtArea.setText(area + " m²");

        holder.btnEditar.setOnClickListener(v -> listener.editar(pista));
        holder.btnEliminar.setOnClickListener(v -> listener.eliminar(pista));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    /**
     * ViewHolder:
     * Mantiene referencias a las vistas del item_pista.xml
     * para mejorar rendimiento evitando múltiples findViewById.
     */
    static class PistaViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtDimensiones, txtArea;
        ImageButton btnEditar, btnEliminar;

        public PistaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombrePista);
            txtDimensiones = itemView.findViewById(R.id.txtDimensionesPista);
            txtArea = itemView.findViewById(R.id.txtAreaPista);
            // Cambia estos IDs para que coincidan con el XML
            btnEditar = itemView.findViewById(R.id.btnEditarPista);
            btnEliminar = itemView.findViewById(R.id.btnEliminarPista);
        }
    }
}