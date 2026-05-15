package com.example.myhipicapptfg.ui.admin.competiciones;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Competicion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CompeticionAdapter extends RecyclerView.Adapter<CompeticionAdapter.VH> {

    public interface OnClick {
        void abrir(Competicion c);
        void editar(Competicion c);
        void eliminar(Competicion c);
    }

    private List<Competicion> lista = new ArrayList<>();
    private final OnClick listener;

    public CompeticionAdapter(OnClick listener) {
        this.listener = listener;
    }

    public void actualizar(List<Competicion> nuevaLista) {
        this.lista = nuevaLista != null ? nuevaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_competicion, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Competicion c = lista.get(position);

        h.txtNombre.setText(c.nombre);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(c.fecha);
        h.txtFecha.setText(cal.get(Calendar.DAY_OF_MONTH) + "/"
                + (cal.get(Calendar.MONTH) + 1) + "/"
                + cal.get(Calendar.YEAR));

        h.itemView.setOnClickListener(v -> listener.abrir(c));
        h.btnEditar.setOnClickListener(v -> listener.editar(c));
        h.btnEliminar.setOnClickListener(v -> listener.eliminar(c));
    }

    @Override
    public int getItemCount() { return lista.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView    txtNombre, txtFecha;
        ImageButton btnEditar, btnEliminar;

        VH(@NonNull View v) {
            super(v);
            txtNombre   = v.findViewById(R.id.txtNombreCompeticion);
            txtFecha    = v.findViewById(R.id.txtFechaCompeticion);
            btnEditar   = v.findViewById(R.id.btnEditar);
            btnEliminar = v.findViewById(R.id.btnEliminar);
        }
    }
}