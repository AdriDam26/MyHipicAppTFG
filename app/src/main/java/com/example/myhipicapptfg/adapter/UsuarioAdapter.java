package com.example.myhipicapptfg.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.entities.Usuario;

import java.util.ArrayList;
import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder> {

    private List<Usuario> lista;
    private OnClick listener;

    public interface OnClick {
        void editar(Usuario u);
        void eliminar(Usuario u);
    }

    public UsuarioAdapter(List<Usuario> lista, OnClick listener) {
        this.lista = lista;
        this.listener = listener;
    }

    public void actualizar(List<Usuario> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, email, tipo;
        ImageButton editar, eliminar;

        public ViewHolder(View v) {
            super(v);
            nombre = v.findViewById(R.id.txtNombreUsuario);
            email = v.findViewById(R.id.txtEmailUsuario);
            tipo = v.findViewById(R.id.txtTipoUsuario);
            editar = v.findViewById(R.id.btnEditar);
            eliminar = v.findViewById(R.id.btnEliminar);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usuario, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int pos) {

        Usuario u = lista.get(pos);

        h.nombre.setText(u.nombre + " " + u.apellido1);
        h.email.setText(u.email);
        h.tipo.setText(u.tipo);

        h.editar.setOnClickListener(v -> listener.editar(u));
        h.eliminar.setOnClickListener(v -> listener.eliminar(u));
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }
}