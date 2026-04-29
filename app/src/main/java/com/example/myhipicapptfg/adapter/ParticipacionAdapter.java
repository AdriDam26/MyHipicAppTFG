package com.example.myhipicapptfg.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.entities.Participacion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticipacionAdapter extends RecyclerView.Adapter<ParticipacionAdapter.VH> {

    public interface OnClick {
        void editar(Participacion p);
        void eliminar(Participacion p);
    }

    private List<Participacion>  lista          = new ArrayList<>();
    private Map<Integer, String> nombresAlumnos = new HashMap<>();
    private Map<Integer, String> nombresEquinos = new HashMap<>();

    private final OnClick listener;

    public ParticipacionAdapter(OnClick listener) {
        this.listener = listener;
    }

    public void actualizar(List<Participacion> nuevaLista) {
        this.lista = nuevaLista != null ? nuevaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    // Llamado desde la Activity cuando llegan los usuarios
    public void actualizarNombresAlumnos(Map<Integer, String> mapa) {
        this.nombresAlumnos = mapa;
        notifyDataSetChanged();
    }

    // Llamado desde la Activity cuando llegan los equinos
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

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Participacion p = lista.get(position);

        // Número de dorsal
        h.txtOrden.setText(String.valueOf(p.ordenSalida));

        // Nombre alumno
        String nombreAlumno = nombresAlumnos.containsKey(p.idAlumno)
                ? nombresAlumnos.get(p.idAlumno)
                : "Alumno #" + p.idAlumno;

        // Nombre equino
        String nombreEquino = nombresEquinos.containsKey(p.idEquino)
                ? nombresEquinos.get(p.idEquino)
                : "Equino #" + p.idEquino;

        h.txtInfo.setText(nombreAlumno + " · " + nombreEquino);

        // Nota final
        h.txtNota.setText(String.format("Nota: %.2f%%", p.notaFinal));

        h.btnEditar.setOnClickListener(v -> listener.editar(p));
        h.btnDelete.setOnClickListener(v -> listener.eliminar(p));
    }

    @Override
    public int getItemCount() { return lista.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView    txtOrden, txtInfo, txtNota;
        ImageButton btnDelete, btnEditar;

        VH(@NonNull View v) {
            super(v);
            txtOrden  = v.findViewById(R.id.txtOrdenSalida);
            txtInfo   = v.findViewById(R.id.txtNombreBinomio);
            txtNota   = v.findViewById(R.id.txtNotaFinal);
            btnDelete = v.findViewById(R.id.btnEliminarParticipacion);
            btnEditar = v.findViewById(R.id.btnEditarParticipacion);
        }
    }
}