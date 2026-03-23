package com.example.myhipicapptfg.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "RutaPersonal",
        foreignKeys = @ForeignKey(
                entity = Propietario.class,
                parentColumns = "ID_Propietario",
                childColumns = "ID_Propietario",
                onDelete = ForeignKey.CASCADE
        )
)
public class RutaPersonal {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Ruta_Personal")
    public int idRutaPersonal;

    @ColumnInfo(name = "Nombre")
    public String nombre;

    @ColumnInfo(name = "Duracion")
    public String duracion; // Guardar como TEXT HH:MM

    @ColumnInfo(name = "Hora")
    public String hora; // Guardar como TEXT HH:MM

    @ColumnInfo(name = "Fecha")
    public String fecha; // Guardar como TEXT YYYY-MM-DD

    @ColumnInfo(name = "Distancia_Recorrida")
    public double distanciaRecorrida; // en Km

    @ColumnInfo(name = "ID_Propietario")
    public int idPropietario;
}
