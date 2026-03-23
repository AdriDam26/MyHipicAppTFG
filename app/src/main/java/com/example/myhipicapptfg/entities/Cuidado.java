package com.example.myhipicapptfg.entities;


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
    public String fecha; // TEXT 'YYYY-MM-DD'

    @ColumnInfo(name = "Proxima_Revision")
    public String proximaRevision; // puede ser null

    @ColumnInfo(name = "Descripcion")
    public String descripcion;

    @ColumnInfo(name = "ID_Equino")
    public int idEquino;

    // Constantes Tipo de Cuidado
    public static final String VETERINARIO = "Veterinario";
    public static final String VACUNA = "Vacuna";
    public static final String HERRAJE = "Herraje";
    public static final String DENTISTA = "Dentista";
    public static final String FISIOTERAPIA = "Fisioterapia";
    public static final String DESPARASITACION = "Desparasitación";
    public static final String ENTRENAMIENTO = "Entrenamiento";
    public static final String ALIMENTACION = "Alimentación";
    public static final String MEDICINAS = "Medicinas";
    public static final String PELUQUERIA = "Peluquería";
    public static final String OTROS = "Otros";
}