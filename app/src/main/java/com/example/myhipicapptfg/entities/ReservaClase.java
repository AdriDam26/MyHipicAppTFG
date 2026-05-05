package com.example.myhipicapptfg.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
        tableName = "ReservaClase",
        primaryKeys = {"ID_Alumno", "ID_Clase"},
        foreignKeys = {
                @ForeignKey(
                        entity = Alumno.class,
                        parentColumns = "ID_Alumno",
                        childColumns = "ID_Alumno",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Clase.class,
                        parentColumns = "ID_Clase",
                        childColumns = "ID_Clase",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class ReservaClase {

    @ColumnInfo(name = "ID_Alumno")
    public int idAlumno;

    @ColumnInfo(name = "ID_Clase")
    public int idClase;

    @ColumnInfo(name = "Fecha_Reserva")
    public long fechaReserva;


    // 🔹 Estados posibles

    // 🔹 Constructor vacío (OBLIGATORIO Room)
    public ReservaClase() {}

    // 🔹 Constructor recomendado
    public ReservaClase(int idAlumno, int idClase, long fechaReserva, String estado) {
        this.idAlumno = idAlumno;
        this.idClase = idClase;
        this.fechaReserva = fechaReserva;
    }
}