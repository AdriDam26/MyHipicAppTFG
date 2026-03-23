package com.example.myhipicapptfg.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Alumno",
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "ID_Usuario", // columna en Usuario
                childColumns = "ID_Alumno",   // columna en Alumno que referencia
                onDelete = ForeignKey.CASCADE
        )
)
public class Alumno {

    @PrimaryKey
    @ColumnInfo(name = "ID_Alumno")  // nombre exacto de la columna en la tabla
    public int idAlumno;

}