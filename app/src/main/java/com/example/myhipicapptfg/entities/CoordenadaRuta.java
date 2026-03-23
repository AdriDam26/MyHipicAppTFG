package com.example.myhipicapptfg.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "CoordenadaRuta",
        foreignKeys = @ForeignKey(
                entity = RutaPersonal.class,
                parentColumns = "ID_Ruta_Personal",
                childColumns = "ID_Ruta_Personal",
                onDelete = ForeignKey.CASCADE
        )
)
public class CoordenadaRuta {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Coordenada")
    public int idCordenada;

    @ColumnInfo(name = "Orden")
    public int orden; // >=1

    @ColumnInfo(name = "Latitud")
    public double latitud;

    @ColumnInfo(name = "Longitud")
    public double longitud;

    @ColumnInfo(name = "ID_Ruta_Personal")
    public int idRutaPersonal;
}