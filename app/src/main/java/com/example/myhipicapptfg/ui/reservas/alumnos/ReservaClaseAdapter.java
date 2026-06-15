package com.example.myhipicapptfg.ui.reservas.alumnos;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.model.ClaseModel;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;


/**
 * Adapter encargado de mostrar una lista de clases en un RecyclerView.
 *
 * Permite dos modos de funcionamiento:
 * - Modo reserva: muestra clases disponibles para reservar
 * - Modo cancelación: muestra clases ya reservadas que se pueden cancelar
 *
 * Gestiona:
 * - Renderizado de la información de cada clase
 * - Cambio dinámico de UI según el modo
 * - Control de cupos disponibles
 * - Gestión de acciones (reservar / cancelar)
 */
public class ReservaClaseAdapter
        extends ListAdapter<ClaseModel, ReservaClaseAdapter.ViewHolder> {

    /**
     * Listener para manejar el click en el botón de acción.
     */
    private final OnClaseClickListener listener;

    /**
     * Indica si el adapter está en modo cancelación o reserva.
     */
    private final boolean esModoCancelacion;

    /**
     * Formateador de hora (HH:mm).
     */
    private final SimpleDateFormat timeFormat =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    /**
     * Formateador de fecha (dd/MM/yyyy).
     */
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public interface OnClaseClickListener {
        void onAccionClick(ClaseModel clase);
    }

    /**
     * Constructor del adapter.
     *
     * @param esModoCancelacion indica si se muestra modo cancelación o reserva
     * @param listener callback para acciones del botón
     */
    public ReservaClaseAdapter(boolean esModoCancelacion, OnClaseClickListener listener) {
        super(DIFF_CALLBACK);
        this.esModoCancelacion = esModoCancelacion;
        this.listener = listener;
    }

    /**
     * Infla la vista de cada item del RecyclerView.
     */
    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clase_reserva, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Vincula los datos de una clase con la vista correspondiente.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClaseModel m = getItem(position);
        // Información básica de la clase
        holder.tvDisciplina.setText(m.disciplina != null ? m.disciplina : "Clase");
        holder.tvNivel.setText(
                m.nivel != null ? "Nivel: " + m.nivel : "Nivel no especificado"
        );
        holder.tvDia.setText(m.fecha != 0 ? dateFormat.format(m.fecha) : "Sin fecha");
        holder.tvHora.setText(timeFormat.format(m.horaInicio)
                + " - " + timeFormat.format(m.horaFin));

        if (esModoCancelacion) {
            holder.tvProfesor.setVisibility(View.VISIBLE);
            holder.tvPista.setVisibility(View.VISIBLE);
            holder.tvCupo.setVisibility(View.GONE);


            holder.tvProfesor.setText("Prof: " + (m.nombreProfesor != null
                    ? m.nombreProfesor : "No asignado"));
            holder.tvPista.setText("Pista: " + (m.nombrePista != null
                    ? m.nombrePista : "No asignada"));

            holder.btnAccion.setText("Cancelar");
            holder.btnAccion.setBackgroundTintList(
                    ColorStateList.valueOf(Color.parseColor("#E57373")));
            holder.btnAccion.setEnabled(true);
        } else {
            holder.tvProfesor.setVisibility(View.GONE);
            holder.tvPista.setVisibility(View.GONE);
            holder.tvCupo.setVisibility(View.VISIBLE);

            holder.btnAccion.setText("Reservar");

            // Control de cupos
            if (m.inscritos >= ClaseModel.MAXIMO) {
                holder.tvCupo.setTextColor(Color.RED);
                holder.tvCupo.setText("¡LLENO! " + m.inscritos + "/" + ClaseModel.MAXIMO);
                holder.btnAccion.setEnabled(false);
                holder.btnAccion.setBackgroundTintList(
                        ColorStateList.valueOf(Color.LTGRAY));
            } else {
                holder.tvCupo.setTextColor(Color.parseColor("#666666"));
                holder.tvCupo.setText("Inscritos: " + m.inscritos + "/" + ClaseModel.MAXIMO);
                holder.btnAccion.setEnabled(true);
                holder.btnAccion.setBackgroundTintList(
                        ColorStateList.valueOf(Color.parseColor("#4CAF50")));
            }
        }

        holder.btnAccion.setOnClickListener(v -> {
            if (listener != null) listener.onAccionClick(m);
        });
    }

    /**
     * DiffUtil para optimizar actualizaciones del RecyclerView.
     */
    private static final DiffUtil.ItemCallback<ClaseModel> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull ClaseModel o, @NonNull ClaseModel n) {
                    return o.idClase == n.idClase;
                }
                @Override
                public boolean areContentsTheSame(@NonNull ClaseModel o, @NonNull ClaseModel n) {
                    return o.fecha == n.fecha && o.horaInicio == n.horaInicio
                            && o.horaFin == n.horaFin && o.inscritos == n.inscritos
                            && Objects.equals(o.disciplina, n.disciplina)
                            && Objects.equals(o.nombreProfesor, n.nombreProfesor)
                            && Objects.equals(o.nombrePista, n.nombrePista);
                }
            };

    /**
     * ViewHolder que contiene las referencias a las vistas del item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDisciplina, tvDia, tvProfesor, tvPista, tvHora, tvCupo, tvNivel;;
        MaterialButton btnAccion;

        public ViewHolder(@NonNull View v) {
            super(v);
            tvDisciplina = v.findViewById(R.id.tvDisciplina);
            tvNivel = v.findViewById(R.id.tvNivel);
            tvDia = v.findViewById(R.id.tvDia);
            tvProfesor = v.findViewById(R.id.tvProfesor);
            tvPista = v.findViewById(R.id.tvPista);
            tvHora = v.findViewById(R.id.tvHora);
            tvCupo = v.findViewById(R.id.tvCupo);
            btnAccion = v.findViewById(R.id.btnAccion);
        }
    }
}