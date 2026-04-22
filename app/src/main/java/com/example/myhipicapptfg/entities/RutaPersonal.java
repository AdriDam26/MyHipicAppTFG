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
                childColumns = "ID_Usuario",
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
    public long duracion; // en milisegundos o segundos

    @ColumnInfo(name = "Hora")
    public long hora;

    @ColumnInfo(name = "Fecha")
    public long fecha;

    @ColumnInfo(name = "Distancia_Recorrida")
    public double distanciaRecorrida; // km

    @ColumnInfo(name = "ID_Usuario")
    public int idUsuario;

    // 🔹 Constructor vacío (OBLIGATORIO Room)
    public RutaPersonal() {}

    // 🔹 Constructor recomendado
    public RutaPersonal(String nombre,
                        long duracion,
                        long hora,
                        long fecha,
                        double distanciaRecorrida,
                        int idUsuario) {

        this.nombre = nombre;
        this.duracion = duracion;
        this.hora = hora;
        this.fecha = fecha;
        this.distanciaRecorrida = distanciaRecorrida;
        this.idUsuario = idUsuario;
    }
}