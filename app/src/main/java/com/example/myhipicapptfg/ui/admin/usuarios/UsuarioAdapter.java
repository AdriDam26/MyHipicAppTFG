package com.example.myhipicapptfg.ui.admin.usuarios;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;

import java.util.List;

import com.google.android.material.imageview.ShapeableImageView;
import android.net.Uri;


public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder> {

    private List<Usuario> lista;
    private OnClick listener;

    public interface OnClick {
        void editar(Usuario u);
        void eliminar(Usuario u);
        void clickItem(Usuario u);
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

        TextView nombre, email, tipo, telefono;
        ImageButton editar, eliminar;
        ShapeableImageView foto;

        public ViewHolder(View v) {
            super(v);

            // ✅ IDs del nuevo XML (Usuario)
            nombre = v.findViewById(R.id.txtNombreUsuario);
            email = v.findViewById(R.id.txtEmailUsuario);
            telefono = v.findViewById(R.id.txtTelefonoUsuario);
            tipo = v.findViewById(R.id.txtTipoUsuario);
            foto     = v.findViewById(R.id.imgFotoUsuario);
            editar = v.findViewById(R.id.btnEditarUsuario);
            eliminar = v.findViewById(R.id.btnEliminarUsuario);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usuario, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int pos) {

        Usuario u = lista.get(pos);

        h.nombre.setText(u.nombre + " " + u.apellido1);
        h.email.setText(u.email);

        //teléfono directo
        h.telefono.setText(u.telefono != null ? u.telefono : "");

        // tipo
        h.tipo.setText(u.tipo);

        // color por tipo
        switch (u.tipo) {

            case Usuario.TIPO_PROFESOR:
                h.tipo.setBackgroundTintList(ColorStateList.valueOf(0xFFE0F2FE));
                h.tipo.setTextColor(0xFF1D4ED8);
                break;

            case Usuario.TIPO_ALUMNO:
                h.tipo.setBackgroundTintList(ColorStateList.valueOf(0xFFDCFCE7));
                h.tipo.setTextColor(0xFF166534);
                break;

            case Usuario.TIPO_JUEZ:
                h.tipo.setBackgroundTintList(ColorStateList.valueOf(0xFFFEE2E2));
                h.tipo.setTextColor(0xFF991B1B);
                break;

            case Usuario.TIPO_ADMIN:
                h.tipo.setBackgroundTintList(ColorStateList.valueOf(0xFFFEF3C7));
                h.tipo.setTextColor(0xFF92400E);
                break;

            case Usuario.TIPO_PROPIETARIO:
                h.tipo.setBackgroundTintList(ColorStateList.valueOf(0xFFEDE9FE));
                h.tipo.setTextColor(0xFF5B21B6);
                break;

            default:
                h.tipo.setBackgroundTintList(ColorStateList.valueOf(0xFFEEF2FF));
                h.tipo.setTextColor(0xFF4338CA);
                break;
        }

        // Foto de perfil
        Glide.with(h.itemView.getContext())
                .load(u.fotoPerfil)
                .placeholder(R.drawable.ic_person_placeholder)
                .error(R.drawable.ic_person_placeholder)
                .circleCrop() // Si quieres que sea redonda
                .into(h.foto);

        h.editar.setOnClickListener(v -> listener.editar(u));
        h.eliminar.setOnClickListener(v -> listener.eliminar(u));
        h.itemView.setOnClickListener(v -> listener.clickItem(u));
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }
}