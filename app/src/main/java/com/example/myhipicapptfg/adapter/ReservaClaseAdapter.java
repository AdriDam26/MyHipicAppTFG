package com.example.myhipicapptfg.adapter;

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
import com.example.myhipicapptfg.entities.Clase;
import com.example.myhipicapptfg.entities.Usuario;
import com.example.myhipicapptfg.entities.Pista;
import com.example.myhipicapptfg.entities.ReservaClase;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ReservaClaseAdapter extends ListAdapter<Clase, ReservaClaseAdapter.ViewHolder> {

    private final OnClaseClickListener listener;
    private final boolean esModoCancelacion;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private List<Usuario> listaUsuarios;
    private List<Pista> listaPistas;
    private List<ReservaClase> todasLasReservas;

    public interface OnClaseClickListener {
        void onAccionClick(Clase clase);
    }

    public ReservaClaseAdapter(boolean esModoCancelacion, OnClaseClickListener listener) {
        super(DIFF_CALLBACK);
        this.esModoCancelacion = esModoCancelacion;
        this.listener = listener;
    }

    public void setDatosReferencia(List<Usuario> usuarios, List<Pista> pistas, List<ReservaClase> reservas) {
        this.listaUsuarios = usuarios;
        this.listaPistas = pistas;
        this.todasLasReservas = reservas;
        // Importante: notifyDataSetChanged es necesario aquí porque cambian datos externos a la lista de Clases
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clase_reserva, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Clase clase = getItem(position);

        // 1. Datos básicos
        holder.tvDisciplina.setText(clase.disciplina != null ? clase.disciplina : "Clase");
        holder.tvDia.setText(clase.fecha != 0 ? dateFormat.format(clase.fecha) : "Sin fecha");

        try {
            String horaTexto = timeFormat.format(clase.horaInicio) + " - " + timeFormat.format(clase.horaFin);
            holder.tvHora.setText(horaTexto);
        } catch (Exception e) {
            holder.tvHora.setText("--:--");
        }

        // 2. Lógica de Cupo (Cálculo dinámico)
        int inscritos = obtenerNumeroInscritos(clase.idClase);
        int maximo = 10;

        // 3. Configuración según el modo (RESERVA vs CANCELACIÓN)
        if (esModoCancelacion) {
            // --- MODO MIS RESERVAS ---
            // Mostramos nombres, ocultamos cupo
            holder.tvProfesor.setVisibility(View.VISIBLE);
            holder.tvPista.setVisibility(View.VISIBLE);
            holder.tvCupo.setVisibility(View.GONE);

            holder.tvProfesor.setText("Prof: " + obtenerNombreProfesor(clase.idProfesor));
            holder.tvPista.setText("Pista: " + obtenerNombrePista(clase.idPista));

            holder.btnAccion.setText("Cancelar");
            holder.btnAccion.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E57373")));
            holder.btnAccion.setEnabled(true);
        } else {
            // --- MODO RESERVAR ---
            // Ocultamos nombres (como solicitaste), mostramos cupo
            holder.tvProfesor.setVisibility(View.GONE);
            holder.tvPista.setVisibility(View.GONE);
            holder.tvCupo.setVisibility(View.VISIBLE);

            holder.btnAccion.setText("Reservar");
            holder.tvCupo.setText("Inscritos: " + inscritos + "/" + maximo);

            if (inscritos >= maximo) {
                holder.tvCupo.setTextColor(Color.RED);
                holder.tvCupo.setText("¡LLENO! " + inscritos + "/" + maximo);
                holder.btnAccion.setEnabled(false);
                holder.btnAccion.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            } else {
                holder.tvCupo.setTextColor(Color.parseColor("#666666"));
                holder.btnAccion.setEnabled(true);
                holder.btnAccion.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
            }
        }

        holder.btnAccion.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAccionClick(clase);
            }
        });
    }

    private String obtenerNombreProfesor(int idProfesor) {
        if (listaUsuarios == null) return "Cargando...";
        for (Usuario u : listaUsuarios) {
            if (u.idUsuario == idProfesor) {
                return u.nombre != null ? u.nombre : "Sin nombre";
            }
        }
        return "No asignado";
    }

    private String obtenerNombrePista(int idPista) {
        if (listaPistas == null) return "Cargando...";
        for (Pista p : listaPistas) {
            if (p.idPista == idPista) return p.nombre;
        }
        return "No asignada";
    }

    private int obtenerNumeroInscritos(int idClase) {
        if (todasLasReservas == null) return 0;
        int contador = 0;
        for (ReservaClase r : todasLasReservas) {
            if (r.idClase == idClase) {
                contador++;
            }
        }
        return contador;
    }

    private static final DiffUtil.ItemCallback<Clase> DIFF_CALLBACK = new DiffUtil.ItemCallback<Clase>() {
        @Override
        public boolean areItemsTheSame(@NonNull Clase oldItem, @NonNull Clase newItem) {
            return oldItem.idClase == newItem.idClase;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Clase oldItem, @NonNull Clase newItem) {
            // Incluimos IDs de profesor y pista para que el DiffUtil detecte cambios si se reasignan
            return oldItem.fecha == newItem.fecha &&
                    oldItem.horaInicio == newItem.horaInicio &&
                    oldItem.horaFin == newItem.horaFin &&
                    oldItem.idProfesor == newItem.idProfesor &&
                    oldItem.idPista == newItem.idPista &&
                    Objects.equals(oldItem.disciplina, newItem.disciplina);
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDisciplina, tvDia, tvProfesor, tvPista, tvHora, tvCupo;
        MaterialButton btnAccion;

        public ViewHolder(@NonNull View v) {
            super(v);
            tvDisciplina = v.findViewById(R.id.tvDisciplina);
            tvDia = v.findViewById(R.id.tvDia);
            tvProfesor = v.findViewById(R.id.tvProfesor);
            tvPista = v.findViewById(R.id.tvPista);
            tvHora = v.findViewById(R.id.tvHora);
            tvCupo = v.findViewById(R.id.tvCupo);
            btnAccion = v.findViewById(R.id.btnAccion);
        }
    }
}