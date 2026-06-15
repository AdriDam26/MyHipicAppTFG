package com.example.myhipicapptfg.datos.local.entidades;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

/**
 * Entidad NotaMovimiento para la base de datos Room.
 *
 * Representa la nota que un juez asigna a un determinado movimiento
 * dentro de una participación en una prueba.
 *
 * Es una tabla intermedia con atributos adicionales:
 * - Nota (puntuación)
 * - Observación del juez
 */
@Entity(
        tableName = "Nota_Movimiento",
        // Definimos la clave primaria combinando ambos IDs
        primaryKeys = {"ID_Participacion", "ID_Movimiento"},
        foreignKeys = {
                @ForeignKey(
                        entity = Movimiento.class,
                        parentColumns = "ID_Movimiento",
                        childColumns = "ID_Movimiento",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Participacion.class,
                        parentColumns = "ID_Participacion",
                        childColumns = "ID_Participacion",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = {"ID_Movimiento"}),
                @Index(value = {"ID_Participacion"})
        }
)
public class NotaMovimiento {

    /**
     * ID de la participación a la que pertenece la nota.
     */
    @ColumnInfo(name = "ID_Participacion")
    public int idParticipacion;

    @ColumnInfo(name = "ID_Movimiento")
    public int idMovimiento;

    // Atributos
    @ColumnInfo(name = "Nota")
    public double nota;

    @ColumnInfo(name = "Observacion")
    public String observacion;

    /**
     * Constructor vacío requerido por Room.
     */
    public NotaMovimiento() {}

    /**
     * Constructor para crear una evaluación de un movimiento.
     */
    public NotaMovimiento(double nota, String observacion, int idMovimiento, int idParticipacion) {
        this.nota = nota;
        this.observacion = observacion;
        this.idMovimiento = idMovimiento;
        this.idParticipacion = idParticipacion;
    }
}