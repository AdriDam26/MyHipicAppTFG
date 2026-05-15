package com.example.myhipicapptfg.datos.local.entidades;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Cuidado",
        foreignKeys = @ForeignKey(
                entity = Equino.class,
                parentColumns = "ID_Equino",
                childColumns = "ID_Equino",
                onDelete = ForeignKey.CASCADE
        )
)
public class Cuidado {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Cuidado")
    public int idCuidado;

    @ColumnInfo(name = "Tipo_Cuidado")
    public String tipoCuidado;

    @ColumnInfo(name = "Fecha")
    public long fecha;


    @ColumnInfo(name = "Descripcion")
    public String descripcion;

    @ColumnInfo(name = "ID_Equino")
    public int idEquino;

    // 🔹 Tipos de cuidado
    public static final String VETERINARIO = "Veterinario";
    public static final String VACUNA = "Vacuna";
    public static final String HERRAJE = "Herraje";
    public static final String DESPARASITACION = "Desparasitacion";
    public static final String FISIOTERAPIA = "Fisioterapia";
    public static final String ALIMENTACION = "Alimentacion";
    public static final String ENTRENAMIENTO = "Entrenamiento";
    public static final String OTROS = "Otros";

    // 🔹 Constructor vacío (OBLIGATORIO Room)
    public Cuidado() {}

    // 🔹 Constructor recomendado
    public Cuidado(int idEquino,
                   String tipoCuidado,
                   long fecha,
                   Long proximaRevision,
                   String descripcion) {

        this.idEquino = idEquino;
        this.tipoCuidado = tipoCuidado;
        this.fecha = fecha;
        this.descripcion = descripcion;
    }
}