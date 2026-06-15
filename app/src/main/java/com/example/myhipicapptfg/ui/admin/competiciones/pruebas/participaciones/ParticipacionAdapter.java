package com.example.myhipicapptfg.ui.admin.competiciones.pruebas.participaciones;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Participacion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Adapter encargado de mostrar las participaciones dentro de una prueba.
 *
 * Responsabilidades:
 * - Mostrar orden de salida
 * - Mostrar nombre del alumno (jinete)
 * - Mostrar nombre del equino (binomio)
 * - Gestionar acciones de edición y eliminación
 *
 * Utiliza mapas externos para resolver IDs a nombres.
 */

public class ParticipacionAdapter extends RecyclerView.Adapter<ParticipacionAdapter.VH> {

    /**
     * Eventos de interacción con la UI
     */
    public interface OnClick {
        void editar(Participacion p);
        void eliminar(Participacion p);
    }

    private List<Participacion>  lista          = new ArrayList<>();

    // Mapa: idAlumno -> nombre
    private Map<Integer, String> nombresAlumnos = new HashMap<>();

    // Mapa: idEquino -> nombre
    private Map<Integer, String> nombresEquinos = new HashMap<>();

    // Listener de eventos UI
    private final OnClick listener;

    public ParticipacionAdapter(OnClick listener) {
        this.listener = listener;
    }

    /**
     * Actualiza la lista de participaciones
     */

    public void actualizar(List<Participacion> nuevaLista) {
        this.lista = nuevaLista != null ? nuevaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * Actualiza la lista de participaciones
     */

    public void actualizarNombresAlumnos(Map<Integer, String> mapa) {
        this.nombresAlumnos = mapa;
        notifyDataSetChanged();
    }

    /**
     * Actualiza el mapa de equinos (id → nombre)
     */
    public void actualizarNombresEquinos(Map<Integer, String> mapa) {
        this.nombresEquinos = mapa;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_participacion, parent, false);
        return new VH(v);
    }

    /**
     * Vincula los datos de una participación con la vista
     */
    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Participacion p = lista.get(position);

        h.txtOrden.setText(String.valueOf(p.ordenSalida));

        String nombreAlumno = nombresAlumnos.containsKey(p.idAlumno)
                ? nombresAlumnos.get(p.idAlumno)
                : "Alumno #" + p.idAlumno;

        String nombreEquino = nombresEquinos.containsKey(p.idEquino)
                ? "🐴 " + nombresEquinos.get(p.idEquino)
                : "🐴 Equino #" + p.idEquino;

        h.txtJinete.setText(nombreAlumno);
        h.txtInfo.setText(nombreEquino);

        h.btnEditar.setOnClickListener(v -> listener.editar(p));
        h.btnDelete.setOnClickListener(v -> listener.eliminar(p));
    }

    @Override
    public int getItemCount() { return lista.size(); }

    /**
     * ViewHolder:
     * cachea referencias del layout item_participacion
     * para mejorar rendimiento del RecyclerView
     */
    static class VH extends RecyclerView.ViewHolder {
        TextView    txtOrden, txtJinete, txtInfo;
        ImageButton btnEditar, btnDelete;

        VH(@NonNull View v) {
            super(v);
            txtOrden  = v.findViewById(R.id.txtOrdenSalida);
            txtJinete = v.findViewById(R.id.txtNombreJinete);
            txtInfo   = v.findViewById(R.id.txtNombreBinomio);
            btnEditar = v.findViewById(R.id.btnEditarParticipacion);
            btnDelete = v.findViewById(R.id.btnEliminarParticipacion);
        }
    }
}