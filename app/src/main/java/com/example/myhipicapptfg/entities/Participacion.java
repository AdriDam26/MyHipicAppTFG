package com.example.myhipicapptfg.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Participacion",
        foreignKeys = {
                @ForeignKey(
                        entity = Alumno.class,
                        parentColumns = "ID_Alumno",
                        childColumns = "ID_Alumno",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Equino.class,
                        parentColumns = "ID_Equino",
                        childColumns = "ID_Equino",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Prueba.class,
                        parentColumns = "ID_Prueba",
                        childColumns = "ID_Prueba",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = {"ID_Alumno"}),
                @Index(value = {"ID_Equino"}),
                @Index(value = {"ID_Prueba"})
        }
)
public class Participacion {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Participacion")
    public int idParticipacion;

    @ColumnInfo(name = "Posicion")
    public int posicion;

    @ColumnInfo(name = "Hora_Salida")
    public long horaSalida;

    @ColumnInfo(name = "ID_Alumno")
    public int idAlumno;

    @ColumnInfo(name = "ID_Equino")
    public int idEquino;

    @ColumnInfo(name = "ID_Prueba")
    public int idPrueba;

    // 🔹 Constructor vacío (Room)
    public Participacion() {}

    // 🔹 Constructor recomendado
    public Participacion(int posicion,
                         long horaSalida,
                         int idAlumno,
                         int idEquino,
                         int idPrueba) {

        this.posicion = posicion;
        this.horaSalida = horaSalida;
        this.idAlumno = idAlumno;
        this.idEquino = idEquino;
        this.idPrueba = idPrueba;
    }
}