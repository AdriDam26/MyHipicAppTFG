package com.example.myhipicapptfg.ui.admin.equinos;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Equino;

import java.util.ArrayList;
import java.util.List;

public class EquinoAdapter extends RecyclerView.Adapter<EquinoAdapter.EquinoViewHolder> {

    private List<Equino> lista;
    private final OnClick listener;

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

        Equino e = lista.get(position);

        holder.txtNombre.setText(e.nombre);
        holder.txtRaza.setText(e.raza);
        holder.txtMicrochip.setText("Chip: " + e.numeroMicrochip);

        // 🧠 Disciplina
        holder.txtDisciplina.setText(getDisciplina(e));

        // 🎯 Temperamento
        holder.txtTemperamento.setText(e.temperamento != null ? e.temperamento.toUpperCase() : "");

        configurarColorTemperamento(holder.txtTemperamento, e.temperamento);

        holder.btnEditar.setOnClickListener(v -> listener.editar(e));
        holder.btnEliminar.setOnClickListener(v -> listener.eliminar(e));
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    // 🔥 Disciplina calculada
    private String getDisciplina(Equino e) {
        if (e.sabeDoma && e.sabeSalto) return "DOMA + SALTO";
        if (e.sabeDoma) return "DOMA";
        if (e.sabeSalto) return "SALTO";
        return "SIN DISCIPLINA";
    }

    // 🎨 Color del temperamento
    private void configurarColorTemperamento(TextView view, String t) {

        if (t == null) return;

        switch (t) {

            case Equino.FACIL:
                view.setBackgroundTintList(
                        ColorStateList.valueOf(0xFF10B981)); // verde
                break;

            case Equino.DIFICIL:
                view.setBackgroundTintList(
                        ColorStateList.valueOf(0xFFEF4444)); // rojo
                break;

            case Equino.MANEJABLE:
            default:
                view.setBackgroundTintList(
                        ColorStateList.valueOf(0xFFD97706)); // naranja
                break;
        }
    }

    static class EquinoViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre, txtRaza, txtMicrochip, txtDisciplina, txtTemperamento;
        ImageButton btnEditar, btnEliminar;

        public EquinoViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNombre = itemView.findViewById(R.id.txtNombreEquino);
            txtRaza = itemView.findViewById(R.id.txtRazaEquino);
            txtMicrochip = itemView.findViewById(R.id.txtMicrochipEquino);
            txtDisciplina = itemView.findViewById(R.id.txtDisciplinaEquino);
            txtTemperamento = itemView.findViewById(R.id.txtTemperamentoEquino);

            btnEditar = itemView.findViewById(R.id.btnEditarEquino);
            btnEliminar = itemView.findViewById(R.id.btnEliminarEquino);
        }
    }
}