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
                @Index(value = {"ID_Prueba"}),
                @Index(value = {"ID_Alumno", "ID_Equino", "ID_Prueba"}, unique = true)
        }
)
public class Participacion {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Participacion")
    public int idParticipacion;

    @ColumnInfo(name = "Orden_Salida")
    public int ordenSalida;

    // Calculado desde Puntuacion, no se introduce a mano
    @ColumnInfo(name = "Nota_Final")
    public double notaFinal;

    // (suma notas×coef / suma 10×coef) × 100
    @ColumnInfo(name = "Porcentaje")
    public double porcentaje;

    @ColumnInfo(name = "ID_Alumno")
    public int idAlumno;

    @ColumnInfo(name = "ID_Equino")
    public int idEquino;

    @ColumnInfo(name = "ID_Prueba")
    public int idPrueba;

    @ColumnInfo(name = "Correccion")
    public double correccion; // 0.0, 2.0 o 4.0

    @ColumnInfo(name = "Eliminado")
    public boolean eliminado;

    public Participacion() {}

    public Participacion(int ordenSalida, int idAlumno,
                         int idEquino, int idPrueba) {
        this.ordenSalida = ordenSalida;
        this.idAlumno    = idAlumno;
        this.idEquino    = idEquino;
        this.idPrueba    = idPrueba;
        this.notaFinal   = 0.0;
        this.porcentaje  = 0.0;
    }
}