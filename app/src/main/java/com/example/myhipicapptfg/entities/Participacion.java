package com.example.myhipicapptfg.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
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
                        entity = Convocatoria.class,
                        parentColumns = "ID_Convocatoria",
                        childColumns = "ID_Convocatoria",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class Participacion {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Participacion")
    public int idParticipacion;

    @ColumnInfo(name = "Puntaje")
    public double puntaje; // derivado, puede calcularse desde Calificación

    @ColumnInfo(name = "Hora")
    public String hora; // Time 'HH:MM:SS'

    @ColumnInfo(name = "ID_Alumno")
    public int idAlumno;

    @ColumnInfo(name = "ID_Equino")
    public int idEquino;

    @ColumnInfo(name = "ID_Convocatoria")
    public int idConvocatoria;
}