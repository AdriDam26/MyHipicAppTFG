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
import com.example.myhipicapptfg.model.ClaseUIModel;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;


public class ReservaClaseAdapter
        extends ListAdapter<ClaseUIModel, ReservaClaseAdapter.ViewHolder> {

    private final OnClaseClickListener listener;
    private final boolean esModoCancelacion;
    private final SimpleDateFormat timeFormat =
            new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public interface OnClaseClickListener {
        void onAccionClick(ClaseUIModel clase);
    }

    public ReservaClaseAdapter(boolean esModoCancelacion, OnClaseClickListener listener) {
        super(DIFF_CALLBACK);
        this.esModoCancelacion = esModoCancelacion;
        this.listener = listener;
    }

    // ── setDatosReferencia() ya no existe ─────────────────────────────────────

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clase_reserva, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClaseUIModel m = getItem(position);

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

            // ✅ Ya viene resuelto, sin bucles
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

            // ✅ inscritos viene del COUNT() del JOIN
            if (m.inscritos >= ClaseUIModel.MAXIMO) {
                holder.tvCupo.setTextColor(Color.RED);
                holder.tvCupo.setText("¡LLENO! " + m.inscritos + "/" + ClaseUIModel.MAXIMO);
                holder.btnAccion.setEnabled(false);
                holder.btnAccion.setBackgroundTintList(
                        ColorStateList.valueOf(Color.LTGRAY));
            } else {
                holder.tvCupo.setTextColor(Color.parseColor("#666666"));
                holder.tvCupo.setText("Inscritos: " + m.inscritos + "/" + ClaseUIModel.MAXIMO);
                holder.btnAccion.setEnabled(true);
                holder.btnAccion.setBackgroundTintList(
                        ColorStateList.valueOf(Color.parseColor("#4CAF50")));
            }
        }

        holder.btnAccion.setOnClickListener(v -> {
            if (listener != null) listener.onAccionClick(m);
        });
    }

    private static final DiffUtil.ItemCallback<ClaseUIModel> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull ClaseUIModel o, @NonNull ClaseUIModel n) {
                    return o.idClase == n.idClase;
                }
                @Override
                public boolean areContentsTheSame(@NonNull ClaseUIModel o, @NonNull ClaseUIModel n) {
                    return o.fecha == n.fecha && o.horaInicio == n.horaInicio
                            && o.horaFin == n.horaFin && o.inscritos == n.inscritos
                            && Objects.equals(o.disciplina, n.disciplina)
                            && Objects.equals(o.nombreProfesor, n.nombreProfesor)
                            && Objects.equals(o.nombrePista, n.nombrePista);
                }
            };

    // ViewHolder sin cambios
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