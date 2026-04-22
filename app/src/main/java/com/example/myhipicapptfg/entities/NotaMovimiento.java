package com.example.myhipicapptfg.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Nota_Movimiento",
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
                ),
                @ForeignKey(
                        entity = Juez.class,
                        parentColumns = "ID_Juez",
                        childColumns = "ID_Juez",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class NotaMovimiento {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Nota_Movimiento")
    public int idNotaMovimiento;

    @ColumnInfo(name = "Nota")
    public double nota;

    @ColumnInfo(name = "Observacion")
    public String observacion;

    @ColumnInfo(name = "ID_Movimiento")
    public int idMovimiento;

    @ColumnInfo(name = "ID_Participacion")
    public int idParticipacion;

    @ColumnInfo(name = "ID_Juez")
    public int idJuez;

    // 🔹 Constructor vacío (Room)
    public NotaMovimiento() {}

    // 🔹 Constructor recomendado
    public NotaMovimiento(double nota,
                          String observacion,
                          int idMovimiento,
                          int idParticipacion,
                          int idJuez) {

        this.nota = nota;
        this.observacion = observacion;
        this.idMovimiento = idMovimiento;
        this.idParticipacion = idParticipacion;
        this.idJuez = idJuez;
    }
}