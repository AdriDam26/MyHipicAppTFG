package com.example.myhipicapptfg.datos.local.entidades;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entidad Participacion para la base de datos Room.
 *
 * Representa la participación de un alumno con un equino en una prueba concreta.
 */
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
                // Índices para mejorar rendimiento de búsquedas
                @Index(value = {"ID_Alumno"}),
                @Index(value = {"ID_Equino"}),
                @Index(value = {"ID_Prueba"}),
                // Evita duplicar la misma participación (mismo alumno, equino y prueba)
                @Index(value = {"ID_Alumno", "ID_Equino", "ID_Prueba"}, unique = true)
        }
)
public class Participacion {

    /**
     * Identificador único de la participación (clave primaria autogenerada).
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Participacion")
    public int idParticipacion;

    // Atributos
    @ColumnInfo(name = "Orden_Salida")
    public int ordenSalida;

    /**
     * Nota final obtenida.
     * Se calcula a partir de las puntuaciones.
     */
    @ColumnInfo(name = "Nota_Final")
    public double notaFinal;

    /**
     * Nota final obtenida.
     * Se calcula a partir de las puntuaciones.
     */
    @ColumnInfo(name = "Porcentaje")
    public double porcentaje;

    @ColumnInfo(name = "ID_Alumno")
    public int idAlumno;

    @ColumnInfo(name = "ID_Equino")
    public int idEquino;

    @ColumnInfo(name = "ID_Prueba")
    public int idPrueba;

    /**
     * Corrección aplicada a la nota (ej: penalización).
     * Valores posibles: 0.0, 2.0 o 4.0
     */
    @ColumnInfo(name = "Correccion")
    public double correccion;

    @ColumnInfo(name = "Eliminado")
    public boolean eliminado;

    /**
     * Constructor vacío requerido por Room.
     */
    public Participacion() {}

    /**
     * Constructor básico para crear una participación inicial.
     */
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