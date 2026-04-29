package com.example.myhipicapptfg.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "RutaPersonal",
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "ID_Usuario",
                childColumns = "ID_Propietario",
                onDelete = ForeignKey.CASCADE
        )
)
public class RutaPersonal {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Ruta_Personal")
    public long idRutaPersonal;

    @ColumnInfo(name = "Nombre")
    public String nombre;

    @ColumnInfo(name = "Duracion")
    public String duracion;

    @ColumnInfo(name = "Hora")
    public String hora;

    @ColumnInfo(name = "Fecha")
    public String fecha;

    @ColumnInfo(name = "Distancia_Recorrida")
    public double distanciaRecorrida;

    @ColumnInfo(name = "ID_Propietario")
    public long idPropietario;
}