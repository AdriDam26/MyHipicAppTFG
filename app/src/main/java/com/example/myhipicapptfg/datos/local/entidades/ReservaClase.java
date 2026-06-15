package com.example.myhipicapptfg.datos.local.entidades;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

/**
 * Entidad que representa la tabla de relación entre Alumno y Clase.
 *
 * Esta tabla modela las reservas realizadas por los alumnos en las clases.
 * Cada registro indica qué alumno ha reservado qué clase y en qué fecha.
 *
 * Se trata de una relación N:M (muchos a muchos) resuelta mediante una tabla intermedia.
 */
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

    /**
     * Identificador del alumno que realiza la reserva.
     */
    @ColumnInfo(name = "ID_Alumno")
    public int idAlumno;


    /**
     * Identificador de la clase reservada.
     */
    @ColumnInfo(name = "ID_Clase")
    public int idClase;

    /**
     * Fecha en la que se realiza la reserva.
     * Se almacena como timestamp (milisegundos desde epoch).
     */
    @ColumnInfo(name = "Fecha_Reserva")
    public long fechaReserva;


    /**
     * Constructor vacío obligatorio para Room
     */
    public ReservaClase() {}

    /**
     * Constructor recomendado para crear reservas de forma rápida.
     *
     * @param idAlumno     ID del alumno
     * @param idClase      ID de la clase
     * @param fechaReserva fecha de creación de la reserva (timestamp)
     */
    public ReservaClase(int idAlumno, int idClase, long fechaReserva) {
        this.idAlumno = idAlumno;
        this.idClase = idClase;
        this.fechaReserva = fechaReserva;
    }


}