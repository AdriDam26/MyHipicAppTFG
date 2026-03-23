package com.example.myhipicapptfg.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Propietario",
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "ID_Usuario",
                childColumns = "ID_Propietario",
                onDelete = ForeignKey.CASCADE
        )
)
public class Propietario {

    @PrimaryKey
    @ColumnInfo(name = "ID_Propietario")
    public int idPropietario;

}