package com.example.myhipicapptfg.ui.competiciones.participantes.calificacion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.model.MovimientoConNota;

import java.util.Locale;


/**
 * Adaptador encargado de mostrar la hoja de calificaciones
 * de una participación.
 *
 * Cada elemento representa un movimiento de la reprise junto
 * con su nota, coeficiente, directriz y observaciones.
 *
 * Utiliza ListAdapter y DiffUtil para optimizar las
 * actualizaciones del RecyclerView.
 */
public class HojaCalifAdapter
        extends ListAdapter<MovimientoConNota, HojaCalifAdapter.VH> {

    /**
     * Constructor del adaptador.
     * Configura DiffUtil para detectar cambios entre movimientos.
     */
    public HojaCalifAdapter() {
        super(new DiffUtil.ItemCallback<MovimientoConNota>() {
            /**
             * Comprueba si dos elementos representan el mismo movimiento.
             */
            @Override public boolean areItemsTheSame(@NonNull MovimientoConNota a,
                                                     @NonNull MovimientoConNota b) {
                return a.idMovimiento == b.idMovimiento;
            }
            /**
             * Comprueba si el contenido de dos movimientos es idéntico.
             */
            @Override public boolean areContentsTheSame(@NonNull MovimientoConNota a,
                                                        @NonNull MovimientoConNota b) {
                return a.nota == b.nota;
            }
        });
    }

    /**
     * Crea una nueva vista para un elemento de la hoja de calificaciones.
     */
    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hoja_calif, parent, false);
        return new VH(v);
    }

    /**
     * Vincula los datos de un movimiento con su fila correspondiente.
     */
    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        MovimientoConNota m = getItem(pos);
        h.tvOrden.setText(String.valueOf(m.orden));
        h.tvLetra.setText(m.letra);
        h.tvEjercicio.setText(m.ejercicio);
        h.tvCoef.setText("×" + (int) m.coeficiente);
        h.tvNota.setText(String.format(Locale.getDefault(), "%.1f", m.nota));
        h.tvDirectriz.setText(m.directriz != null && !m.directriz.isEmpty()
                ? m.directriz : "—");
        h.tvObservacion.setText(m.observacion != null && !m.observacion.isEmpty()
                ? m.observacion : "—");
    }

    /**
     * ViewHolder que mantiene las referencias a las vistas
     * de cada elemento de la lista.
     */
    static class VH extends RecyclerView.ViewHolder {
        TextView tvOrden, tvLetra, tvEjercicio, tvCoef, tvNota, tvDirectriz, tvObservacion;
        VH(@NonNull View v) {
            super(v);
            tvOrden       = v.findViewById(R.id.tvOrden);
            tvLetra       = v.findViewById(R.id.tvLetra);
            tvEjercicio   = v.findViewById(R.id.tvEjercicio);
            tvCoef        = v.findViewById(R.id.tvCoeficiente);
            tvNota        = v.findViewById(R.id.tvNota);
            tvDirectriz   = v.findViewById(R.id.tvDirectriz);
            tvObservacion = v.findViewById(R.id.tvObservacion);
        }
    }
}