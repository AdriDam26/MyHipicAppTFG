package com.example.myhipicapptfg.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.entities.Pista;

import java.util.ArrayList;
import java.util.List;

public class PistaAdapter extends RecyclerView.Adapter<PistaAdapter.PistaViewHolder> {

    private List<Pista> lista;
    private final OnClick listener;

    public interface OnClick {
        void editar(Pista p);
        void eliminar(Pista p);
    }

    public PistaAdapter(List<Pista> lista, OnClick listener) {
        this.lista = lista != null ? lista : new ArrayList<>();
        this.listener = listener;
    }

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

    @Override
    public void onBindViewHolder(@NonNull PistaViewHolder holder, int position) {

        Pista pista = lista.get(position);

        holder.txtNombre.setText(pista.nombre);

        holder.txtDimensiones.setText(
                "Ancho: " + pista.ancho + " m  |  Largo: " + pista.largo + " m"
        );

        holder.btnEditar.setOnClickListener(v -> listener.editar(pista));
        holder.btnEliminar.setOnClickListener(v -> listener.eliminar(pista));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class PistaViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtDimensiones;
        ImageButton btnEditar, btnEliminar;

        public PistaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombrePista);
            txtDimensiones = itemView.findViewById(R.id.txtDimensionesPista);
            // Cambia estos IDs para que coincidan con el XML
            btnEditar = itemView.findViewById(R.id.btnEditarPista);
            btnEliminar = itemView.findViewById(R.id.btnEliminarPista);
        }
    }
}