package com.example.myhipicapptfg.ui.gps.seguir;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.RutaPersonal;
import java.util.ArrayList;
import java.util.List;

public class RutasAdapter extends RecyclerView.Adapter<RutasAdapter.RutaViewHolder> {

    private List<RutaPersonal> rutas = new ArrayList<>();
    private final OnRutaClickListener listener;

    public interface OnRutaClickListener {
        void onRutaClick(RutaPersonal ruta);
    }

    public RutasAdapter(OnRutaClickListener listener) {
        this.listener = listener;
    }

    public void setRutas(List<RutaPersonal> nuevasRutas) {
        this.rutas = nuevasRutas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RutaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ruta, parent, false);
        return new RutaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RutaViewHolder holder, int position) {
        holder.bind(rutas.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return rutas != null ? rutas.size() : 0;
    }

    static class RutaViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombre;
        private final TextView tvFecha;
        private final TextView tvDistancia;
        private final TextView tvDuracion;

        public RutaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreRuta);
            tvFecha = itemView.findViewById(R.id.tvFechaRuta);
            tvDistancia = itemView.findViewById(R.id.tvDistanciaRuta);
            tvDuracion = itemView.findViewById(R.id.tvDuracionRuta);
        }

        public void bind(final RutaPersonal ruta, final OnRutaClickListener listener) {
            tvNombre.setText(ruta.nombre);
            tvFecha.setText(ruta.fecha);
            tvDistancia.setText(String.format("%.2f km", ruta.distanciaRecorrida));
            tvDuracion.setText(ruta.duracion);

            itemView.setOnClickListener(v -> listener.onRutaClick(ruta));
        }
    }
}