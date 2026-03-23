package com.example.myhipicapptfg.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Usuario",
        indices = {
                @Index(value = {"DNI"}, unique = true),
                @Index(value = {"Email"}, unique = true)
        }
)
public class Usuario {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Usuario")
    public int idUsuario;

    @ColumnInfo(name = "Email")
    public String email;

    @ColumnInfo(name = "Telefono")
    public String telefono;

    @ColumnInfo(name = "Apellido1")
    public String apellido1;

    @ColumnInfo(name = "Apellido2")
    public String apellido2;

    @ColumnInfo(name = "DNI")
    public String dni;

    @ColumnInfo(name = "Nombre")
    public String nombre;

    @ColumnInfo(name = "Fecha_Nacimiento")
    public String fechaNacimiento; // Guardar como TEXT 'YYYY-MM-DD'

    @ColumnInfo(name = "Sexo")
    public String sexo;

    @ColumnInfo(name = "Tipo")
    public String tipo;

    // Valores permitidos para sexo
    public static final String SEXO_MASCULINO = "M";
    public static final String SEXO_FEMENINO = "F";

    // Valores permitidos para tipo
    public static final String TIPO_ADMIN = "admin";
    public static final String TIPO_ALUMNO = "alumno";
    public static final String TIPO_PROFESOR = "profesor";
    public static final String TIPO_PROPIETARIO = "propietario";

}