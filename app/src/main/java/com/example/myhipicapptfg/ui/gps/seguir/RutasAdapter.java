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

/**
 * Adapter encargado de mostrar las rutas almacenadas en un RecyclerView.
 *
 * Actúa como intermediario entre la lista de objetos {@link RutaPersonal}
 * y la interfaz gráfica.
 *
 */
public class RutasAdapter extends RecyclerView.Adapter<RutasAdapter.RutaViewHolder> {

    /**
     * Lista de rutas que se mostrarán en el RecyclerView.
     */
    private List<RutaPersonal> rutas = new ArrayList<>();

    /**
     * Listener utilizado para notificar la selección de una ruta.
     */
    private final OnRutaClickListener listener;

    /**
     * Interfaz utilizada para comunicar la ruta seleccionada
     * a la Activity o Fragment que utilice este Adapter.
     */
    public interface OnRutaClickListener {
        void onRutaClick(RutaPersonal ruta);
    }

    /**
     * Constructor del Adapter.
     *
     * @param listener Listener que recibirá los eventos de selección.
     */
    public RutasAdapter(OnRutaClickListener listener) {
        this.listener = listener;
    }


    /**
     * Actualiza la lista de rutas mostradas.
     *
     * Tras reemplazar los datos se notifica al RecyclerView
     * para que vuelva a dibujar sus elementos.
     *
     * @param nuevasRutas Lista actualizada de rutas.
     */
    public void setRutas(List<RutaPersonal> nuevasRutas) {
        this.rutas = nuevasRutas;
        notifyDataSetChanged();
    }

    /**
     * Crea una nueva vista para un elemento de la lista.
     *
     * Este método infla el layout XML correspondiente
     * a cada ruta y crea su ViewHolder asociado.
     *
     * @param parent Vista padre.
     * @param viewType Tipo de vista.
     * @return Nuevo ViewHolder.
     */
    @NonNull
    @Override
    public RutaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ruta, parent, false);
        return new RutaViewHolder(view);
    }

    /**
     * Asocia los datos de una ruta concreta
     * con los componentes visuales del ViewHolder.
     *
     * @param holder ViewHolder que se va a actualizar.
     * @param position Posición del elemento dentro de la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull RutaViewHolder holder, int position) {
        holder.bind(rutas.get(position), listener);
    }

    /**
     * Devuelve el número total de rutas que deben mostrarse.
     *
     * @return Número de elementos de la lista.
     */
    @Override
    public int getItemCount() {
        return rutas != null ? rutas.size() : 0;
    }

    /**
     * ViewHolder encargado de representar una única ruta
     * dentro del RecyclerView.
     *
     * Mantiene referencias a los componentes visuales
     * para mejorar el rendimiento y evitar búsquedas
     * repetidas mediante findViewById().
     */
    static class RutaViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombre;
        private final TextView tvFecha;
        private final TextView tvDistancia;
        private final TextView tvDuracion;

        /**
         * Constructor del ViewHolder.
         *
         * Obtiene las referencias a los componentes
         * definidos en el layout item_ruta.xml.
         *
         * @param itemView Vista del elemento.
         */
        public RutaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreRuta);
            tvFecha = itemView.findViewById(R.id.tvFechaRuta);
            tvDistancia = itemView.findViewById(R.id.tvDistanciaRuta);
            tvDuracion = itemView.findViewById(R.id.tvDuracionRuta);
        }

        /**
         * Asocia los datos de una ruta con la interfaz visual.
         *
         * Además configura el evento de selección
         * para notificar la ruta pulsada.
         *
         * @param ruta Ruta que se mostrará.
         * @param listener Listener encargado de recibir la selección.
         */
        public void bind(final RutaPersonal ruta, final OnRutaClickListener listener) {
            tvNombre.setText(ruta.nombre);
            tvFecha.setText(ruta.fecha);
            tvDistancia.setText(String.format("%.2f km", ruta.distanciaRecorrida));
            tvDuracion.setText(ruta.duracion);

            itemView.setOnClickListener(v -> listener.onRutaClick(ruta));
        }
    }
}