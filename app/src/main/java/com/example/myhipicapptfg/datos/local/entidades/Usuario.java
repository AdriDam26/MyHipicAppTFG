package com.example.myhipicapptfg.datos.local.entidades;

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
    public long fechaNacimiento;

    @ColumnInfo(name = "Fecha_Registro")
    public long fechaRegistro;

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

    public static final String TIPO_JUEZ = "juez";

    @Override
    public String toString() {
        return nombre + " " + apellido1 + " " + apellido2;
    }

    public Usuario() {}

    public Usuario(String email, String telefono,
                   String apellido1, String apellido2, String dni,
                   String nombre, long fechaNacimiento,
                   long fechaRegistro, String sexo, String tipo) {

        this.email = email;
        this.telefono = telefono;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.dni = dni;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.fechaRegistro = fechaRegistro;
        this.sexo = sexo;
        this.tipo = tipo;
    }


}