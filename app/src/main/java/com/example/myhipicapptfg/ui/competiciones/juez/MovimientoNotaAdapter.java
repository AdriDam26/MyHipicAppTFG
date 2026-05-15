package com.example.myhipicapptfg.ui.competiciones.juez;

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

public class MovimientoNotaAdapter
        extends RecyclerView.Adapter<MovimientoNotaAdapter.VH> {

    // ── Callback para avisar a la Activity cuando cambia cualquier nota ──
    public interface OnNotaCambiadaListener {
        void onNotaCambiada();
    }

    private OnNotaCambiadaListener notaListener;

    public void setOnNotaCambiadaListener(OnNotaCambiadaListener l) {
        this.notaListener = l;
    }
    // ─────────────────────────────────────────────────────────────────────

    private List<MovimientoConNota> items = new ArrayList<>();

    public void setItems(List<MovimientoConNota> list) {
        items = list;
        notifyDataSetChanged();
    }

    public List<MovimientoConNota> getItems() { return items; }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movimiento_nota, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        MovimientoConNota m = items.get(position);

        h.tvOrden.setText(String.valueOf(m.orden));
        h.tvLetra.setText(m.letra);
        h.tvEjercicio.setText(m.ejercicio);
        h.tvCoef.setText("×" + m.coeficiente);
        h.tvDirectriz.setText(m.directriz);

        // Evitar disparar watcher al hacer bind
        h.etNota.setTag(position);
        h.etObs.setTag(position);

        h.etNota.removeTextChangedListener(h.notaWatcher);
        h.etObs.removeTextChangedListener(h.obsWatcher);

        h.etNota.setText(m.nota > 0 ? String.valueOf(m.nota) : "");
        h.etObs.setText(m.observacion != null ? m.observacion : "");

        h.notaWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
            @Override
            public void afterTextChanged(Editable s) {
                int pos = (int) h.etNota.getTag();
                try {
                    double v = Double.parseDouble(s.toString());
                    if (v < 0)  v = 0;
                    if (v > 10) v = 10;
                    items.get(pos).nota = v;
                } catch (NumberFormatException ignored) {
                    items.get(pos).nota = 0;
                }
                // Avisar a la Activity para recalcular en tiempo real
                if (notaListener != null) notaListener.onNotaCambiada();
            }
        };

        h.obsWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
            @Override
            public void afterTextChanged(Editable s) {
                int pos = (int) h.etObs.getTag();
                items.get(pos).observacion = s.toString();
            }
        };

        h.etNota.addTextChangedListener(h.notaWatcher);
        h.etObs.addTextChangedListener(h.obsWatcher);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvOrden, tvLetra, tvEjercicio, tvCoef, tvDirectriz;
        EditText etNota, etObs;
        TextWatcher notaWatcher, obsWatcher;

        VH(@NonNull View v) {
            super(v);
            tvOrden     = v.findViewById(R.id.tvOrden);
            tvLetra     = v.findViewById(R.id.tvLetra);
            tvEjercicio = v.findViewById(R.id.tvEjercicio);
            tvCoef      = v.findViewById(R.id.tvCoeficiente);
            tvDirectriz = v.findViewById(R.id.tvDirectriz);
            etNota      = v.findViewById(R.id.etNota);
            etObs       = v.findViewById(R.id.etObservacion);
        }
    }
}