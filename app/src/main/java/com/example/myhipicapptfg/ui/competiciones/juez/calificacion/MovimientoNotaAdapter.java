package com.example.myhipicapptfg.ui.competiciones.juez.calificacion;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.model.MovimientoConNota;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador encargado de mostrar los movimientos de una prueba
 * junto con los campos de nota y observación que debe completar el juez.
 *
 * Cada elemento de la lista representa un movimiento de la reprise
 * y permite introducir:
 * - La nota obtenida.
 * - Una observación asociada al movimiento.
 *
 * Además, informa a la Activity cuando cambia una nota para que
 * pueda recalcular automáticamente la puntuación total.
 */
public class MovimientoNotaAdapter
        extends RecyclerView.Adapter<MovimientoNotaAdapter.VH> {

    /**
     * Interfaz utilizada para notificar a la Activity
     * cuando se modifica alguna nota.
     */
    public interface OnNotaCambiadaListener {
        void onNotaCambiada();
    }

    /**
     * Listener registrado por la Activity para recibir
     * avisos cuando cambian las notas.
     */
    private OnNotaCambiadaListener notaListener;

    /**
     * Registra el listener que será avisado cuando
     * una nota sea modificada.
     *
     * @param l Listener de cambios de nota.
     */
    public void setOnNotaCambiadaListener(OnNotaCambiadaListener l) {
        this.notaListener = l;
    }

    /**
     * Lista de movimientos con sus notas y observaciones.
     */
    private List<MovimientoConNota> items = new ArrayList<>();

    /**
     * Actualiza los datos mostrados por el adaptador.
     *
     * @param list Nueva lista de movimientos.
     */
    public void setItems(List<MovimientoConNota> list) {
        items = list;
        notifyDataSetChanged();
    }

    /**
     * Devuelve la lista actual de movimientos.
     *
     * @return Lista de movimientos con nota.
     */
    public List<MovimientoConNota> getItems() {
        return items;
    }

    /**
     * Crea una nueva vista para un elemento del RecyclerView.
     *
     * @param parent   Contenedor padre.
     * @param viewType Tipo de vista.
     * @return Nuevo ViewHolder.
     */
    @NonNull
    @Override
    public VH onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_movimiento_nota,
                        parent,
                        false
                );

        return new VH(v);
    }

    /**
     * Asocia los datos de un movimiento con su vista correspondiente.
     *
     * @param h        ViewHolder.
     * @param position Posición del elemento.
     */
    @Override
    public void onBindViewHolder(
            @NonNull VH h,
            int position
    ) {

        MovimientoConNota m = items.get(position);

        // Información del movimiento
        h.tvOrden.setText(String.valueOf(m.orden));
        h.tvLetra.setText(m.letra);
        h.tvEjercicio.setText(m.ejercicio);
        h.tvCoef.setText("×" + m.coeficiente);
        h.tvDirectriz.setText(m.directriz);

        // Guardar posición para recuperar el elemento correcto
        h.etNota.setTag(position);
        h.etObs.setTag(position);

        // Evita que los TextWatcher se disparen
        // al reutilizar vistas del RecyclerView.
        h.etNota.removeTextChangedListener(h.notaWatcher);
        h.etObs.removeTextChangedListener(h.obsWatcher);

        // Restaurar valores previamente introducidos
        h.etNota.setText(
                m.nota > 0
                        ? String.valueOf(m.nota)
                        : ""
        );

        h.etObs.setText(
                m.observacion != null
                        ? m.observacion
                        : ""
        );

        /**
         * TextWatcher encargado de actualizar la nota
         * cuando el usuario modifica el campo correspondiente.
         */
        h.notaWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                int pos = (int) h.etNota.getTag();

                try {

                    double valor =
                            Double.parseDouble(s.toString());

                    items.get(pos).nota = valor;

                } catch (NumberFormatException ignored) {

                    items.get(pos).nota = 0;
                }

                // Avisar a la Activity para recalcular
                // la nota total en tiempo real.
                if (notaListener != null) {
                    notaListener.onNotaCambiada();
                }
            }
        };

        /**
         * TextWatcher encargado de actualizar
         * la observación del movimiento.
         */
        h.obsWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                int pos = (int) h.etObs.getTag();

                items.get(pos).observacion =
                        s.toString();
            }
        };

        // Registrar listeners
        h.etNota.addTextChangedListener(h.notaWatcher);
        h.etObs.addTextChangedListener(h.obsWatcher);
    }

    /**
     * Devuelve el número total de elementos
     * mostrados por el adaptador.
     *
     * @return Número de movimientos.
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * ViewHolder que almacena las referencias
     * a los componentes gráficos de cada movimiento.
     */
    static class VH extends RecyclerView.ViewHolder {

        TextView tvOrden;
        TextView tvLetra;
        TextView tvEjercicio;
        TextView tvCoef;
        TextView tvDirectriz;

        EditText etNota;
        EditText etObs;

        TextWatcher notaWatcher;
        TextWatcher obsWatcher;

        /**
         * Constructor del ViewHolder.
         *
         * @param v Vista asociada al elemento.
         */
        VH(@NonNull View v) {
            super(v);

            tvOrden = v.findViewById(R.id.tvOrden);
            tvLetra = v.findViewById(R.id.tvLetra);
            tvEjercicio = v.findViewById(R.id.tvEjercicio);
            tvCoef = v.findViewById(R.id.tvCoeficiente);
            tvDirectriz = v.findViewById(R.id.tvDirectriz);

            etNota = v.findViewById(R.id.etNota);
            etObs = v.findViewById(R.id.etObservacion);
        }
    }
}