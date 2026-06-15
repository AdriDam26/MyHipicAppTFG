package com.example.myhipicapptfg.ui.competiciones.juez.participantes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.model.ParticipacionDetalle;


/**
 * Adaptador para mostrar la lista de participantes de una prueba.
 *
 * Hereda de ListAdapter para aprovechar las ventajas de DiffUtil,
 * permitiendo actualizar únicamente los elementos que cambian
 * y mejorando el rendimiento del RecyclerView.
 */
public class ParticipanteAdapter
        extends ListAdapter<ParticipacionDetalle, ParticipanteAdapter.ViewHolder> {

    /**
     * Interfaz utilizada para notificar cuando se selecciona
     * un participante de la lista.
     */
    public interface OnParticipanteClickListener {
        void onPuntuar(ParticipacionDetalle item);
    }

    // Listener que gestionará los clics sobre los participante
    private OnParticipanteClickListener listener;


    /**
     * Callback de DiffUtil utilizado para comparar elementos
     * antiguos y nuevos de la lista.
     *
     * Permite actualizar únicamente los elementos modificados.
     */
    private static final DiffUtil.ItemCallback<ParticipacionDetalle> DIFF =
            new DiffUtil.ItemCallback<ParticipacionDetalle>() {
                @Override
                public boolean areItemsTheSame(@NonNull ParticipacionDetalle a,
                                               @NonNull ParticipacionDetalle b) {
                    return a.idParticipacion == b.idParticipacion;
                }
                @Override
                public boolean areContentsTheSame(@NonNull ParticipacionDetalle a,
                                                  @NonNull ParticipacionDetalle b) {
                    return a.ordenSalida   == b.ordenSalida
                            && a.nombreJinete.equals(b.nombreJinete)
                            && a.nombreCaballo.equals(b.nombreCaballo);
                }
            };

    /**
     * Constructor del adaptador.
     *
     * Inicializa ListAdapter utilizando el callback DIFF.
     */
    public ParticipanteAdapter() {
        super(DIFF);
    }


    /**
     * Establece el listener que gestionará los clics
     * sobre los participantes.
     *
     * @param l Listener de selección.
     */
    public void setOnParticipanteClickListener(OnParticipanteClickListener l) {
        this.listener = l;
    }

    /**
     * Crea una nueva fila del RecyclerView.
     *
     * Se infla el layout XML que representa cada participante.
     *
     * @param parent Vista contenedora.
     * @param viewType Tipo de vista.
     * @return Nuevo ViewHolder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_participante, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Vincula los datos de un participante con las vistas
     * correspondientes del ViewHolder.
     *
     * @param holder ViewHolder que contiene las vistas.
     * @param position Posición del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParticipacionDetalle item = getItem(position);
        holder.tvOrden.setText(String.valueOf(item.ordenSalida));
        holder.tvJinete.setText(item.nombreJinete);
        holder.tvCaballo.setText("🐴 " + item.nombreCaballo);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPuntuar(item);
        });
    }

    /**
     * Clase ViewHolder.
     *
     * Mantiene referencias a las vistas de cada fila para evitar
     * llamadas repetidas a findViewById y mejorar el rendimiento.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrden, tvJinete, tvCaballo;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrden   = itemView.findViewById(R.id.tvOrdenSalida);
            tvJinete  = itemView.findViewById(R.id.tvNombreJinete);
            tvCaballo = itemView.findViewById(R.id.tvNombreCaballo);
        }
    }
}