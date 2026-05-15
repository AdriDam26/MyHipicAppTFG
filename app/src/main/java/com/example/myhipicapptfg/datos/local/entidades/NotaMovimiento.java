package com.example.myhipicapptfg.datos.local.entidades;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

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

    @ColumnInfo(name = "ID_Participacion")
    public int idParticipacion;

    @ColumnInfo(name = "ID_Movimiento")
    public int idMovimiento;

    @ColumnInfo(name = "Nota")
    public double nota;

    @ColumnInfo(name = "Observacion")
    public String observacion;

    // Nota: Hemos quitado idNotaMovimiento porque la clave ya son los otros dos IDs

    public NotaMovimiento() {}

    public NotaMovimiento(double nota, String observacion, int idMovimiento, int idParticipacion) {
        this.nota = nota;
        this.observacion = observacion;
        this.idMovimiento = idMovimiento;
        this.idParticipacion = idParticipacion;
    }
}