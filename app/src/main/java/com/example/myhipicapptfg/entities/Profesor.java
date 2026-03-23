package com.example.myhipicapptfg.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Profesor",
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "ID_Usuario",
                childColumns = "ID_Profesor",
                onDelete = ForeignKey.CASCADE
        )
)
public class Profesor {

    @PrimaryKey
    @ColumnInfo(name = "ID_Profesor")
    public int idProfesor;

}
