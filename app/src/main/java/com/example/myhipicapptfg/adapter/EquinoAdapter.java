package com.example.myhipicapptfg.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.entities.Equino;

import java.util.ArrayList;
import java.util.List;

public class EquinoAdapter extends RecyclerView.Adapter<EquinoAdapter.EquinoViewHolder> {

    private List<Equino> lista;
    private final OnClick listener;

    // Interfaz para manejar los eventos de click desde la Activity
    public interface OnClick {
        void editar(Equino e);
        void eliminar(Equino e);
    }

    public EquinoAdapter(List<Equino> lista, OnClick listener) {
        this.lista = (lista != null) ? lista : new ArrayList<>();
        this.listener = listener;
    }

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
    public void onBindViewHolder(@NonNull EquinoViewHolder holder, int position) {
        Equino equino = lista.get(position);

        holder.txtNombre.setText(equino.nombre);

        // Concatenamos Raza y Microchip como planeamos en el XML
        String infoSecundaria = equino.raza + " | Chip: " + equino.numeroMicrochip;
        holder.txtRazaMicrochip.setText(infoSecundaria);

        // Mostramos el temperamento y ajustamos el color según la constante
        holder.txtTemperamento.setText(equino.temperamento.toUpperCase());
        configurarColorTemperamento(holder.txtTemperamento, equino.temperamento);

        // Configuración de botones
        holder.btnEditar.setOnClickListener(v -> listener.editar(equino));
        holder.btnEliminar.setOnClickListener(v -> listener.eliminar(equino));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    /**
     * Cambia el color de fondo del badge de temperamento para dar feedback visual rápido.
     */
    private void configurarColorTemperamento(TextView view, String temperamento) {
        switch (temperamento) {
            case Equino.FACIL:
                view.setBackgroundColor(Color.parseColor("#10B981")); // Verde
                break;
            case Equino.DIFICIL:
                view.setBackgroundColor(Color.parseColor("#EF4444")); // Rojo
                break;
            case Equino.MANEJABLE:
            default:
                view.setBackgroundColor(Color.parseColor("#D97706")); // Naranja/Ocre
                break;
        }
    }

    // ViewHolder que contiene las referencias a las vistas del item_equino.xml
    static class EquinoViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre, txtRazaMicrochip, txtTemperamento;
        ImageButton btnEditar, btnEliminar;

        public EquinoViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNombre = itemView.findViewById(R.id.txtNombreEquino);
            txtRazaMicrochip = itemView.findViewById(R.id.txtRazaMicrochip);
            txtTemperamento = itemView.findViewById(R.id.txtTemperamentoEquino);

            btnEditar = itemView.findViewById(R.id.btnEditarEquino);
            btnEliminar = itemView.findViewById(R.id.btnEliminarEquino);
        }
    }
}