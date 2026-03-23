package com.example.myhipicapptfg.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Calificacion",
        foreignKeys = {
                @ForeignKey(
                        entity = Reprisa.class,
                        parentColumns = "ID_Reprisa",
                        childColumns = "ID_Reprisa",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Participacion.class,
                        parentColumns = "ID_Participacion",
                        childColumns = "ID_Participacion",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class Calificacion {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_Calificacion")
    public int idCalificacion;

    @ColumnInfo(name = "Nota")
    public double nota; // decimal(5,2) >= 0, not null

    @ColumnInfo(name = "Observacion")
    public String observacion;

    @ColumnInfo(name = "ID_Reprisa")
    public int idReprisa;

    @ColumnInfo(name = "ID_Participacion")
    public int idParticipacion;
}

