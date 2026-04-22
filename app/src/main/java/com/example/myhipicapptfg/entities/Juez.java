package com.example.myhipicapptfg.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Juez",
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "ID_Usuario",
                childColumns = "ID_Juez",
                onDelete = ForeignKey.CASCADE
        )
)
public class Juez {

    @PrimaryKey
    @ColumnInfo(name = "ID_Juez")
    public int idJuez;

    @ColumnInfo(name = "Numero_Licencia")
    public String numeroLicencia;

    @ColumnInfo(name = "Federacion")
    public String federacion;

    // Constructor vacío
    public Juez() {}

    public Juez(int idJuez, String numeroLicencia, String federacion) {
        this.idJuez = idJuez;
        this.numeroLicencia = numeroLicencia;
        this.federacion = federacion;
    }
}