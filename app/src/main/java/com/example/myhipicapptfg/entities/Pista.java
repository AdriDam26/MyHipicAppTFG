package com.example.myhipicapptfg.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Pista",
        indices = {@Index(value = {"Nombre"}, unique = true)} // ⚡ índice único en Nombre
)
public class Pista {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Pista")
    public int idPista;

    @ColumnInfo(name = "Nombre")
    public String nombre;

    @ColumnInfo(name = "Ancho")
    public double ancho;

    @ColumnInfo(name = "Largo")
    public double largo;

    @ColumnInfo(name = "Estado")
    public String estado;

    // Valores permitidos para Estado
    public static final String DISPONIBLE = "Disponible";
    public static final String MANTENIMIENTO = "Mantenimiento";
    public static final String CERRADA = "Cerrada";
}
