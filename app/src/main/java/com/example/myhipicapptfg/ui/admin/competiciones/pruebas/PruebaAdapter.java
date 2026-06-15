package com.example.myhipicapptfg.ui.admin.competiciones.pruebas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Prueba;
import com.example.myhipicapptfg.model.ConteoParticipantes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter encargado de mostrar la lista de Pruebas en un RecyclerView.
 *
 * Responsabilidades:
 * - Mostrar datos básicos de la prueba
 * - Mostrar nombre del juez asociado
 * - Mostrar avisos de participantes
 * - Gestionar acciones (abrir, editar, eliminar)
 */
public class PruebaAdapter extends RecyclerView.Adapter<PruebaAdapter.VH> {

    /**
     * Interfaz de eventos de la UI
     */
    public interface OnClick {
        void editar(Prueba p);
        void eliminar(Prueba p);
        void abrir(Prueba p);
    }

    // Lista principal de pruebas
    private List<Prueba> lista = new ArrayList<>();

    // Mapa idJuez → nombre completo para mostrar en el item
    private Map<Integer, String> nombresJueces = new HashMap<>();

    // Mapa: idPrueba -> número de participantes
    private Map<Integer, Integer> conteosParticipantes = new HashMap<>();

    // Listener de eventos
    private final OnClick listener;

    public PruebaAdapter(OnClick listener) {
        this.listener = listener;
    }

    /**
     * Actualiza la lista de pruebas
     */
    public void actualizar(List<Prueba> nuevaLista) {
        this.lista = nuevaLista != null ? nuevaLista : new ArrayList<>();
        notifyDataSetChanged();
    }


    /**
     * Actualiza el mapa de jueces (id → nombre)
     */
    public void actualizarJueces(Map<Integer, String> mapa) {
        this.nombresJueces = mapa;
        notifyDataSetChanged();
    }

    /**
     * Actualiza el conteo de participantes por prueba
     */
    public void actualizarConteos(List<ConteoParticipantes> lista) {
        conteosParticipantes.clear();
        if (lista != null) {
            for (ConteoParticipantes c : lista) {
                conteosParticipantes.put(c.idPrueba, c.total);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prueba, parent, false);
        return new VH(v);
    }

    /**
     * Vincula los datos de una prueba con su vista
     */
    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Prueba p = lista.get(position);

        h.txtNombre.setText(p.nombre);
        h.txtCategoria.setText(p.categoria);
        h.txtNivel.setText(p.nivel);

        // Mostrar nombre del juez
        if (p.idJuez != null && nombresJueces.containsKey(p.idJuez)) {
            h.txtJuez.setText(nombresJueces.get(p.idJuez));
            h.txtJuez.setVisibility(View.VISIBLE);
        } else {
            h.txtJuez.setText("Sin juez");
            h.txtJuez.setVisibility(View.VISIBLE);
        }

        LinearLayout layoutAviso = h.itemView.findViewById(R.id.layoutAvisoParticipantes);
        TextView tvAviso         = h.itemView.findViewById(R.id.tvAvisoParticipantes);

        // Aviso de participantes
        int total = conteosParticipantes.getOrDefault(p.idPrueba, 0);

        if (total < Prueba.MIN_PARTICIPANTES) {
            layoutAviso.setVisibility(View.VISIBLE);
            tvAviso.setText(total == 0
                    ? "Sin participantes — se requieren mínimo 3"
                    : "Mínimo 3 participantes requeridos (" + total + "/3)");
        } else {
            layoutAviso.setVisibility(View.GONE);
        }

        h.btnEditar.setOnClickListener(v -> listener.editar(p));
        h.btnEliminar.setOnClickListener(v -> listener.eliminar(p));
        h.itemView.setOnClickListener(v -> listener.abrir(p));
    }

    @Override
    public int getItemCount() { return lista.size(); }

    /**
     * ViewHolder: cachea referencias de vistas del item_prueba
     * para optimizar el rendimiento del RecyclerView.
     */
    static class VH extends RecyclerView.ViewHolder {
        TextView    txtNombre, txtCategoria, txtNivel, txtJuez;
        ImageButton btnEditar, btnEliminar;

        VH(@NonNull View v) {
            super(v);
            txtNombre    = v.findViewById(R.id.txtNombrePrueba);
            txtCategoria = v.findViewById(R.id.txtCategoriaPrueba);
            txtNivel     = v.findViewById(R.id.txtNivelPrueba);
            txtJuez      = v.findViewById(R.id.txtJuezPrueba);
            btnEditar    = v.findViewById(R.id.btnEditar);
            btnEliminar  = v.findViewById(R.id.btnEliminar);
        }
    }


}